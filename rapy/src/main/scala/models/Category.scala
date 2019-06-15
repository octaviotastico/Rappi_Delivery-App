package models

object Consumer extends ModelCompanion[User] {
  
  protected def dbTable: DatabaseTable[User] = Database.users

  def apply(username: String, location_id: Int): User = {
    new User(username, location_id, "Consumer", None, None)
  }

  private[models] def apply(jsonValue: JValue): User = {
    val value = jsonValue.extract[User]
    value.save()
    value
  }
  
  override def all: List[User] = {
    super.all.filter(x => x.category == "Consumer")
  }

  override def find(id: Int): Option[User] = {
    val consumer = super.find(id)
    consumer match {
      case Some(c) =>  if(c.category == "Consumer") Some(c) else None
      case None => None
    }
  }

  override def exists(attr: String, value: Any): Boolean = {
    val consumers = this.all
    val consumer = consumers.filter(x => x.toMap(attr) == value)
    !(consumer.isEmpty)
  }
  
  override def filter(mapOfAttributes: Map[String, Any]): List[User] = {
    all.filter(x => mapOfAttributes.foldLeft(true) {(f, y) => f && x.toMap(y._1) == y._2})
  }
  
  override def get_id(attr: String, value: Any): Int = {
    val consumer = this.all
    val list = consumer.filter(x => x.toMap(attr) == value)
    if(list.isEmpty) {
      return 0
    }
    list.head.id
  }

}

object Provider extends ModelCompanion[User] {
  
  protected def dbTable: DatabaseTable[User] = Database.users
  def apply(username: String, location_id: Int, storeName: String,
            maxDeliveryDistance: Int): User = {
    new User(username, location_id, "Provider", Some(storeName), Some(maxDeliveryDistance))
  }

  private[models] def apply(jsonValue: JValue): User = {
    val value = jsonValue.extract[User]
    value.save()
    value
  }

  override def all: List[User] = {
    super.all.filter(x => x.category == "Provider")
  }

  override def find(id: Int): Option[User] = {
    val provider = super.find(id)
    provider match {
      case Some(c) =>  if(c.category == "Provider") Some(c) else None
      case None => None
    }
  }
  
  override def exists(attr: String, value: Any): Boolean = {
    val providers = this.all
    val provider = providers.filter(x => x.toMap(attr) == value)
    !(provider.isEmpty)
  }
  
  override def filter(mapOfAttributes: Map[String, Any]): List[User] = {
    all.filter(x => mapOfAttributes.foldLeft(true) {(f, y) => f && (x.toMap(y._1) == y._2 || x.toMap(y._1) == Some(y._2))})
  }
  
  override def get_id(attr: String, value: Any): Int = {
    val providers = this.all
    val list = providers.filter(x => x.toMap(attr) == value)
    if(list.isEmpty) {
      return 0
    }
    list.head.id
  }

}