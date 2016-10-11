package utils

import akka.actor._
import akka.testkit.TestProbe
import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.cookie.Cookie
import org.asynchttpclient.ws.{WebSocket, WebSocketTextListener, WebSocketUpgradeHandler}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.duration.Duration

object WebsocketProbe {

  case class MessageReceived(jsValue: JsValue)

  case class SendMessage(msg: JsValue)

  case class Error(t: Throwable)

  case object Close

  case object Open

}


class WebsocketProbe(url: String, cookies: (String, String)*)(implicit actorSystem: ActorSystem) {
  import WebsocketProbe._

  val probe = TestProbe()

  val websocketActor = actorSystem.actorOf(WebsocketActor.props(url, cookies, probe.ref))

  probe.expectMsg(Open)

  def expectMsg(jsValue: JsValue): Unit = {
    probe.expectMsg(MessageReceived(jsValue))
  }

  def fetchMsg(max: Duration): JsValue = {
    probe.expectMsgPF(max) {
      case msg: MessageReceived => msg.jsValue
    }
  }

  def sendMsg(jsValue: JsValue): Unit = {
    websocketActor ! SendMessage(jsValue)
  }
}

class WebsocketActor (url: String, cookies: Seq[(String, String)], listener: ActorRef) extends Actor {
  import WebsocketProbe._

  val client = new DefaultAsyncHttpClient()


  println("bazinga test: " + s"ws://$url")
  val wsRequestBuilder = client.prepareGet(s"ws://$url")

  for ((name, value) <- cookies) wsRequestBuilder.addCookie(createCookie(name, value))

  val websocket = wsRequestBuilder.execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
    new WebSocketTextListener {
      override def onMessage(message: String): Unit =
        listener ! MessageReceived(Json.parse(message))

      override def onError(t: Throwable): Unit =
        listener ! Error(t)

      override def onClose(websocket: WebSocket): Unit = {
        listener ! Close
        self ! PoisonPill
      }

      override def onOpen(websocket: WebSocket): Unit =
        listener ! Open

    }).build()).get()

  override def receive: Actor.Receive = {
    case SendMessage(msg) ⇒
      websocket.sendMessage(msg.toString)
    case Close ⇒
      websocket.close()
      self ! PoisonPill
  }

  private def createCookie(name: String, value: String): Cookie = {
    new Cookie(name, value, false, "", "", -1, false, false)
  }
}

object WebsocketActor {
  def props(url: String, cookies: Seq[(String, String)], listener: ActorRef) = Props(new WebsocketActor(url, cookies: Seq[(String, String)], listener))
}
