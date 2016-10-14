package actors

import actors.ListRoomActor.RoomsUpdated
import akka.actor.{Actor, ActorRef, Props}
import akka.stream.SharedKillSwitch
import models.ChessTableInfo
import play.api.libs.json.{JsArray, JsObject, Json, Writes}

class ListRoomActor (initialMsg: Map[Int, List[ChessTableInfo]], out: ActorRef, killSwitch: SharedKillSwitch) extends Actor {
  println("bazinga ListRoomActor1")
  context.system.eventStream.subscribe(self, classOf[RoomsUpdated])
  println("bazinga ListRoomActor2")
  sendJson(initialMsg)
  println("bazinga ListRoomActor3")

  // TODO: add closing socker and killing this actor
  override def receive: Receive = {
    case RoomsUpdated(rooms) =>
      println("bazinga ListRoomActor4")
      sendJson(rooms)
  }

  private def sendJson(msg: Map[Int, List[ChessTableInfo]]): Unit = {
    // TODO: try to get rid of both of those Writes
    implicit val roomWrites = new Writes[List[ChessTableInfo]] {
      def writes(tables: List[ChessTableInfo]) = JsArray(tables.map(Json.toJson(_)))
    }

    implicit val roomsWrites = new Writes[Map[Int, List[ChessTableInfo]]] {
      def writes(rooms: Map[Int, List[ChessTableInfo]]) = {
        val converted = rooms.map(mapItem => (mapItem._1.toString, Json.toJson(mapItem._2)))
        Json.obj(
          "rooms" -> JsObject(converted.toSeq)
        )
      }
    }

    out ! Json.toJson(msg).toString
  }

  override def postStop(): Unit = {
    println("bazinga postStop in ListRoomActor")
  }
}

object ListRoomActor {
  // TODO: replace initialMsg with akka-streams stuff in RoomController
  def props(initialMsg: Map[Int, List[ChessTableInfo]], out: ActorRef, killSwitch: SharedKillSwitch) =
    Props(new ListRoomActor(initialMsg, out, killSwitch))

  // TODO: change value to ChessTableInfo (instead of List[ChessTableInfo])
  case class RoomsUpdated(rooms: Map[Int, List[ChessTableInfo]])
}
