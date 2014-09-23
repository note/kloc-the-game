package controllers

import play.api._
import play.api.mvc._
import net.michalsitko.kloc.game.Color
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

  def registerUser = Action { request =>
    request.getQueryString("playerName") match {
      case Some(playerName) =>
        val userId = Room.registerUser(playerName)
        Ok(Json.obj("userId" -> JsString(userId)))
      case None =>
        Ok(Json.obj("errors" -> JsArray(List(JsString("No room name")))))
    }

  }

  def joinRoom(roomId: Int) = WebSocket.tryAccept[JsValue] { request =>
    println("bazinga44")
    val userId = request.cookies.get("userId")
    val colorStr = request.getQueryString("color")
    val color = colorStr.flatMap(Color.fromString(_))
    if(userId.isDefined && color.isDefined){
      Room.getRoomById(roomId) match {
        case Some(room) =>
          room.join(userId.get.value, color.get)
        case _ =>
          Future.successful(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("No room name")))))))
      }
    }else{
      println("Bad formatted request")
      Future.successful(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("Bad formatted request")))))))
    }
  }

  def mainJs = Action {
    Ok(views.js.main())
  }

}