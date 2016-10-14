package controllers

import models._
import net.michalsitko.kloc.game.Color
import play.api._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

case class ChessTableInfos(tables: List[ChessTableInfo])

class ApplicationController extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready.", Room.getRoomNames().map((name: Int) => (name, routes.ApplicationController.joinRoom(name).webSocketURL()))))
  }

  def createRoom(timeLimitInSeconds: Int) = Action { implicit request =>
        val roomId = Room.newRoom(timeLimitInSeconds)
        Ok(Json.obj("roomId" -> roomId))
  }

  // TODO: replace with up to date method
  def joinRoom(roomId: Int) = WebSocket.tryAccept[JsValue] { request =>
    val userId = request.cookies.get("userId")
    Logger.debug(s"User with id '$userId' requested to join room with id '$roomId'")

    val colorStr = request.getQueryString("color")
    val color = colorStr.flatMap(Color.fromString(_))
    if(userId.isDefined && color.isDefined) {
      Room.getRoomById(roomId) match {
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
    RoomsRepository.getRoomsSocket()
  }

  def mainJs = Action { implicit request =>
    Ok(views.js.main())
  }

}