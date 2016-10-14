package controllers

import play.api.libs.json.{JsArray, JsBoolean, JsString, Json}
import play.api.mvc.{Action, Controller}
import services.UserService

class UserController (userService: UserService) extends Controller {
  def logInUser = Action { request =>
    request.getQueryString("userName") match {
      case Some(userName) =>
        val userId = userService.registerUser(userName)
        Ok(Json.obj("userId" -> JsString(userId)))
      case None =>
        Ok(Json.obj("errors" -> JsArray(List(JsString("Incorrect request")))))
    }
  }

  def isUserLoggedIn = Action { request =>
    val userExists = request.getQueryString("userId") match {
      case Some(userId) =>
        userService.existsUserId(userId)
      case None =>
        false
    }
    Ok(Json.obj("result" -> JsBoolean(userExists)))
  }
}
