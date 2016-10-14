package services

import actors.ListRoomActor.RoomsUpdated
import akka.actor.ActorSystem
import models.{ChessTable, ChessTableInfo, Room}
import play.api.Logger

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.ExecutionContext

trait RoomService {
  def newRoom(timeLimitInSeconds: Int): Int
  def removeRoom(roomId: Int): Unit
  def getRoomById(id: Int): Option[Room]
  def getRoomNames: Iterable[Int]
  def refreshNeeded(): Unit
  def getTablesInfo(): Map[Int, List[ChessTableInfo]]
}

class InMemoryRoomService (userService: UserService, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends RoomService {
  private val rooms = MutableMap[Int, Room]()
  private var nextId = 0

  def newRoom(timeLimitInSeconds: Int): Int = {
    val table = new ChessTable(actorSystem, timeLimitInSeconds * 1000)
    val roomId = useNextId()
    rooms += (roomId -> new Room(table, this, userService, roomId))
    Logger.info(s"Created room with id: $roomId")
    roomId
  }

  def removeRoom(roomId: Int): Unit = rooms.remove(roomId)

  def getRoomById(id: Int): Option[Room] = rooms.get(id)

  def getRoomNames = rooms.keys

  def refreshNeeded(): Unit = {
    val updateMsg = RoomsUpdated(getTablesInfo)
    actorSystem.eventStream.publish(updateMsg)
  }

  /*** PRIVATE METHODS ***/

  def getTablesInfo(): Map[Int, List[ChessTableInfo]] =
    rooms.mapValues(_.getTablesInfo).toMap

  private def useNextId() = {
    val res = nextId
    nextId += 1
    res
  }
}
