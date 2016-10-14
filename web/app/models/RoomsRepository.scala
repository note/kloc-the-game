package models

import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json._


object RoomsRepository {
  import ChessTableInfo._

  // TODO: try to get rid of both of those Writes
  implicit val roomWrites = new Writes[List[ChessTableInfo]] {
    def writes(tables: List[ChessTableInfo]) = JsArray(tables.map(Json.toJson(_)))
  }

  implicit val roomsWrites = new Writes[Map[Int, List[ChessTableInfo]]] {
    def writes(rooms: Map[Int, List[ChessTableInfo]]) = {
      val converted = rooms.map(mapItem => (mapItem._1.toString, Json.toJson(mapItem._2)))
      Json.obj(
        "rooms" -> JsObject(converted.toSeq)
      )
    }
  }

  def getRoomsSocket(): (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    val (actorEnumerator, infoJson) = getIterateeAndEnumerator
    val enumerator = Enumerator[JsValue](infoJson).andThen(actorEnumerator)
    (Iteratee.ignore[JsValue], enumerator)
  }

  def refreshNeeded() = {
    val tablesInfo = Room.getTablesInfo()
    val tablesJson = Json.toJson(tablesInfo)
    chatChannel.push(tablesJson)
  }

  private val (repoEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  private def getIterateeAndEnumerator = {
    val tablesInfo = Room.getTablesInfo()
    val tablesJson = Json.toJson(tablesInfo)
    (repoEnumerator, tablesJson)
  }
}
