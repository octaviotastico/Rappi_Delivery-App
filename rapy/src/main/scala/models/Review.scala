package models

object Review extends ModelCompanion[Review] {
  
  protected def dbTable: DatabaseTable[Review] = Database.review
  def apply(consumer_id: Int, provider_id: Int, order_id: Int,
            order_stars: Int, provider_stars: Int,
            order_comment: String, provider_comment: String): Review = {

    new Review(consumer_id, provider_id, order_id, order_stars,
               provider_stars, order_comment, provider_comment)
  }

  private[models] def apply(jsonValue: JValue): Review = {
    val value = jsonValue.extract[Review]
    value._id = (jsonValue \ "id").extract[Int]
    value.save()
    value
  }

}

class Review(val consumer_id: Int, val provider_id: Int, val order_id: Int,
             val order_stars: Int, val provider_stars: Int,
             val order_comment: String, val provider_comment: String) extends Model[Review] {
  
  // Database
  protected def dbTable: DatabaseTable[Review] = Review.dbTable

  // Protected information
  private var commentary = "Not commented Yet"

  override def toMap: Map[String, Any] = {
    super.toMap + ("consumer_id" -> consumer_id, "provider_id" -> provider_id,
                   "order_stars" -> order_stars, "provider_stars" -> provider_stars,
                   "order_comment" -> order_comment, "provider_comment" -> provider_comment,
                   "order_id" -> order_id)
  }
  
  def format: Map[String, Any] = {
    
    val consumer = Consumer.find(consumer_id).get
    val provider = Provider.find(provider_id).get

    val consumerUsername = consumer.username
    val consumerLocation = Location.find(consumer.location_id).get.name
    val providerStoreName = provider.storeName

    toMap + ("consumerUsername" -> consumerUsername) +
            ("consumerLocation" -> consumerLocation) +
            ("providerStoreName" -> providerStoreName)
  }

  override def toString: String = {
    s"""$consumer_id commented $provider_comment and
    gave $provider_id $provider_stars stars, also
    commented $order_comment and gave $order_stars
    stars to the order"""
  }
}