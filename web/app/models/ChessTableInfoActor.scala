package models

import akka.actor._
import play.api.libs.json.{JsArray, JsObject, Json, Writes}

/**
 * Created by michal on 25/09/14.
 */
class ChessTableInfoActor (out: ActorRef) extends Actor {
  def receive = {
    case msg: ChessTableInfoNeeded =>
      implicit val chessTableInfoWrites = new Writes[ChessTableInfo] {
        def writes(info: ChessTableInfo) = Json.obj(
          "white" -> info.whitePlayerName.getOrElse(null),
          "black" -> info.blackPlayerName.getOrElse(null)
        )
      }

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
}

case class ChessTableInfoNeeded()
