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
  def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }
}
