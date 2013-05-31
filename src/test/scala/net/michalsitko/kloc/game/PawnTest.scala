package net.michalsitko.kloc.game

import net.michalsitko.game.{Chessboard, Move}
import org.scalatest.{PrivateMethodTester, FunSuite}
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
class PawnTest extends FunSuite with ShouldMatchers with PositionGenerator{
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  test("can move forward by 2 fields from start position") {
    expectLegal(getInitialPosition(), new Move("e2", "e4"))
    expectLegal(getInitialPosition(), new Move("e7", "e5"))
    expectLegal(getInitialPosition(), new Move("a7", "a5"))

    expectIllegal(getInitialPosition(), new Move("e2", "d4"))
    expectIllegal(getInitialPosition(), new Move("e2", "e5"))
  }

  test("can move forward by 1 field from start position") {
    expectLegal(getInitialPosition(), new Move("e2", "e3"))
    expectLegal(getInitialPosition(), new Move("e7", "e6"))
  }

  test("cannot move forward by 2 fields from non-start position"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e3"))
    chessboard.applyMove(new Move("e7", "e6"))

    expectIllegal(chessboard, new Move("e3", "e5"))
    expectIllegal(chessboard, new Move("e7", "e6"))
  }

  test("move forward by 1 field from non-start position"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e3"))
    chessboard.applyMove(new Move("e7", "e6"))

    expectLegal(chessboard, new Move("e3", "e4"))
    expectLegal(chessboard, new Move("e6", "e5"))
    expectIllegal(chessboard, new Move("e3", "e2"))
    expectIllegal(chessboard, new Move("e6", "e7"))
  }

  test("take on diagonal 1 field forward"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))
    chessboard.applyMove(new Move("d7", "d5"))

    expectLegal(chessboard, new Move("e4", "d5"))
    expectLegal(chessboard, new Move("d5", "e4"))
    expectIllegal(chessboard, new Move("e4", "f5"))
    expectIllegal(chessboard, new Move("d5", "c4"))
  }

  test("enpassant right after enemy pawn move"){
    val chessboard = getTypicalPosition()
  }

  test("cannot enpassant later") {
    val chessboard = getTypicalPosition()

  }

  test("be promoted to any piece") {
    val chessboard = getPromotionPosition()
  }

  test("cannot overleap when forward by one field"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))
    chessboard.applyMove(new Move("e7", "e5"))

    expectIllegal(chessboard, new Move("e4", "e5"))
    expectIllegal(chessboard, new Move("e5", "e4"))
  }

  test("cannot overleap when forward by two fields"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e6"))

    expectIllegal(chessboard, new Move("e7", "e5"))
  }
}
