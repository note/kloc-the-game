package services

import akka.actor.ActorSystem
import models.{ChessTable, ChessTableInfo, Room}
import play.api.Logger
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json._

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.ExecutionContext

trait RoomService {
  def newRoom(timeLimitInSeconds: Int): Int
  def removeRoom(roomId: Int): Unit
  def getRoomsSocket: (Iteratee[JsValue, Unit], Enumerator[JsValue])
  def getRoomById(id: Int): Option[Room]
  def getRoomNames: Iterable[Int]
  def refreshNeeded(): Unit
}

class InMemoryRoomService (userService: UserService, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends RoomService {
  private val rooms = MutableMap[Int, Room]()
  private var nextId = 0

  private val (repoEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  def newRoom(timeLimitInSeconds: Int): Int = {
    val table = new ChessTable(actorSystem, timeLimitInSeconds * 1000)
    val roomId = useNextId()
    rooms += (roomId -> new Room(table, this, userService, roomId))
    Logger.info(s"Created room with id: $roomId")
    roomId
  }

  def removeRoom(roomId: Int): Unit = rooms.remove(roomId)

  def getRoomsSocket: (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    val (actorEnumerator, infoJson) = getIterateeAndEnumerator
    val enumerator = Enumerator[JsValue](infoJson).andThen(actorEnumerator)
    (Iteratee.ignore[JsValue], enumerator)
  }

  def getRoomById(id: Int): Option[Room] = rooms.get(id)

  def getRoomNames = rooms.keys

  def refreshNeeded(): Unit = {
    val tablesJson = getTablesInfoJson
    chatChannel.push(tablesJson)
  }

  /*** PRIVATE METHODS ***/

  private def getTablesInfo(): Map[Int, List[ChessTableInfo]] =
    rooms.mapValues(_.getTablesInfo).toMap

  private def getIterateeAndEnumerator = {
    val tablesJson = getTablesInfoJson
    (repoEnumerator, tablesJson)
  }

  private def getTablesInfoJson: JsValue = {
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

    val tablesInfo = getTablesInfo()
    Json.toJson(tablesInfo)
  }

  private def useNextId() = {
    val res = nextId
    nextId += 1
    res
  }
}
