package controllers

import net.michalsitko.kloc.game.Color
import play.api.Logger
import play.api.libs.json.{JsArray, JsString, JsValue, Json}
import play.api.mvc.{Action, Controller, WebSocket}
import services.InMemoryRoomService

import scala.concurrent.Future

class RoomController extends Controller {
  def createRoom(timeLimitInSeconds: Int) = Action { implicit request =>
    val roomId = InMemoryRoomService.newRoom(timeLimitInSeconds)
    Ok(Json.obj("roomId" -> roomId))
  }

  // TODO: replace with up to date method
  def joinRoom(roomId: Int) = WebSocket.tryAccept[JsValue] { request =>
    val userId = request.cookies.get("userId")
    Logger.debug(s"User with id '$userId' requested to join room with id '$roomId'")

    val colorStr = request.getQueryString("color")
    val color = colorStr.flatMap(Color.fromString(_))
    if(userId.isDefined && color.isDefined) {
     InMemoryRoomService.getRoomById(roomId) match {
        case Some(room) =>
          room.join(userId.get.value, color.get)
        case _ =>
          Future.successful(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("No room name")))))))
      }
    } else {
      Future.successful(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("Bad formatted request")))))))
    }
  }

  def listRooms() = WebSocket.using[JsValue] { request =>
    InMemoryRoomService.getRoomsSocket
  }
}
