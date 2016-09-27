import akka.actor.ActorSystem
import akka.util.Timeout
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.{JsObject, JsString, JsDefined}
import play.api.libs.ws.WSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.test.Helpers._
import utils.WebsocketProbe

import scala.concurrent.duration._

class RoomFlowIntegrationTest extends PlaySpec with OneServerPerSuite with FutureAwaits with DefaultAwaitTimeout {
  // TODO: close this system
  implicit val system = ActorSystem("RoomFlowIntegrationTest")

  override implicit def defaultAwaitTimeout: Timeout = 3.seconds

  val baseUrl = s"http://localhost:$port"

  "test server logic" in new TestContext {
    println("bazinga 1")
    val userId1 = createUser("user1")
    val userId2 = createUser("user2")
    val userId3 = createUser("user3")

    println(s"bazinga 2: $userId1, $userId2, $userId3")
    println("bazinga 3")

    val listRoomsProbe = new WebsocketProbe(s"localhost:$port/listRooms")

    listRoomsProbe.expectMsg(JsObject(List("rooms" -> JsObject(List.empty))))
  }

  trait TestContext extends Fixtures {
    val wsClient = app.injector.instanceOf[WSClient]

    def createUser(userName: String): String = {
      val createUserUrl =  s"$baseUrl/logInUser"
      val response = await(wsClient.url(createUserUrl).withQueryString("userName" -> userName).get())
      response.status must be (OK)
      (response.json \ "userId") match {
        case JsDefined(JsString(userId)) => userId
        case _ => fail("Unexpected JSON returned when creating user")
      }
    }
  }

  trait Fixtures {
    val user1 = "user1"
    val user2 = "user2"
    val user3 = "user3"
  }
}
