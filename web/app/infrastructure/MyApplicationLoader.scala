package infrastructure

import java.util.concurrent.Executors

import com.typesafe.config.Config
import controllers.{ApplicationController, Assets, RoomController, UserController}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import router.Routes
import services.InMemoryRoomService

import scala.concurrent.ExecutionContext

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

  lazy val userService = InMemoryRoomService.userService
  lazy val roomService = InMemoryRoomService

  lazy val userController = new UserController(userService)
  lazy val roomController = new RoomController
  lazy val appController = new ApplicationController
  lazy val assets = new Assets(httpErrorHandler)

  lazy val router: Router = new Routes(
    httpErrorHandler,
    userController,
    roomController,
    appController,
    assets)

  protected def executionContext(config: Config): ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))

  /** Allows control of HttpClient instance to be used. For now default PlayWS client is shared. */
//  protected def httpClient(config: Config, ec: ExecutionContext): HttpAccess =
//    new PlayWsHttpClient(config, wsClient)(ec, system) with LoggingClient
}