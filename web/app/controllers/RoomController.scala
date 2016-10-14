package controllers

import net.michalsitko.kloc.game.Color
import play.api.Logger
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.json.{JsArray, JsString, JsValue, Json}
import play.api.mvc.{Result, Action, Controller, WebSocket}
import services.RoomService

import scala.concurrent.Future

class RoomController (roomService: RoomService) extends Controller {
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
    } yield {
      roomService.getRoomById(roomId) match {
        case Some(room) =>
          Right(room.join(userId.value, color))
        case _ =>
          Left(Ok(Json.obj("errors" -> JsArray(List(JsString("No room name"))))))
      }
    }
    Future.successful(result.getOrElse(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("Bad formatted request"))))))))
  }

  def listRooms() = WebSocket.using[JsValue] { request =>
    roomService.getRoomsSocket
  }
}
