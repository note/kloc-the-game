package net.michalsitko.kloc.game

import net.michalsitko.game.{Move, Chessboard}
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
class QueenTest extends FunSuite with ShouldMatchers with PositionGenerator{
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  test("can move vertically") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("d2", "e3"))
    chessboard.applyMove(new Move("d7", "e6"))

    expectLegal(chessboard, new Move("d1", "d2"))
    expectLegal(chessboard, new Move("d1", "d6"))
    expectLegal(chessboard, new Move("d8", "d7"))
    expectLegal(chessboard, new Move("d8", "d4"))
  }

  test("can move horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("d2", "d4"))
    chessboard.applyMove(new Move("d1", "d3"))

    expectLegal(chessboard, new Move("d3", "b3"))
    expectLegal(chessboard, new Move("d3", "g3"))
  }

  test("can move diagonally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "d4"))
    chessboard.applyMove(new Move("c2", "c3"))

    expectLegal(chessboard, new Move("d1", "c2"))
    expectLegal(chessboard, new Move("d1", "g4"))
  }

  test("can take enemy piece") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("d2", "e3"))
    chessboard.applyMove(new Move("d7", "e6"))

    expectLegal(chessboard, new Move("d1", "d8"))
    expectLegal(chessboard, new Move("d8", "d1"))
  }

  test("cannot move other way than diagonally, vertically and horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("d2", "d4"))
    chessboard.applyMove(new Move("e2", "e4"))

    expectIllegal(getInitialPosition(), new Move("d1", "e3"))
    expectIllegal(getInitialPosition(), new Move("d1", "c3"))
  }

  test("cannot overleap") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("d2", "d4"))
    chessboard.applyMove(new Move("d7", "e6"))

    expectIllegal(chessboard, new Move("d1", "d4"))
    expectIllegal(chessboard, new Move("d1", "d5"))
    expectIllegal(chessboard, new Move("d1", "a4"))
    expectIllegal(chessboard, new Move("d8", "d2"))
    expectIllegal(chessboard, new Move("d8", "d1"))
  }
}
