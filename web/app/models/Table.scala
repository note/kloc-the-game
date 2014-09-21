package models


import akka.actor.{ActorRef, Actor, Props}
import net.michalsitko.kloc.game.Move
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Input, Enumerator, Iteratee, Concurrent}
import play.api.libs.json._
import scala.collection.mutable.Map
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import models.MoveMessage
import models.Connected
import models.Join
import play.api.libs.json.JsObject
import play.api.libs.json.JsString

class Table (actor: ActorRef) {
  def join() = {
    implicit val timeout = Timeout(1 second)
    (actor ? Join("player1")).map {
      case Connected(enumerator) =>
        val in = Iteratee.foreach[JsValue](event => {
          println("bazinga event received")
          actor ! MoveMessage((event \ "player").as[String], Move((event \ "from").as[String], (event \ "to").as[String]))
        }).map { _ =>
          println("Disconnected")
        }

        Right((in, enumerator))
      case _ =>
        Right((Iteratee.ignore[JsValue], Enumerator[JsValue](JsObject(Seq("error" -> JsString("cannot connect to table")))).andThen(Enumerator.enumInput(Input.EOF))))
    }
  }
}

object Table {
  var tables = Map[Int, Table]()
  var nextId = 0;

  def newTable(name: String) = {
    val roomActor = Akka.system.actorOf(Props[TableActor])
    val tableId = useNextId()
    tables += (tableId -> new Table(roomActor))
    tableId
  }

  def useNextId() = {
    val res = nextId
    nextId += 1
    res
  }

  def getTableById(id: Int) = tables.get(id)

  def getTableNames() = tables.keys
}

class TableActor extends Actor {
  val (tableEnumerator, tableChannel) = Concurrent.broadcast[JsValue]

  override def receive: Actor.Receive = {
    case MoveMessage(userId, move) =>
      notifyAll(MoveMessage(userId, move))
    case Join(playerName) =>
      sender() ! Connected(tableEnumerator)
  }

  private def notifyAll(moveMessage: MoveMessage) = {
    // TODO: add toJson method to Move (or something else and more idiomatic)
    val msg = JsObject(
      Seq(
        "from" -> JsString(moveMessage.move.from.toString),
        "to" -> JsString(moveMessage.move.to.toString)
      )
    )
    tableChannel.push(msg)
  }
}

case class MoveMessage(userId: String, move: Move)
case class Connected(enumerator: Enumerator[JsValue])
case class Join(playerName: String)
