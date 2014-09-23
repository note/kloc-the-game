package models


import akka.actor.{ActorRef, Actor, Props}
import net.michalsitko.kloc.game.{GameStatus, Color, Move}
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Input, Enumerator, Iteratee, Concurrent}
import play.api.libs.json._
import scala.collection.mutable.Map
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import models.MoveMessage
import models.Connected
import models.Join
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import scala.util.Random
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json

class Room (actor: ActorRef) {
  implicit val timeout = Timeout(1 second)

  // TODO: probably to remove
//  implicit val userReads: Reads[User] = (
//    (JsPath \ "userId").read[String]
//    )(Room.getUserById _)
//
//  implicit val moveReads: Reads[Move] = (
//    (JsPath \ "from").read[String] and
//      (JsPath \ "to").read[String]
//    )(Move.apply _)
//
//  implicit val moveMessage: Reads[MoveMessage] = (
//    (JsPath).read[User] and
//      (JsPath).read(Move)
//    )(MoveMessage.apply _)

  def join(userId: String, color: Color) = {
    val user = Room.getUserById(userId)
    (actor ? Join(user, color)).map {
      case Connected(enumerator) =>
        val in = Iteratee.foreach[JsValue](event => {
          println("websocket received something" + event.toString())
          (event \ "type").as[String] match {
            case "move" =>
              actor ! MoveMessage(user, Move((event \ "from").as[String], (event \ "to").as[String]))
            case "start" =>
              actor ! Start(user)
          }
        }).map { _ =>
          println("Disconnected")
        }

        Right((in, enumerator))
      case _ =>
        Right((Iteratee.ignore[JsValue], Enumerator[JsValue](JsObject(Seq("error" -> JsString("cannot connect to room")))).andThen(Enumerator.enumInput(Input.EOF))))
    }
  }
}

object Room {
  var rooms = Map[Int, Room]()
  var nextId = 0;
  var users = Map[String, User]()
  val rand = new Random(System.currentTimeMillis())
  println("creating room")

  def newRoom(name: String) = {
    val table = new ChessTable(120 * 60 * 1000)
    val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
    val roomId = useNextId()
    rooms += (roomId -> new Room(roomActor))
    roomId
  }

  def useNextId() = {
    val res = nextId
    nextId += 1
    res
  }

  def getRoomById(id: Int) = rooms.get(id)

  def getRoomNames() = rooms.keys

  def registerUser(userName: String) = {
    val userId = rand.alphanumeric.take(20).foldLeft("")((res, nextChar) => res + nextChar)
    users += (userId -> User(userName, userId))
    userId
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
      sender() ! Connected(roomEnumerator)
    case Start(user) =>
      table.requestStart(user)
    case MoveMessage(user, move) =>
      val res = table.move(user, move)
      if(res.isDefined){
        notifyAll(MoveNotification(user, move, res.get, table.getTimes()))
      }else{
        println("bazinga incorrect move")
      }
    case _ =>
      println("bazinga RoomActor.receive unknown message")
  }

  override def postStop() {
    println("bazinga stopped actor")
  }

  private def notifyAll(moveMessage: MoveNotification) = {
    def mapTo(m: Map[String, Long]): JsObject = {
      JsObject(m.map(mapItem => (mapItem._1, JsNumber(mapItem._2))).toSeq)
    }

    // TODO: add toJson method to Move (or something else and more idiomatic)
    val msg = JsObject(
      Seq(
        "from" -> JsString(moveMessage.move.from.toString),
        "to" -> JsString(moveMessage.move.to.toString),
        "result" -> JsString(moveMessage.gameStatus.toString),
        "times" -> mapTo(moveMessage.playersMillisecondsLeft)
      )
    )
    roomChannel.push(msg)
  }
}


class ClientMessage(user: User)

case class Join(user: User, color: Color)
case class Start(user: User)
case class MoveMessage(user: User, move: Move)
case class MoveNotification(user: User, move: Move, gameStatus: GameStatus, playersMillisecondsLeft: Map[String, Long])

case class Connected(enumerator: Enumerator[JsValue])


