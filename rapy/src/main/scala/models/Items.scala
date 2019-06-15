package models

object Item extends ModelCompanion[Item] {
  protected def dbTable: DatabaseTable[Item] = Database.items

  def apply(name: String, description: String, price: Float, provider_id: Int): Item = {
    new Item(name, description, price, provider_id)
  }

  private[models] def apply(jsonValue: JValue): Item = {
    val value = jsonValue.extract[Item]
    value._id = (jsonValue \ "id").extract[Int]
    value.save()
    value
  }

  def get_id(name: String, provider_id: Int): Int = {
    val list = super.filter(Map(("name" -> name), ("provider_id" -> provider_id)))
    if(list.isEmpty) {
      return 0
    }
    list.head.id
  }
}

class Item(val name: String, val description: String, val price: Float,
            val provider_id: Int) extends Model[Item] {
  
  protected def dbTable: DatabaseTable[Item] = Item.dbTable

  override def toMap: Map[String, Any] = {
    super.toMap + ("name" -> name, "description" -> description,
                   "price" -> price, "provider_id" -> provider_id)
  }

  def format: Map[String, Any] = toMap

  override def toString: String = s"Item: $name, $price, $provider_id"

}
