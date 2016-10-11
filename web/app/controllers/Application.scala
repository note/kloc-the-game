package controllers

import models._
import net.michalsitko.kloc.game.Color
import play.api._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class ChessTableInfos(tables: List[ChessTableInfo])

class ApplicationController extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready.", Room.getRoomNames().map((name: Int) => (name, routes.ApplicationController.joinRoom(name).webSocketURL()))))
  }

  def createRoom(timeLimitInSeconds: Int) = Action { implicit request =>
        val roomId = Room.newRoom(timeLimitInSeconds)
        Ok(Json.obj("roomId" -> roomId))
  }

  def logInUser = Action { request =>
    request.getQueryString("userName") match {
      case Some(userName) =>
        val userId = Room.registerUser(userName)
        Ok(Json.obj("userId" -> JsString(userId)))
      case None =>
        Ok(Json.obj("errors" -> JsArray(List(JsString("Incorrect request")))))
    }
  }

  def isUserLoggedIn = Action { request =>
    val userExists = request.getQueryString("userId") match {
      case Some(userId) =>
        Room.existsUserId(userId)
      case None =>
        false
    }
    Ok(Json.obj("result" -> JsBoolean(userExists)))
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
    Await.result(RoomsRepository.getRoomsSocket(), 2000 milliseconds)
  }

  def mainJs = Action { implicit request =>
    Ok(views.js.main())
  }

}