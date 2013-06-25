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

class KingTest extends FlatSpec with KingBehaviour {
  private def prepareChessboardWithKing(king: King): Chessboard = {
    // it seems to be odd and in fact it is. Piece is lazy so we have to invoke is somehow before first use of any of its specializations
    Piece

    val chessboard = new Chessboard
    chessboard.setPiece("e4", Some(king))
    chessboard
  }

  "White King" should behave like king(prepareChessboardWithKing(WhiteKing), WhiteKing)
  "BlackKing" should behave like king(prepareChessboardWithKing(BlackKing), BlackKing)
}

trait KingBehaviour extends ShouldMatchers with PositionGenerator {
  this: FlatSpec =>

  def king(chessboard: Chessboard, kingBeingTested: King) {
    val colorBeingTested: Color = kingBeingTested.getColor()
    val oppositeRook: Rook = RookFactory.forColor(colorBeingTested.opposite())
    val friendlyRook: Rook = RookFactory.forColor(colorBeingTested)

    it can "move all directions by one field" in {
      val legalFields = List("d3", "e3", "f3", "d4", "f4", "d5", "e5", "f5")
      for (destinationField <- legalFields){
        (chessboard, Move("e4", destinationField)) should beLegal
      }
    }

    it can "not move illegal moves" in {
      val legalFields = List("d3", "e3", "f3", "d4", "f4", "d5", "e5", "f5")
      val illegalFields = allFields.filter(!legalFields.contains(_))

      for (destinationField <- illegalFields){
        (chessboard, Move("e4", destinationField)) should not (beLegal)
      }
    }

    it can "take enemy piece" in {
      chessboard.setPiece("d5", Some(oppositeRook))
      (chessboard, Move("e4", "d5")) should beLegal
    }

    it can "not overleap" in {
      chessboard.setPiece("e5", Some(friendlyRook))
      (chessboard, Move("e4", "e5")) should not(beLegal)
    }

    it can "be checked" in {
      val attackingFields = List("e1", "e2", "e3", "e5", "e6", "e7", "e8", "a4", "b4", "c4", "d4", "f4", "g4", "h4")

      for (attackingField <- attackingFields){
        chessboard.setPiece(attackingField, Some(oppositeRook))
        expectResult(true)(kingBeingTested.isChecked(chessboard, "e4"))
        chessboard.setPiece(attackingField, None)
      }

      val nonAttackingFields = allFields.filter(!attackingFields.contains(_))

      for (attackingField <- nonAttackingFields){
        chessboard.setPiece(attackingField, Some(oppositeRook))
        expectResult(false)(kingBeingTested.isChecked(chessboard, "e4"))
        chessboard.setPiece(attackingField, None)
      }
    }

    it can "be checkmated" in pending
  }

}
