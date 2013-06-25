package net.michalsitko.kloc.game

import org.scalatest._
import org.scalatest.matchers.{MatchResult, ShouldMatchers, Matcher}
import net.michalsitko.game._
import scala.Some
import net.michalsitko.kloc.game.matchers.CustomMatchers.beLegal
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 6/1/13
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */

class KingTest extends FlatSpec with KingBehaviour{
    private def prepareChessboardWithKing(king: King): Chessboard = {
      // it seems to be odd and actually is. Piece is lazy so we have to invoke is somehow before first use of any of its specializations
      Piece

      val chessboard = new Chessboard
      chessboard.setPiece("e4", Some(king))
      chessboard
    }

  "White King" should behave like king(prepareChessboardWithKing(WhiteKing), White())
  "BlackKing" should behave like king(prepareChessboardWithKing(BlackKing), Black())
}

trait KingBehaviour extends ShouldMatchers with PositionGenerator {   this: FlatSpec =>
  def king (chessboard: Chessboard, colorBeingTested: Color) {
    it can "move all directions by one field" in {
        (chessboard, Move("e4", "e3")) should beLegal
        (chessboard, Move("e4", "e5")) should beLegal
        (chessboard, Move("e4", "d4")) should beLegal
        (chessboard, Move("e4", "f4")) should beLegal
        (chessboard, Move("e4", "f5")) should beLegal
        (chessboard, Move("e4", "f3")) should beLegal
    }

    it can "not move by more than one field" in {
        (chessboard, Move("e4", "e2")) should not (beLegal)
        (chessboard, Move("e4", "e8")) should not (beLegal)
        (chessboard, Move("e4", "c4")) should not (beLegal)
        (chessboard, Move("e4", "g3")) should not (beLegal)
        (chessboard, Move("e4", "g2")) should not (beLegal)
    }

    it can "take enemy piece" in {
        chessboard.setPiece("d5", Some(RookFactory.forColor(colorBeingTested.opposite())))
        (chessboard, Move("e4", "d5")) should beLegal
    }

    it can "not overleap" in {
        chessboard.setPiece("e5", Some(RookFactory.forColor(colorBeingTested)))
        (chessboard, Move("e4", "e5")) should not (beLegal)
    }
  }

  /*test("can be checked") {
    val chessboard = new Chessboard
    Piece
    chessboard.setPiece("e4", Some(WhiteKing))
    chessboard.setPiece("e8", Some(BlackRook))

    expectResult(true)(WhiteKing.isChecked("e4"))
  }*/
}
