package controllers

import models._
import play.api.mvc._
import services.InMemoryRoomService

case class ChessTableInfos(tables: List[ChessTableInfo])

class ApplicationController extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready.", InMemoryRoomService.getRoomNames().map((name: Int) => (name, routes.RoomController.joinRoom(name).webSocketURL()))))
  }

  def mainJs = Action { implicit request =>
    Ok(views.js.main())
  }

}