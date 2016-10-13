package models

import akka.actor._
import play.api.libs.json.{JsArray, JsObject, Json, Writes}

class ChessTableInfoActor (out: ActorRef) extends Actor {
  import ChessTableInfoActor._
  import ChessTableInfo._

  def receive = {
    case ChessTableInfoNeeded =>

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

      out ! Json.toJson(Room.getTablesInfo())
  }
}

object ChessTableInfoActor{
  def props(out: ActorRef) = Props(new ChessTableInfoActor(out))

  case object ChessTableInfoNeeded
}
