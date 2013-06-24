package net.michalsitko.kloc.game

import org.scalatest.{BeforeAndAfter, fixture}
import org.scalatest.matchers.{MatchResult, ShouldMatchers, Matcher}
import net.michalsitko.game._
import scala.Some
import net.michalsitko.kloc.game.matchers.CustomMatchers.beLegal

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 6/1/13
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
class KingTest extends fixture.FunSpec with ShouldMatchers with PositionGenerator {
  type FixtureParam = Chessboard

  def withFixture(test: OneArgTest) {
    // it seems to be odd and actually is. Piece is lazy so we have to invoke is somehow before first use of any of its specializations
    Piece

    val chessboard = new Chessboard
    chessboard.setPiece(Field.fromString("e4"), Some(WhiteKing))
    test(chessboard)
  }

  describe("A King") {
    it("can move all directions by one field") {
      chessboard =>
        (chessboard, Move("e4", "e3")) should beLegal
        (chessboard, Move("e4", "e5")) should beLegal
        (chessboard, Move("e4", "d4")) should beLegal
        (chessboard, Move("e4", "f4")) should beLegal
        (chessboard, Move("e4", "f5")) should beLegal
        (chessboard, Move("e4", "f3")) should beLegal
    }

    it("cannot move by more than one field") {
      chessboard =>
        (chessboard, Move("e4", "e2")) should not (beLegal)
        (chessboard, Move("e4", "e8")) should not (beLegal)
        (chessboard, Move("e4", "c4")) should not (beLegal)
        (chessboard, Move("e4", "g3")) should not (beLegal)
        (chessboard, Move("e4", "g2")) should not (beLegal)
    }

    it("can take enemy piece") {
      chessboard =>
        chessboard.setPiece(Field.fromString("d5"), Some(BlackRook))
        (chessboard, Move("e4", "d5")) should beLegal
    }

    it("cannot overleap") {
      chessboard =>
        chessboard.setPiece(Field.fromString("e5"), Some(WhitePawn))
        (chessboard, Move("e4", "e5")) should not (beLegal)
    }
  }

  /*test("can be checked") {
    val chessboard = new Chessboard
    Piece
    chessboard.setPiece(Field.fromString("e4"), Some(WhiteKing))
    chessboard.setPiece(Field.fromString("e8"), Some(BlackRook))

    expectResult(true)(WhiteKing.isChecked("e4"))
  }*/
}
