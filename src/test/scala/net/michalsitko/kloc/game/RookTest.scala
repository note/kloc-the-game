package net.michalsitko.kloc.game

import net.michalsitko.game.{Chessboard, Move}
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
class RookTest extends FunSuite with ShouldMatchers with PositionGenerator{
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  test("can move vertically") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "b3"))
    chessboard.applyMove(Move("a7", "b6"))

    expectLegal(chessboard, Move("a1", "a2"))
    expectLegal(chessboard, Move("a1", "a6"))
    expectLegal(chessboard, Move("a8", "a7"))
    expectLegal(chessboard, Move("a8", "a4"))
  }

  test("can move horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "a4"))
    chessboard.applyMove(Move("a1", "a3"))

    expectLegal(chessboard, Move("a3", "b3"))
    expectLegal(chessboard, Move("a3", "g3"))
  }

  test("can take enemy piece") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "b3"))
    chessboard.applyMove(Move("a7", "b6"))

    expectLegal(chessboard, Move("a1", "a8"))
    expectLegal(chessboard, Move("a8", "a1"))
  }

  test("cannot move other way than vertically and horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "a4"))
    chessboard.applyMove(Move("b2", "b4"))

    expectIllegal(getInitialPosition(), Move("a1", "b2"))
    expectIllegal(getInitialPosition(), Move("a1", "d4"))
    expectIllegal(getInitialPosition(), Move("a1", "b3"))
  }

  test("cannot overleap") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "a4"))
    chessboard.applyMove(Move("a7", "b6"))

    expectIllegal(chessboard, Move("a1", "a4"))
    expectIllegal(chessboard, Move("a1", "a5"))
    expectIllegal(chessboard, Move("a8", "a2"))
    expectIllegal(chessboard, Move("a8", "a1"))
  }
}
