package controllers

import actors.ListRoomActor
import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.stream.{KillSwitches, Materializer}
import net.michalsitko.kloc.game.Color
import play.api.Logger
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, Controller, Result, WebSocket}
import services.RoomService

import scala.concurrent.Future

class RoomController (roomService: RoomService)(implicit actorSystem: ActorSystem, materializer: Materializer) extends Controller {
  def createRoom(timeLimitInSeconds: Int) = Action { implicit request =>
    val roomId = roomService.newRoom(timeLimitInSeconds)
    Ok(Json.obj("roomId" -> roomId))
  }

  // TODO: replace with up to date method
  def joinRoom(roomId: Int) = WebSocket.tryAccept[JsValue] { request =>
    val userIdOpt = request.cookies.get("userId")
    Logger.debug(s"User with id '$userIdOpt' requested to join room with id '$roomId'")

    val colorStr = request.getQueryString("color")
    val colorOpt = colorStr.flatMap(Color.fromString(_))
    val result: Option[Either[Result, (Iteratee[JsValue, Unit], Enumerator[JsValue])] with Product with Serializable] = for {
      userId <- userIdOpt
      color <- colorOpt
      room <- roomService.getRoomById(roomId)
    } yield {
      Right(room.join(userId.value, color))
    }
    Future.successful(result.getOrElse(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("Not able to join the room"))))))))
  }

  def listRooms() = WebSocket.accept { request =>
    val initialMsg = roomService.getTablesInfo()
    val killSwitch = KillSwitches.shared(scala.util.Random.nextInt.toString)
    val flow = Flow[String].map(s => s).via(killSwitch.flow)
    flow.via(ActorFlow.actorRef(out => ListRoomActor.props(initialMsg, out, killSwitch)))
  }
}
