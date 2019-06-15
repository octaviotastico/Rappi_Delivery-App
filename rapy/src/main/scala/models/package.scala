package object models {
  private[models] implicit val formats: org.json4s.DefaultFormats = org.json4s.DefaultFormats

  type DatabaseTable[M <: Model[M]] = models.db.DatabaseTable[M]
  val Database: db.Database.type = models.db.Database

  type Dict[K, V] = scala.collection.mutable.Map[K, V]
  val Dict: scala.collection.mutable.Map.type = scala.collection.mutable.Map

  type JValue = org.json4s.JValue
}
