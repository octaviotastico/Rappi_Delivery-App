package models

object User extends ModelCompanion[User] {
  
  protected def dbTable: DatabaseTable[User] = Database.users
  def apply(username: String, location_id: Int,
            category: String, storeName: Option[String],
            maxDeliveryDistance: Option[Int]): User = {
    new User(username, location_id, category, storeName, maxDeliveryDistance)
  }

  private[models] def apply(jsonValue: JValue): User = {
    val value = jsonValue.extract[User]
    value._id = (jsonValue \ "id").extract[Int]
    value.balance = (jsonValue \ "balance").extract[Int]
    value.save()
    value
  }
}

class User(val username: String, val location_id: Int,
           val category: String, val storeName: Option[String],
           val maxDeliveryDistance: Option[Int]) extends Model[User] {

  // Database
  protected def dbTable: DatabaseTable[User] = User.dbTable

  // Protected information
  private var balance: Float = 0
  
  // Non protected information
  var commentaries = List[String]()
  var stars = List[Int]()
  var reputation = 0

  // Functions used for reviews
  def setStars(star: Int): Unit = this.stars :+ star
  def setCommentary(comment: String): Unit = this.commentaries ++ comment
  def setReputation(reputation: Int): Unit = this.reputation = reputation
  def getStarsLength: Int = this.stars.length
  def pay(total: Float): Unit = this.balance = this.balance - total
  def getStarsSum: Int = {

    if(this.stars.isEmpty) return 0
    else if (this.stars.length == 1) return this.stars.head
    else return this.stars.reduceLeft[Int](_+_)

  }

  // Showable information
  private val info = Set("id", "username", "location_id",
                         "storeName", "maxDeliveryDistance")

  override def toMap: Map[String, Any] = {
    category match {
      case "Consumer" => super.toMap + ("username" -> username,
                         "location_id" -> location_id,
                         "category" -> category, "balance" -> balance)
                         
      case "Provider" => super.toMap + ("username" -> username,
                         "location_id" -> location_id,
                         "category" -> category, "storeName" -> storeName,
                         "maxDeliveryDistance" -> maxDeliveryDistance,
                         "balance" -> balance)
    } 
  }

  def format: Map[String, Any] = toMap.filterKeys(info)
  override def toString: String = s"$category: $username"
}


