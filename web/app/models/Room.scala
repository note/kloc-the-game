package models

import akka.actor.ActorSystem
import models.ChessTableState.ChessTableState
import net.michalsitko.kloc.game.{Color, GameStatus, Move}
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json.Reads._
import play.api.libs.json._
import services.{RoomService, UserService}

import scala.concurrent.duration._

class Room (table: ChessTable, roomService: RoomService, userService: UserService, roomId: Int) {
  private val (roomEnumerator, roomChannel) = Concurrent.broadcast[JsValue]

  def join(userId: String, color: Color): (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    val user = userService.getUserById(userId)
    val Connected(enumerator, state) = join(user, color)
    roomService.refreshNeeded()
    val in = Iteratee.foreach[JsValue](event => {
      Logger.debug("websocket received something" + event.toString())
      (event \ "type").as[String] match {
        case "move" =>
          val move = Move((event \ "from").as[String], (event \ "to").as[String])
          applyMove(user, move)
      }
    }).map { _ =>
      leaveRoom(roomId, user)
      roomService.refreshNeeded()
      Logger.debug("Disconnected3")
    }

    if(state == ChessTableState.Started){
      // RoomActor reacts to this message by sending start message to clients
      // we should be sure that client has been already established
      // This is just best effort - it does not guarantee anything
      // TODO: think about alternatives
      Akka.system.scheduler.scheduleOnce(1000 milliseconds){
        started()
      }
    }

    (in, enumerator)
  }

  def getTablesInfo: List[ChessTableInfo] = List(table.getInfo())

  private def join(user: User, color: Color): Connected = {
    table.addPlayer(user, color)
    Connected(roomEnumerator, table.state)
  }

  private def leaveRoom(roomId: Int, user: User) = {
    table.userLeft(user)
    if(table.getPlayers.size == 0){
      roomService.removeRoom(roomId)
    }
  }

  private def started() = {
    val colors = table.getColors
    val startObj = JsObject(Seq("type" -> JsString("start"), "times" -> MoveNotification.mapToJsObject(table.getTimes().toMap), "colors" -> JsObject(colors.map(mapItem => (mapItem._1, JsString(mapItem._2.toString))).toSeq)))
    notifyAll(startObj)
  }

  private def applyMove(user: User, move: Move) = {
    val res = table.move(user, move)
    if(res.isDefined) {
      notifyAll(MoveNotification.toJsObject(MoveNotification(user, move, res.get, table.getTimes().toMap)))
    } else {
      Logger.warn("incorrect move")
    }
  }

  private def notifyAll(msg: JsObject) = {
    roomChannel.push(msg)
  }
}

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

case class ChessTableInfo(white: Option[String], black: Option[String])

object ChessTableInfo {
  import play.api.libs.json._

  implicit val chessTableInfoFormat = Json.format[ChessTableInfo]

}

