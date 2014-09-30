package models

import net.michalsitko.kloc.game._
import scala.collection.mutable.Map
import akka.actor.Cancellable
import scala.concurrent.duration._
import play.libs.Akka
import net.michalsitko.kloc.game.White
import scala.Some
import net.michalsitko.kloc.game.Black
import net.michalsitko.kloc.game.IncorrectMoveException
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by michal on 22/09/14.
 */

object ChessTableState extends Enumeration {
  type ChessTableState = Value
  val WaitingForPlayers, Started, Finished = Value
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
        start()
        ChessTableState.Started
      case ChessTableState.Started if game.status.isFinished() => ChessTableState.Finished
      case _ => currentState
    }
  }

  def start() = {
    println("chesstable start")
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

  def move(user: User, move: Move): Option[GameStatus] = {
    val player = players.get(user)
    player.map{player =>
      if(player.color == game.whoseTurn)
        applyMove(player, move)
      else
        None
    }.getOrElse(None)
  }

  def getInfo(): ChessTableInfo = {
    val whitePlayer = if (white != null) Some(white.user.name) else None
    val blackPlayer = if (black != null) Some(black.user.name) else None
    ChessTableInfo(whitePlayer, blackPlayer)
  }

  private def applyMove(player: Player, move: Move): Option[GameStatus] = {
    try{
      game.applyMove(move)
      player.stopTimer(noTimeLeftTask)
      noTimeLeftTask = Some(player.opponent.startTimer(this))
      Some(game.status)
    }catch{
      // TODO: this case is really suspicious with js validation - add some logging
      case e: IncorrectMoveException =>
        player.startTimer(this)
        None
    }
  }

  def canStart(): Boolean = {
    players.size == 2
  }

  def getTimes(): Map[String, Long] = {
    players.map(mapItem => (mapItem._1.name, mapItem._2.getMsLeft))
  }

  def getColors(): Map[String, Color] = {
    players.map(mapItem => (mapItem._1.name, mapItem._2.color))
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