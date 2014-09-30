package models


import akka.actor.{PoisonPill, ActorRef, Actor, Props}
import net.michalsitko.kloc.game.{GameStatus, Color, Move}
import play.api.libs.iteratee.{Input, Enumerator, Iteratee, Concurrent}
import play.api.libs.json._
import scala.collection.mutable.{Map => MutableMap}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import scala.util.Random
import play.api.libs.json.Reads._
import models.ChessTableState.ChessTableState
import play.api.libs.concurrent.Akka
import scala.concurrent._

class Room (actor: ActorRef, roomId: Int) {
  implicit val timeout = Timeout(1 second)

  def join(userId: String, color: Color) = {
    val user = Room.getUserById(userId)
    (actor ? Join(user, color)).map {
      case Connected(enumerator, state) =>
        RoomsRepository.refreshNeeded()
        val in = Iteratee.foreach[JsValue](event => {
          println("websocket received something" + event.toString())
          (event \ "type").as[String] match {
            case "move" =>
              actor ! MoveMessage(user, Move((event \ "from").as[String], (event \ "to").as[String]))
          }
        }).map { _ =>
          println("Disconnected")
          actor ! RoomLeft(roomId, user)
          println("Disconnected2")
          RoomsRepository.refreshNeeded()
          println("Disconnected3")
        }

        if(state == ChessTableState.Started){
          // RoomActor reacts to this message by sending start message to clients
          // we should be sure that client has been already established
          // This is just best effort - it does not guarantee anything
          // TODO: think about alternatives
          Akka.system.scheduler.scheduleOnce(1000 milliseconds){
            actor ! Started()
          }
        }

        Right((in, enumerator))
      case _ =>
        Right((Iteratee.ignore[JsValue], Enumerator[JsValue](JsObject(Seq("error" -> JsString("cannot connect to room")))).andThen(Enumerator.enumInput(Input.EOF))))
    }
  }

  def getTablesInfo(): List[ChessTableInfo] = {
    Await.result((actor ? GetTablesInfo()).map {
      case res: List[ChessTableInfo] => res
      case _ => List[ChessTableInfo]()
    }, 1000 milliseconds)
  }
}

object Room {
  val rooms = MutableMap[Int, Room]()
  var nextId = 0;
  val users = MutableMap[String, User]()
  val rand = new Random(System.currentTimeMillis())
  println("creating room")

  def newRoom() = {
    val table = new ChessTable(120 * 60 * 1000)
    val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
    val roomId = useNextId()
    rooms += (roomId -> new Room(roomActor, roomId))
    roomId
  }

  def removeRoom(roomId: Int) = {
    rooms.remove(roomId)
  }

  def useNextId() = {
    val res = nextId
    nextId += 1
    res
  }

  def getRoomById(id: Int) = rooms.get(id)

  def getRoomNames() = rooms.keys

  def getTablesInfo(): Map[Int, List[ChessTableInfo]] = rooms.map(mapItem => (mapItem._1, mapItem._2.getTablesInfo())).toMap

  def registerUser(userName: String) = {
    val userId = rand.alphanumeric.take(20).foldLeft("")((res, nextChar) => res + nextChar)
    users += (userId -> User(userName, userId))
    userId
  }

  def existsUserId(userId: String) = {
    users.contains(userId)
  }

  def getUserById(userId: String): User = {
    users(userId)
  }
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
    case Started() =>
      val colors = table.getColors
      val startObj = JsObject(Seq("type" -> JsString("start"), "times" -> MoveNotification.mapToJsObject(table.getTimes().toMap), "colors" -> JsObject(colors.map(mapItem => (mapItem._1, JsString(mapItem._2.toString))).toSeq)))
      notifyAll(startObj)
    case MoveMessage(user, move) =>
      val res = table.move(user, move)
      if(res.isDefined){
        notifyAll(MoveNotification.toJsObject(MoveNotification(user, move, res.get, table.getTimes().toMap)))
      }else{
        println("bazinga incorrect move")
      }
    case GetTablesInfo() =>
      val tablesInfo = table.getInfo()
      sender() ! List[ChessTableInfo](tablesInfo)
    case _ =>
      println("bazinga RoomActor.receive unknown message")
  }

  override def postStop() {
    println("bazinga stopped actor")
  }

  private def notifyAll(msg: JsObject) = {
    roomChannel.push(msg)
  }
}


class ClientMessage(user: User)

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

case class Started()

case class GetTablesInfo()

case class ChessTableInfo(whitePlayerName: Option[String], blackPlayerName: Option[String])

