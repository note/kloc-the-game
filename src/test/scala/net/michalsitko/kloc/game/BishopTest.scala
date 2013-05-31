package net.michalsitko.kloc.game

import net.michalsitko.game.{Move, Chessboard}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
class BishopTest extends FunSuite with ShouldMatchers with PositionGenerator {
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  test("can move diagonally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))
    chessboard.applyMove(new Move("d7", "d6"))

    expectLegal(chessboard, new Move("f1", "e2"))
    expectLegal(chessboard, new Move("f1", "c4"))
    expectLegal(chessboard, new Move("c8", "d7"))
    expectLegal(chessboard, new Move("c8", "g4"))
  }

  test("can take enemy piece") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))
    chessboard.applyMove(new Move("b7", "b5"))

    expectLegal(chessboard, new Move("f1", "b5"))
  }

  test("cannot move other way than diagonally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))
    chessboard.applyMove(new Move("f2", "f4"))

    expectIllegal(getInitialPosition(), new Move("f1", "f3"))
    expectIllegal(getInitialPosition(), new Move("f1", "f2"))
    expectIllegal(getInitialPosition(), new Move("f1", "g3"))
  }

  test("cannot overleap") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))
    chessboard.applyMove(new Move("g7", "g5"))
    chessboard.applyMove(new Move("d2", "d3"))

    expectIllegal(chessboard, new Move("f1", "d3"))
    expectIllegal(chessboard, new Move("f1", "c4"))
    expectIllegal(chessboard, new Move("c1", "h6"))
  }
}
