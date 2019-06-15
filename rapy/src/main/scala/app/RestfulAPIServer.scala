package app

import cask._
import models._
import upickle.default.{ReadWriter => RW, macroRW}

case class Requested(name: String, amount: Int)
object Requested {
  implicit val rw: RW[Requested] = macroRW
}

object RestfulAPIServer extends MainRoutes  {
  override def host: String = "0.0.0.0"
  override def port: Int = 4000

  @get("/")
  def root(): Response = {
    JSONResponse("Ok")
  }

  ////////// Locations functions //////////

  @get("/api/locations")
  def locations(): Response = {
    JSONResponse(Location.all.map(location => location.format))
  }

  @postJson("/api/locations")
  def locations(name: String, coordX: Int, coordY: Int): Response = {
    if (Location.exists("name", name)) {
      return JSONResponse("existing location name", 409)
    }
    val location = Location(name, coordX, coordY)
    location.save()
    JSONResponse(location.id)
  }

  ////////// Consumers functions //////////

  @get("/api/consumers")
  def consumers(locationName: String = ""): Response = {
    // If no argument supplied
    if(locationName == "") {
      return JSONResponse(Consumer.all.map(consumer => consumer.format))
    }
    // Filter also by location id
    val location_id = Location.get_id("name", locationName) 
    val consumers = Consumer.filter(Map(("location_id" -> location_id)))
    JSONResponse(consumers.map(user => user.format))
  }

  @postJson("/api/consumers")
  def consumers(username: String, locationName: String): Response = {
    if(User.exists("username", username)) {
      return JSONResponse("existing username", 409)
    }
    val location_id = Location.get_id("name", locationName)
    if(location_id == 0) {
      return JSONResponse("non existing location", 404)
    }
    val consumer = Consumer(username, location_id)
    consumer.save()
    JSONResponse(consumer.id)
  }

  ////////// Providers functions //////////

  @get("/api/providers/")
  def providers(locationName: String = ""): Response = {
    // If no argument supplied
    if(locationName == "") {
      return JSONResponse(Provider.all.map(provider => provider.format))
    }
    // Filter also by location id
    val location_id = Location.get_id("name", locationName)
    if(location_id == 0) {
      return JSONResponse("non existing location", 404)
    }
    val providers = Provider.filter(Map("location_id" -> location_id))
    JSONResponse(providers.map(provider => provider.format))
  }

  @postJson("/api/providers")
  def providers(username: String, storeName: String, locationName: String,
                maxDeliveryDistance: Int): Response = {
    if(User.exists("username", username)) {
      return JSONResponse("existing username", 409)
    }
    val shops = Provider.filter(Map("storeName" -> storeName))
    if(!shops.isEmpty) {
      return JSONResponse("existing storeName", 409)
    }
    val location_id = Location.get_id("name", locationName)
    if(location_id == 0) {
      return JSONResponse("non existing location", 404)
    }
    if(maxDeliveryDistance < 0) {
      return JSONResponse("negative maxDeliveryDistance", 400)
    }
    val provider = Provider(username, location_id, storeName, maxDeliveryDistance)
    
    provider.save()
    JSONResponse(provider.id)
  }

  ////////// Users functions //////////

  @post("/api/users/delete/:username")
  def users(username: String): Response = {
    val user_id = User.get_id("username", username)
    if(user_id == 0) {
      return JSONResponse("non existing user", 404)
    }
    User.delete(user_id)
    JSONResponse("Ok", 200)
  }

  ////////// Items functions //////////

  @get("/api/items/")
  def items(providerUsername: String = ""): Response = {
    if(providerUsername == "") {
      return JSONResponse(Item.all.map(item => item.format))
    }
    val provider_id = Provider.get_id("username", providerUsername)
    if(provider_id == 0) {
      return JSONResponse("non existing provider", 404)
    }
    val provider_items = Item.filter(Map("provider_id" -> provider_id))

    JSONResponse(provider_items.map(items => items.format))
  }

  @postJson("/api/items/")
  def items(name: String, description: String, price: Float,
            providerUsername: String): Response = {
    
    val provider_id = Provider.get_id("username", providerUsername)
    
    if(provider_id == 0) {
      return JSONResponse("non existing provider", 404)
    }
    
    val item_list = Item.filter(Map(("name" -> name),
                    ("provider_id" -> provider_id)))
    
    if(item_list.nonEmpty) {
      return JSONResponse("existing item for provider", 409)
    }

    if(price < 0) {
      return JSONResponse("negative price", 400)
    }

    val item = Item(name, description, price, provider_id)
    item.save()
    JSONResponse(item.id)
  }

  @post("/api/items/delete/:id")
  def items_delete(id: Int): Response = {
    if(Item.find(id) == None) {
      return JSONResponse("non existing item", 404)
    }
    Item.delete(id)
    JSONResponse("Ok")
  }

    ////////// Reviews functions //////////

  @get("/api/reviews/")
  def review(username: String = ""): Response = {

    if(username == "") {
      return JSONResponse(Review.all.map(review => review.format))
    }

    val user_id = User.get_id("username", username)
    if(user_id == 0) {
      return JSONResponse("non existing user", 404)
    }

    val review = Review.find(user_id)

    if(review == None) {
      return JSONResponse("non existing reviews from this user", 404)
    }

    JSONResponse(review.get.format)

  }

