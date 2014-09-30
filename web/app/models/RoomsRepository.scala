package models

import akka.actor.{Actor, Props}
import akka.util.Timeout
import play.api.libs.iteratee.{Iteratee, Enumerator, Concurrent}
import play.api.libs.json._
import play.libs.Akka
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by michal on 26/09/14.
 */
class RoomsRepository {

}
object RoomsRepository{
  implicit val timeout = Timeout(1 second)

  lazy val repositoryActor = Akka.system.actorOf(Props[RoomsRepositoryActor])

  def getRoomsSocket() = {
    (repositoryActor ? RepoConnect()).map {
      case RepoConnected(actorEnumerator, infoJson) =>
        val enumerator = Enumerator[JsValue](infoJson).andThen(actorEnumerator)
        (Iteratee.ignore[JsValue], enumerator)
    }
  }

  def refreshNeeded() = {
    repositoryActor ! RefreshNeeded()
  }
}


class RoomsRepositoryActor extends Actor{
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

  val (repoEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  def receive = {
    case RepoConnect() =>
      val tablesInfo = Room.getTablesInfo()
      val tablesJson = Json.toJson(tablesInfo)
      sender() ! RepoConnected(repoEnumerator, tablesJson)
    case RefreshNeeded() => refreshListInClients()
    case _ => println("unexpected message in RoomsRepository mailbox")
  }

  def refreshListInClients() = {
    val tablesInfo = Room.getTablesInfo()
    val tablesJson = Json.toJson(tablesInfo)
    chatChannel.push(tablesJson)
  }
}

case class RepoConnect()
case class RepoConnected(enumerator: Enumerator[JsValue], initialInfo: JsValue)
case class RefreshNeeded()
