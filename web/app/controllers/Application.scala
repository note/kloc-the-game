package controllers

import play.api._
import play.api.libs.concurrent.Akka
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._
import net.michalsitko.kloc.game.{Move, Color, Chessboard}
import play.api.libs.iteratee.{Input, Enumerator, Iteratee}
import play.api.libs.json._
import models._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import play.api.Play.current

case class ChessTableInfos(tables: List[ChessTableInfo])

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready.", Room.getRoomNames().map((name: Int) => (name, routes.Application.joinRoom(name).webSocketURL()))))
  }

  def createRoom = Action { implicit request =>
        val roomId = Room.newRoom()
        val webSocketUrl = routes.Application.joinRoom(roomId).webSocketURL()
        Ok(Json.obj("roomId" -> roomId, "url" -> webSocketUrl))
  }

  def logInUser = Action { request =>
    request.getQueryString("userName") match {
      case Some(userName) =>
        val userId = Room.registerUser(userName)
        Ok(Json.obj("userId" -> JsString(userId)))
      case None =>
        Ok(Json.obj("errors" -> JsArray(List(JsString("No room name")))))
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

  def listRooms() = WebSocket.using[JsValue] { request =>
    println("bazinga listrooms")
    Await.result(RoomsRepository.getRoomsSocket(), 2000 milliseconds)
  }

  def mainJs = Action { implicit request =>
    Ok(views.js.main())
  }

}