package controllers

import play.api._
import play.api.mvc._
import net.michalsitko.kloc.game.Chessboard
import play.api.libs.iteratee.{Input, Enumerator, Iteratee}
import play.api.libs.json._
import models.Table
import scala.concurrent.Future

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready.", Table.getTableNames().map((name: Int) => (name, routes.Application.joinTable(name).webSocketURL()))))
  }

  def createTable = Action { implicit request =>
    request.getQueryString("name") match {
      case Some(tableName) =>
        val tableId = Table.newTable(tableName)
        val webSocketUrl = routes.Application.joinTable(tableId).webSocketURL()
        Ok(Json.obj("tableId" -> tableId, "url" -> webSocketUrl))
      case _ =>
        Ok(Json.obj("errors" -> JsArray(List(JsString("No table name")))))
    }
  }

  def joinTable(tableId: Int) = WebSocket.tryAccept[JsValue] { request =>
    Table.getTableById(tableId) match {
      case Some(table) =>
        table.join()
      case _ =>
        Future.successful(Left(Ok(Json.obj("errors" -> JsArray(List(JsString("No table name")))))))
    }
  }

  def mainJs = Action {
    Ok(views.js.main())
  }

}