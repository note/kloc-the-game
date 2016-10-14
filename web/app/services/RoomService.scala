package services

import akka.actor.Props
import models.{RoomActor, Room, ChessTable, ChessTableInfo}
import play.api.Logger
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json._

import scala.collection.mutable.{Map => MutableMap}

// TODO: get rid of this (inject EC via DI instead)
import scala.concurrent.ExecutionContext.Implicits.global

// TODO: get rid of this
import play.api.Play.current

trait RoomService {
  def newRoom(timeLimitInSeconds: Int): Int
  def removeRoom(roomId: Int): Unit
  def getRoomsSocket: (Iteratee[JsValue, Unit], Enumerator[JsValue])
  def getRoomById(id: Int): Option[Room]
  def getRoomNames: Iterable[Int]
}

object InMemoryRoomService extends RoomService {
  val rooms = MutableMap[Int, Room]()
  var nextId = 0

  // TODO: it's just temporary, when Room is refactored to class it will be injected
  val userService = new InMemoryUserService

  private val (repoEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  def newRoom(timeLimitInSeconds: Int): Int = {
    val table = new ChessTable(timeLimitInSeconds * 1000)
    val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
    val roomId = useNextId()
    rooms += (roomId -> new Room(roomActor, userService, roomId))
    Logger.info(s"Created room with id: $roomId")
    roomId
  }

  def removeRoom(roomId: Int): Unit = {
    rooms.remove(roomId)
  }

  def getRoomsSocket: (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    val (actorEnumerator, infoJson) = getIterateeAndEnumerator
    val enumerator = Enumerator[JsValue](infoJson).andThen(actorEnumerator)
    (Iteratee.ignore[JsValue], enumerator)
  }

  def getRoomById(id: Int): Option[Room] = rooms.get(id)

  def getRoomNames = rooms.keys

  private def getTablesInfo(): Map[Int, List[ChessTableInfo]] =
    rooms.map(mapItem => (mapItem._1, mapItem._2.getTablesInfo())).toMap

  def refreshNeeded() = {
    val tablesJson = getTablesInfoJson
    chatChannel.push(tablesJson)
  }

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
