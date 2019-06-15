package models

object Order extends ModelCompanion[Order] {
  protected def dbTable: DatabaseTable[Order] = Database.orders

  def apply(consumer_id: Int, provider_id: Int, items: List[(Int, Int)], total: Float): Order =
    new Order(consumer_id, provider_id, items.unzip._1, items.unzip._2, total)

  private[models] def apply(jsonValue: JValue): Order = {
    val value = jsonValue.extract[Order]
    value._id = (jsonValue \ "id").extract[Int]
    value.save()
    value
  }

}

class Order(val consumer_id: Int, val provider_id: Int, val item_ids: List[Int], 
            val item_amounts: List[Int], val total: Float) extends Model[Order] {
  
  // Database
  protected def dbTable: DatabaseTable[Order] = Order.dbTable

  // Protected information
  private var status = "Not Payed"

  private val info = Set("id", "consumer_id", "consumerUsername", "consumerLocation",
                         "provider_id", "providerStoreName", "total", "status")
                         
  private var commentary = "Not commented Yet"
  private var stars = 0

  override def toMap: Map[String, Any] = {
    super.toMap + ("consumer_id" -> consumer_id, "provider_id" -> provider_id,
                   "item_ids" -> item_ids, "item_amounts" -> item_amounts,
                   "total" -> total, "status" -> status)
  }
  
  def setStatus(newStatus: String): Unit = this.status = newStatus
  def setStars(stars: Int): Unit = this.stars = stars
  def setCommentary(comment: String): Unit = this.commentary = comment

  def format: Map[String, Any] = {
    
    val consumer = Consumer.find(consumer_id).get
    val provider = Provider.find(provider_id).get

    val consumerUsername = consumer.username
    val consumerLocation = Location.find(consumer.location_id).get.name
    val providerStoreName = provider.storeName

    (toMap + ("consumerUsername" -> consumerUsername) +
             ("consumerLocation" -> consumerLocation) +
             ("providerStoreName" -> providerStoreName)).filterKeys(info)
  }

  def detail: List[Map[String, Any]] = {
    val items = item_ids.zip(item_amounts)
    items map {
      case (id, amount) => (Item.find(id).get.toMap.filterKeys(_ != "provider_id") + ("amount" -> amount))
    }
  }

  override def toString: String = s"$status order from $consumer_id to $provider_id with cost $total"
  
}