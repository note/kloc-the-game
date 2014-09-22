package controllers

import play.api._
import play.api.mvc._
import net.michalsitko.kloc.game.Chessboard
import play.api.libs.iteratee.{Input, Enumerator, Iteratee}
import play.api.libs.json._
import models.Room
import scala.concurrent.Future

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready.", Room.getRoomNames().map((name: Int) => (name, routes.Application.joinRoom(name).webSocketURL()))))
  }

  def createRoom = Action { implicit request =>
    request.getQueryString("name") match {
      case Some(roomName) =>
        val roomId = Room.newRoom(roomName)
        val webSocketUrl = routes.Application.joinRoom(roomId).webSocketURL()
        Ok(Json.obj("roomId" -> roomId, "url" -> webSocketUrl))
      case _ =>
        Ok(Json.obj("errors" -> JsArray(List(JsString("No room name")))))
    }
  }

  def joinRoom(roomId: Int) = WebSocket.tryAccept[JsValue] { request =>
    Room.getRoomById(roomId) match {
      case Some(room) =>
        room.join()
      case _ =>
        Future.successful(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("No room name")))))))
    }
  }

  def mainJs = Action {
    Ok(views.js.main())
  }

}