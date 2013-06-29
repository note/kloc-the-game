package net.michalsitko.kloc.game

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import net.michalsitko.kloc.game.matchers.CustomMatchers.beLegal

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

    (chessboard, Move("a1", "a2")) should beLegal
    (chessboard, Move("a1", "a6")) should beLegal
    (chessboard, Move("a8", "a7")) should beLegal
    (chessboard, Move("a8", "a4")) should beLegal
  }

  test("can move horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "a4"))
    chessboard.applyMove(Move("a1", "a3"))

    (chessboard, Move("a3", "b3")) should beLegal
    (chessboard, Move("a3", "g3")) should beLegal
  }

  test("can take enemy piece") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "b3"))
    chessboard.applyMove(Move("a7", "b6"))

    (chessboard, Move("a1", "a8")) should beLegal
    (chessboard, Move("a8", "a1")) should beLegal
  }

  test("cannot move other way than vertically and horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "a4"))
    chessboard.applyMove(Move("b2", "b4"))

    (getInitialPosition(), Move("a1", "b2")) should not (beLegal)
    (getInitialPosition(), Move("a1", "d4")) should not (beLegal)
    (getInitialPosition(), Move("a1", "b3")) should not (beLegal)
  }

  test("cannot overleap") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("a2", "a4"))
    chessboard.applyMove(Move("a7", "b6"))

    (chessboard, Move("a1", "a4")) should not (beLegal)
    (chessboard, Move("a1", "a5")) should not (beLegal)
    (chessboard, Move("a8", "a2")) should not (beLegal)
    (chessboard, Move("a8", "a1")) should not (beLegal)
  }

  test("can be pinned") {
    val chessboard = new Chessboard
    chessboard.setPiece("e1", Some(WhiteKing))
    chessboard.setPiece("b4", Some(BlackBishop))
    chessboard.setPiece("d2", Some(WhiteQueen))

    expectResult(true){chessboard.isPinned(Move("d2", "d5"))}
    expectResult(true){chessboard.isPinned(Move("d2", "c2"))}
    expectResult(true){chessboard.isPinned(Move("d2", "h2"))}
  }
}
