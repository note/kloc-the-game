package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class KlocGame {
  private val gameState = GameState
  private val chessboard = new Chessboard

  def applyMove(move: Move, gameState: GameState): Unit = {
    if(chessboard.isMoveCorrect(move, gameState))
      chessboard.applyMove(move)
  }
}
