package controllers

import play.api._
import play.api.mvc._
import net.michalsitko.kloc.game.Chessboard

object Application extends Controller {

  def index = Action {
    // to delete, just to check if class from rules project are visible in web project
    val chessboard = new Chessboard
    println("after changes")
    Ok(views.html.index("Your new application is ready."))
  }

}