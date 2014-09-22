package models

import net.michalsitko.kloc.game._
import scala.collection.mutable.Map
import models.Player
import models.User
import models.TimeControl
import akka.actor.{Cancellable}
import scala.concurrent.duration._
import play.libs.Akka
import net.michalsitko.kloc.game.White
import scala.Some
import models.Player
import models.User
import net.michalsitko.kloc.game.Black
import models.TimeControl
import models.NoTimeLeft
import net.michalsitko.kloc.game.IncorrectMoveException
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by michal on 22/09/14.
 */

object ChessTableState extends Enumeration {
  type ChessTableState = Value
  val WaitingForPlayers, Ready, Started, Finished = Value
}

// make constructor private
class ChessTable (timeLimitMs: Int) {
  private var white: Player = _
  private var black: Player = _
  private val players = Map[User, Player]()
  private var currentState = ChessTableState.WaitingForPlayers
  private var noTimeLeftTask: Option[Cancellable] = None

  val game = new KlocGame

  private def canBeAdded(user: User, color: Color) = {
    color != null && players.get(user).isEmpty && !players.exists{case (user, player) => player.color == color}
  }

  private def updateState(){
    currentState = currentState match {
      case ChessTableState.WaitingForPlayers if players.size == 2 =>
        white.opponent = black
        black.opponent = white
        ChessTableState.Ready
      case ChessTableState.Ready if players.forall{case (user, player) => player.getRequestedStart} =>
        start()
        ChessTableState.Started
      case ChessTableState.Started if game.status.isFinished() => ChessTableState.Finished
      case _ => currentState
    }
  }

  def start() = {
    noTimeLeftTask = Some(white.startTimer(this))
  }

  def timeExceeded(user: User) = {
    players.get(user).foreach{player =>
      game.status = game.status.copy(result = Some(new Winner(player.color.opposite())))
      updateState()
    }
  }

  def addPlayer(user: User, color: Color){
    if(!canBeAdded(user, color)){
      throw new IllegalArgumentException("User cannot be added to this ChessTable")
    }

    val player = new Player(user, TimeControl(timeLimitMs), color)
    players(user) = player
    player.color match {
      case White() => white = player
      case Black() => black = player
    }
    updateState()
  }

  def requestStart(user: User){
    players.get(user).foreach{ player =>
      player.requestStart()
      updateState()
    }
  }

  def move(user: User, move: Move) = {
    val player = players.get(user)
    player.foreach{player =>
      if(player.color == game.whoseTurn)
        applyMove(player, move)
    }
  }

  private def applyMove(player: Player, move: Move) = {
    try{
      game.applyMove(move)
      player.stopTimer(noTimeLeftTask)
      noTimeLeftTask = Some(player.opponent.startTimer(this))
    }catch{
      case e: IncorrectMoveException =>
    }
  }

  def canStart(): Boolean = {
    players.size == 2
  }

  def state = currentState
}

case class User(name: String, id: String)

case class Player(user: User, timeControl: TimeControl, color: Color){
  private var millisecondsLeft: Long = timeControl.initialMs
  private var requestedStart: Boolean = false
  private var timerStarted: Long = _
  var opponent: Player = _

  def setMsLeft(msLeft: Int) {
    millisecondsLeft = msLeft
  }

  def requestStart(): Unit = {
    requestedStart = true
  }

  def startTimer(table: ChessTable): Cancellable = {
    timerStarted = System.nanoTime()
    println("bazinga startTimer: " + millisecondsLeft)
    val res = Akka.system.scheduler.scheduleOnce(millisecondsLeft milliseconds){
      table.timeExceeded(user)
    }
    println("bazinga startTimer2: " + millisecondsLeft)
    res
  }

  def stopTimer(task: Option[Cancellable]): Unit = {
    task.foreach(_.cancel())
    val timerStopped = System.nanoTime()
    val diffMs = (timerStopped - timerStarted)/(1000000)
    millisecondsLeft = millisecondsLeft - diffMs
  }

  def getMsLeft = millisecondsLeft

  def getRequestedStart = requestedStart
}

case class TimeControl(initialMs: Int){
}

case class NoTimeLeft(user: User)