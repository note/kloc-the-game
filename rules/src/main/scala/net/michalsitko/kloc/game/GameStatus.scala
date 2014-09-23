package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 9/22/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
case class GameStatus (chessboard: Chessboard, result: Option[Result]){
  // TODO: change to parameterless method
  def isFinished(): Boolean = result.isDefined
  override def toString(): String = {
    result.map(_.toString).getOrElse("")
  }
}
