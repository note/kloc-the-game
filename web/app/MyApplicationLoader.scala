import controllers.{Assets, ApplicationController}
import play.api._
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import router.Routes

class MyApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new MyComponents(context).application
  }
}

class MyComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  lazy val appController = new ApplicationController
  lazy val assets = new Assets(httpErrorHandler)

  lazy val router: Router = new Routes(httpErrorHandler, appController, assets)
}