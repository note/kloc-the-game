package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class KlocGame {
  private var gameState = GameState.default()
  private val chessboard = Chessboard.initialPosition()

  def applyMove(move: Move): (Chessboard, GameState) = {
    if(chessboard.isMoveCorrect(move, gameState)){
      gameState = chessboard.applyMove(move, gameState)
      return (chessboard, gameState)
    }
    throw new IncorrectMoveException()
  }
}
