package net.michalsitko.kloc.game

import org.scalatest.Assertions

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 8/13/13
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
trait MoveAssertions extends Assertions{
  def expectLegal(chessboard: Chessboard, move: Move, gameState: GameState) {
    expectResult(true)(chessboard.isMoveCorrect(move, gameState))
  }

  def expectLegal(chessboard: Chessboard, move: Move) {
    expectLegal(chessboard, move, GameState.default())
  }

  def expectIllegal(chessboard: Chessboard, move: Move, gameState: GameState) {
    expectResult(false)(chessboard.isMoveCorrect(move, gameState))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectIllegal(chessboard, move, GameState.default())
  }
}
