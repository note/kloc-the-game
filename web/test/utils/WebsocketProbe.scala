package utils

import akka.actor._
import akka.testkit.TestProbe
import org.asynchttpclient.ws.{WebSocket, WebSocketTextListener, WebSocketUpgradeHandler}
import org.asynchttpclient.{DefaultAsyncHttpClient, RequestBuilder}
import play.api.libs.json.{JsValue, Json}

object WebsocketProbe {

  case class MessageReceived(jsValue: JsValue)

  case class SendMessage(msg: String)

  case class Error(t: Throwable)

  case object Close

  case object Open

}


class WebsocketProbe(url: String)(implicit actorSystem: ActorSystem) {
  import WebsocketProbe._

  val probe = TestProbe()

  actorSystem.actorOf(WebsocketActor.props(url, probe.ref))

  probe.expectMsg(Open)

  def expectMsg(jsValue: JsValue): Unit = {
    probe.expectMsg(MessageReceived(jsValue))
  }
}

class WebsocketActor (url: String, listener: ActorRef) extends Actor {
  import WebsocketProbe._

  val client = new DefaultAsyncHttpClient()


  println("bazinga test: " + s"ws://$url")
  val wsRequestBuilder = new RequestBuilder("GET").setUrl(s"ws://$url")

  val websocket = client.prepareGet(s"ws://$url").execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
//  val websocket: WebSocket = client.executeRequest(wsRequestBuilder.build(), new WebSocketUpgradeHandler.Builder().addWebSocketListener(
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
      websocket.sendMessage(msg)
    case Close ⇒
      websocket.close()
      self ! PoisonPill
  }
}

object WebsocketActor {
  def props(url: String, listener: ActorRef) = Props(new WebsocketActor(url, listener))
}
