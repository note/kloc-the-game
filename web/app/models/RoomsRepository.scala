package models

import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import play.api.Logger
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json._
import play.libs.Akka

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object RoomsRepository{
  implicit val timeout = Timeout(1 second)

  lazy val repositoryActor = Akka.system.actorOf(Props[RoomsRepositoryActor])

  def getRoomsSocket() = {
    (repositoryActor ? RepoConnect).map {
      case RepoConnected(actorEnumerator, infoJson) =>
        val enumerator = Enumerator[JsValue](infoJson).andThen(actorEnumerator)
        (Iteratee.ignore[JsValue], enumerator)
    }
  }

  def refreshNeeded() = {
    repositoryActor ! RefreshNeeded
  }
}


class RoomsRepositoryActor extends Actor{
  import ChessTableInfo._

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
    case RepoConnect =>
      val tablesInfo = Room.getTablesInfo()
      val tablesJson = Json.toJson(tablesInfo)
      sender() ! RepoConnected(repoEnumerator, tablesJson)
    case RefreshNeeded => refreshListInClients()
    case _ => Logger.warn("unexpected message in RoomsRepositoryActor mailbox")
  }

  def refreshListInClients() = {
    val tablesInfo = Room.getTablesInfo()
    val tablesJson = Json.toJson(tablesInfo)
    chatChannel.push(tablesJson)
  }
}

case object RepoConnect
case class RepoConnected(enumerator: Enumerator[JsValue], initialInfo: JsValue)
case object RefreshNeeded