  @postJson("/api/reviews/")
  def review(consumerUsername: String, providerUsername: String,
             order_comment: String, order_star: Int,
             provider_comment: String, provider_star: Int,
             order_id: Int): Response = {

    // Get the id's with the names
    val consumer_id = Consumer.get_id("username", consumerUsername)
    val provider_id = Provider.get_id("username", providerUsername)

    // Check if provider exists
    if(provider_id == 0) {
      return JSONResponse("non existing provider", 404)
    }

    // Check if consumer exists
    if(consumer_id == 0) {
      return JSONResponse("non existing consumer", 404)
    }

    // Check if the given stars amount is within 1 and 10
    if(order_star < 1 || order_star > 10 ||
       provider_star < 1 || provider_star > 10) {
      return JSONResponse("stars should be between 1 and 10", 409)
    }

    // Get the order with it's id
    val order = Order.find(order_id)

    // Check if the order exists
    if(order == None) {
      return JSONResponse("non existing order", 404)
    }

    // Get the provider with it's id
    val provider = Provider.find(provider_id).get

    println(provider_id)
    println(provider)

    // Update all provider values
    val star_sum = provider.getStarsSum + provider_star
    provider.setCommentary(provider_comment)
    provider.setStars(provider_star)
    provider.setReputation(star_sum / (provider.getStarsLength + 1))
    provider.update()

    // Update all order values
    order.get.setStars(order_star)
    order.get.setCommentary(order_comment)
    order.get.setStatus("finished")
    order.get.update()

    // Create the new Review
    val review = Review(consumer_id, provider_id, order_id, order_star,
                        provider_star, order_comment, provider_comment)
    
    review.save()

    JSONResponse(review.id)
  }

  @post("/api/reviews/delete/:id")
  def reviewsDelete(id: Int): Response = {
    if(Review.find(id) == None) {
      return JSONResponse("non existing review", 404)
    }
    Review.delete(id)
    JSONResponse("Ok")
  }

  ////////// Order functions //////////

  @get("/api/orders/")
  def orders(username: String): Response = {
    val user_id = User.get_id("username", username)
    if(user_id == 0) {
      return JSONResponse("non existing user", 404)
    }
    val provider_order = Order.filter(Map("provider_id" -> user_id))
    val consumer_order = Order.filter(Map("consumer_id" -> user_id))
    val orders = consumer_order ++ provider_order
    if(orders.isEmpty) {
      return JSONResponse("non existing orders from user", 404)
    }
    JSONResponse(orders.map(order => order.format))
  }

  @get("/api/orders/detail/:id")
  def orders(id: Int): Response = {
    val order = Order.find(id)
    
    if(order == None) {
      return JSONResponse("non existing order", 404)
    }
    JSONResponse(order.get.detail)
  }

  @postJson("/api/orders")
  def orders(providerUsername: String, consumerUsername: String,
             items: List[Requested]): Response = {

    val provider_id = Provider.get_id("username", providerUsername)
    val consumer_id = Consumer.get_id("username", consumerUsername)

    if(provider_id == 0) {
      return JSONResponse("non existing provider", 404)
    }

    if(consumer_id == 0) {
      return JSONResponse("non existing consumer", 404)
    }

    // List of (Int, Int) which represents (Id of item, amount of item)
    val item_list = items.map(req => (Item.get_id(req.name, provider_id), req.amount))

    // Filtering the list on search of negative amount
    val item_list_filtered = item_list filter { case (id, amount) => amount > 0}

    if(item_list.length != item_list_filtered.length) {
      return JSONResponse("negative amount on item", 409)
    }

    // Filtering the list in search of non existing items (id == 0)
    val item_list_filtered2 = item_list filter {case (id, amount) => id != 0}

    if(item_list.length != item_list_filtered2.length) {
      return JSONResponse("non existing item for provider", 404)
    }

    // Calculate total
    val total = item_list.foldLeft(0.0f) {(l, x) => l + Item.find(x._1).get.price * x._2}

    // Get the provider and consumer
    val provider = Provider.find(provider_id).get
    val consumer = Consumer.find(consumer_id).get

    // Get the locations and check if the order is deliverable
    val locationProvider = Location.find(provider.location_id).get
    val locationConsumer = Location.find(consumer.location_id).get

    val distance = locationProvider.distance(locationConsumer)

    if(distance.toFloat > provider.maxDeliveryDistance.get) {
      return JSONResponse("not in range for delivery", 410)
    }

    // Create order and make the balance changes
    val order = Order(consumer_id, provider_id, item_list, total)

    consumer.pay(total)
    provider.pay(-total)
    consumer.update()
    provider.update()

    order.setStatus("Payed")

    order.save()
    JSONResponse(order.id)
  }

  @post("/api/orders/delete/:id")
  def ordersDelete(id: Int): Response = {
    if(Order.find(id) == None) {
      return JSONResponse("non existing order", 404)
    }
    Order.delete(id)
    JSONResponse("Ok")
  }

  @post("/api/orders/deliver/:id")
  def ordersDeliver(id: Int): Response = {
    val order = Order.find(id)
    if(order == None) {
      return JSONResponse("non existing order", 404)
    }
    order.get.setStatus("Delivered")
    order.get.update()
    JSONResponse("Ok")
  }

  override def main(args: Array[String]): Unit = {
    System.err.println("\n " + "=" * 39)
    System.err.println(s"| Server running at http://$host:$port ")

    if (args.length > 0) {
      val databaseDir = args(0)
      Database.loadDatabase(databaseDir)
      System.err.println(s"| Using database directory $databaseDir ")
    } else {
      Database.loadDatabase()  // Use default location
    }
    System.err.println(" " + "=" * 39 + "\n")

    super.main(args)
  }

  initialize()
}
