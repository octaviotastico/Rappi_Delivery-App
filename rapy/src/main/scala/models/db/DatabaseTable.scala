package models.db

import scala.collection.mutable.{Map => Dict}

import models.Model

class DatabaseTable[M <: Model[M]](val filename: String) {
  private val _instances: Dict[Int, M] = Dict()

  private[models] def getNextId: Int = if (_instances.isEmpty) 1 else _instances.keys.max + 1

  def instances: Map[Int, M] = _instances.toMap

  def delete(id: Int): Unit = {
    _instances.remove(id)
    Database.saveDatabaseTable(this)
  }

  def save(instance: M): Unit = {
    _instances(instance.id) = instance
    Database.saveDatabaseTable(this)
  }

  def update(instance: M): Unit = {
    delete(instance.id)
    save(instance)
  }
}

