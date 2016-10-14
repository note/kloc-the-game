package infrastructure

import controllers.{ApplicationController, Assets, RoomController, UserController}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import router.Routes
import services.{InMemoryRoomService, InMemoryUserService}

class MyApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new MyComponents(context).application
  }
}

class TestMyApplicationLoader(val components: TestMyComponents) extends MyApplicationLoader {
  override def load(context: Context): Application = components.application
}

class TestMyComponents(override val context: Context) extends MyComponents(context)

class MyComponents(val context: Context)
    extends BuiltInComponentsFromContext(context)
    with AhcWSComponents {

  implicit val ec = play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val defaultActorSystem = actorSystem
  implicit val defaultMaterializer = materializer

  lazy val userService = new InMemoryUserService
  lazy val roomService = new InMemoryRoomService(userService, actorSystem)

  lazy val userController = new UserController(userService)
  lazy val roomController = new RoomController(roomService)
  lazy val appController = new ApplicationController
  lazy val assets = new Assets(httpErrorHandler)

  lazy val router: Router = new Routes(
    httpErrorHandler,
    userController,
    roomController,
    appController,
    assets)

}