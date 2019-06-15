package models

trait ModelCompanion[M <: Model[M]] {

  protected def dbTable: DatabaseTable[M]
  private[models] def apply(jsonValue: JValue): M

  def all: List[M] = {
    dbTable.instances.values.toList
  }

  def find(id: Int): Option[M] = {
    dbTable.instances.get(id)
  }
  
  def exists(attr: String, value: Any): Boolean = {
    all.foldLeft(false) {(f, x) => f || x.toMap(attr) == value}
  }
  
  def delete(id: Int): Unit = { 
    dbTable.delete(id)
  }
  
  def filter(mapOfAttributes: Map[String, Any]): List[M] = {
    all.filter(x => mapOfAttributes.foldLeft(true)
                        {(f, y) => f && x.toMap(y._1) == y._2})
  }
  
  def get_id(attr: String, value: Any): Int = {
    val list = filter(Map((attr -> value)))
    if(list.isEmpty) {
      return 0
    }
    list.head.id
  }

}

trait Model[M <: Model[M]] { self: M =>
  protected var _id: Int = 0

  def id: Int = _id

  protected def dbTable: DatabaseTable[M]

  def toMap: Map[String, Any] = Map("id" -> _id)

  def save(): Unit = {
    if (_id == 0) { _id = dbTable.getNextId }
    dbTable.save(this)
  }

  def update(): Unit = {
    dbTable.update(this)
  }
  
}
