package net.michalsitko.kloc.game

import net.michalsitko.game.{Move, Chessboard}
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
class KnightTest extends FunSuite with ShouldMatchers with PositionGenerator {
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  test("can move knight's moves") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))

    expectLegal(chessboard, new Move("g1", "f3"))
    expectLegal(chessboard, new Move("g1", "h3"))
    expectLegal(chessboard, new Move("g1", "e2"))
    expectLegal(chessboard, new Move("b8", "a6"))
    expectLegal(chessboard, new Move("b8", "c6"))
  }

  test("can take enemy piece") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("g1", "f3"))
    chessboard.applyMove(new Move("e7", "e5"))

    expectLegal(chessboard, new Move("f3", "e5"))
  }

  test("cannot take its own piece") {
    val chessboard = getInitialPosition()

    expectIllegal(chessboard, new Move("g1", "e2"))
  }

  test("cannot move other way than knight's moves") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("f2", "f4"))
    chessboard.applyMove(new Move("g2", "g4"))

    expectIllegal(getInitialPosition(), new Move("g1", "g3"))
    expectIllegal(getInitialPosition(), new Move("g1", "e3"))
  }
}
