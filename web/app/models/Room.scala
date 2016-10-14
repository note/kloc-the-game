package models

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.ChessTableState.ChessTableState
import net.michalsitko.kloc.game.{Color, GameStatus, Move}
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumerator, Input, Iteratee}
import play.api.libs.json.Reads._
import play.api.libs.json.{JsObject, JsString, _}
import services.{InMemoryUserService, UserService}

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent._
import scala.concurrent.duration._

class Room (roomActor: ActorRef, userService: UserService, roomId: Int) {
  implicit val timeout = Timeout(1 second)

  def join(userId: String, color: Color) = {
    val user = userService.getUserById(userId)
    (roomActor ? Join(user, color)).map {
      case Connected(enumerator, state) =>
        RoomsRepository.refreshNeeded()
        val in = Iteratee.foreach[JsValue](event => {
          Logger.debug("websocket received something" + event.toString())
          (event \ "type").as[String] match {
            case "move" =>
              roomActor ! MoveMessage(user, Move((event \ "from").as[String], (event \ "to").as[String]))
          }
        }).map { _ =>
          roomActor ! RoomLeft(roomId, user)
          RoomsRepository.refreshNeeded()
          Logger.debug("Disconnected3")
        }

        if(state == ChessTableState.Started){
          // RoomActor reacts to this message by sending start message to clients
          // we should be sure that client has been already established
          // This is just best effort - it does not guarantee anything
          // TODO: think about alternatives
          Akka.system.scheduler.scheduleOnce(1000 milliseconds){
            roomActor ! Started
          }
        }

        Right((in, enumerator))
      case _ =>
        Right((Iteratee.ignore[JsValue], Enumerator[JsValue](JsObject(Seq("error" -> JsString("cannot connect to room")))).andThen(Enumerator.enumInput(Input.EOF))))
    }
  }

  def getTablesInfo(): List[ChessTableInfo] = {
    Await.result((roomActor ? GetTablesInfo).map {
      case res: List[ChessTableInfo] => res
      case _ => List[ChessTableInfo]()
    }, 1000 milliseconds)
  }
}

object Room {
  val rooms = MutableMap[Int, Room]()
  var nextId = 0

  // TODO: it's just temporary, when Room is refactored to class it will be injected
  val userService = new InMemoryUserService

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

  private def useNextId() = {
    val res = nextId
    nextId += 1
    res
  }

  def getRoomById(id: Int) = rooms.get(id)

  def getRoomNames() = rooms.keys

  def getTablesInfo(): Map[Int, List[ChessTableInfo]] = rooms.map(mapItem => (mapItem._1, mapItem._2.getTablesInfo())).toMap
}

class RoomActor(table: ChessTable) extends Actor {
  val (roomEnumerator, roomChannel) = Concurrent.broadcast[JsValue]

  override def receive: Actor.Receive = {
    case Join(user, color) =>
      table.addPlayer(user, color)
      sender() ! Connected(roomEnumerator, table.state)
    case RoomLeft(roomId, user) =>
      table.userLeft(user)
      if(table.getPlayers.size == 0){
        Room.removeRoom(roomId)
        self ! PoisonPill
      }
    case Started =>
      val colors = table.getColors
      val startObj = JsObject(Seq("type" -> JsString("start"), "times" -> MoveNotification.mapToJsObject(table.getTimes().toMap), "colors" -> JsObject(colors.map(mapItem => (mapItem._1, JsString(mapItem._2.toString))).toSeq)))
      notifyAll(startObj)
    case MoveMessage(user, move) =>
      val res = table.move(user, move)
      if(res.isDefined) {
        notifyAll(MoveNotification.toJsObject(MoveNotification(user, move, res.get, table.getTimes().toMap)))
      } else {
        Logger.warn("incorrect move")
      }
    case GetTablesInfo =>
      val tablesInfo = table.getInfo()
      sender() ! List[ChessTableInfo](tablesInfo)
    case _ =>
      Logger.error("RoomActor received unknown message")
  }

  override def postStop() {
    Logger.debug("stopped actor RoomActor")
  }

  private def notifyAll(msg: JsObject) = {
    roomChannel.push(msg)
  }
}

case class Join(user: User, color: Color)
case class RoomLeft(roomId: Int, user: User)
case class MoveMessage(user: User, move: Move)


case class MoveNotification(user: User, move: Move, gameStatus: GameStatus, playersMillisecondsLeft: Map[String, Long])
object MoveNotification{
  def mapToJsObject(m: Map[String, Long]): JsObject = {
    JsObject(m.map(mapItem => (mapItem._1, JsNumber(mapItem._2))).toSeq)
  }

  def toJsObject(moveNotification: MoveNotification): JsObject = {
    JsObject(
      Seq(
      "type" -> JsString("move"),
      "from" -> JsString(moveNotification.move.from.toString),
      "to" -> JsString(moveNotification.move.to.toString),
      "result" -> JsString(moveNotification.gameStatus.toString),
      "times" -> mapToJsObject(moveNotification.playersMillisecondsLeft),
      "userId" -> JsString(moveNotification.user.id)
      )
    )
  }
}

case class Connected(enumerator: Enumerator[JsValue], tableState: ChessTableState)

case object Started

case object GetTablesInfo

case class ChessTableInfo(white: Option[String], black: Option[String])

object ChessTableInfo {
  import play.api.libs.json._

  implicit val chessTableInfoFormat = Json.format[ChessTableInfo]

}

