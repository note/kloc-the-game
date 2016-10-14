package controllers

import models._
import play.api.mvc._

case class ChessTableInfos(tables: List[ChessTableInfo])

class ApplicationController extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def mainJs = Action { implicit request =>
    Ok(views.js.main())
  }

}