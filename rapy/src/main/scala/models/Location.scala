package models

object Location extends ModelCompanion[Location] {
  protected def dbTable: DatabaseTable[Location] = Database.locations

  def apply(name: String, coordX: Int, coordY: Int): Location =
    new Location(name, coordX, coordY)

  private[models] def apply(jsonValue: JValue): Location = {
    val value = jsonValue.extract[Location]
    value._id = (jsonValue \ "id").extract[Int]
    value.save()
    value
  }
}

class Location(val name: String, val coordX: Int, val coordY: Int) extends Model[Location] {
  
  protected def dbTable: DatabaseTable[Location] = Location.dbTable

  private val info = Set("name", "coordX", "coordY")

  override def toMap: Map[String, Any] = {
    super.toMap + ("name" -> this.name, "coordX" -> this.coordX, "coordY" -> this.coordY)
  }

  def format: Map[String, Any] = toMap.filterKeys(info)

  def distance(location: Location): Int = {
    math.abs(coordX - location.coordX) + math.abs(coordY - location.coordY)
  }

  override def toString: String = s"Location: $name"
}
