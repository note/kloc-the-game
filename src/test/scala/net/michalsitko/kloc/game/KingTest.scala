package net.michalsitko.kloc.game

import org.scalatest._
import org.scalatest.matchers.{MatchResult, ShouldMatchers, Matcher}
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


  def fixture(king: King):Chessboard = {
    val chessboard = new Chessboard
    allFields.map(chessboard.setPiece(_, None))
    chessboard.setPiece("e4", Some(king))
    chessboard
  }

  def king(chessboard: Chessboard, kingBeingTested: King) {
    val colorBeingTested: Color = kingBeingTested.getColor()
    val oppositeRook: Rook = RookFactory.forColor(colorBeingTested.opposite())
    val friendlyRook: Rook = RookFactory.forColor(colorBeingTested)
    val friendlyKnight: Knight = KnightFactory.forColor(colorBeingTested)
    val oppositeKnight: Knight = KnightFactory.forColor(colorBeingTested.opposite())
    val friendlyPawn: Pawn = PawnFactory.forColor(colorBeingTested)
    val oppositeKing: King = KingFactory.forColor(colorBeingTested)

    it can "move all directions by one field" in {
      val chessboard = fixture(kingBeingTested)
      val legalFields = List("d3", "e3", "f3", "d4", "f4", "d5", "e5", "f5")
      for (destinationField <- legalFields){
        (chessboard, Move("e4", destinationField)) should beLegal
      }
    }

    it can "not move illegal moves" in {
      val chessboard = fixture(kingBeingTested)
      val legalFields = List("d3", "e3", "f3", "d4", "f4", "d5", "e5", "f5")
      val illegalFields = allFields.filter(!legalFields.contains(_))

      for (destinationField <- illegalFields){
        (chessboard, Move("e4", destinationField)) should not (beLegal)
      }
    }

    it can "take enemy piece" in {
      val chessboard = fixture(kingBeingTested)
      chessboard.setPiece("d5", Some(oppositeRook))
      (chessboard, Move("e4", "d5")) should beLegal
    }

    it can "not overleap" in {
      val chessboard = fixture(kingBeingTested)
      chessboard.setPiece("e5", Some(friendlyRook))
      (chessboard, Move("e4", "e5")) should not(beLegal)
    }

    it can "be checked by rook" in {
      val chessboard = fixture(kingBeingTested)
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

    it can "be checked by knight" in {
      val chessboard = fixture(kingBeingTested)
      val attackingFields = List("d2", "f2", "g3", "g5", "f6", "d6", "c5", "c3")

      for (attackingField <- attackingFields){
        chessboard.setPiece(attackingField, Some(oppositeKnight))
        expectResult(true)(kingBeingTested.isChecked(chessboard, "e4"))
        chessboard.setPiece(attackingField, None)
      }

      val nonAttackingFields = allFields.filter(!attackingFields.contains(_))

      for (attackingField <- nonAttackingFields){
        chessboard.setPiece(attackingField, Some(oppositeKnight))
        expectResult(false)(kingBeingTested.isChecked(chessboard, "e4"))
        chessboard.setPiece(attackingField, None)
      }
    }

    it can "be checked by pinned piece" in {
      val chessboard = fixture(kingBeingTested)

      // Rook e1 is pinned
      chessboard.setPiece("e1", Some(oppositeRook))
      chessboard.setPiece("a1", Some(oppositeRook))
      chessboard.setPiece("f1", Some(oppositeRook))

      expectResult(true)(kingBeingTested.isChecked(chessboard, "e4"))
    }

    it can "not be checked by own knight" in {
      val chessboard = fixture(kingBeingTested)
      val attackingFields = List("d2", "f2", "g3", "g5", "f6", "d6", "c5", "c3")

      for (attackingField <- attackingFields){
        chessboard.setPiece(attackingField, Some(friendlyKnight))
        expectResult(false)(kingBeingTested.isChecked(chessboard, "e4"))
        chessboard.setPiece(attackingField, None)
      }
    }

    it can "be checkmated" in {
      val chessboard = fixture(kingBeingTested)
      chessboard.setPiece("d1", Some(oppositeRook))
      chessboard.setPiece("e1", Some(oppositeRook))
      chessboard.setPiece("f1", Some(oppositeRook))

      expectResult(true)(kingBeingTested.isCheckmated(chessboard, "e4"))
    }

    it can "be shielded against checkmate" in {
      val chessboard = fixture(kingBeingTested)
      chessboard.setPiece("d1", Some(oppositeRook))
      chessboard.setPiece("e1", Some(oppositeRook))
      chessboard.setPiece("f1", Some(oppositeRook))

      // Rook f2 shields againts oppositeRook on f1 (so King can escape)
      chessboard.setPiece("f2", Some(friendlyKnight))
      expectResult(false)(kingBeingTested.isCheckmated(chessboard, "e4"))
    }

    it can "be shielded against checkmate 2" in {
      val chessboard = fixture(kingBeingTested)
      chessboard.setPiece("d1", Some(oppositeRook))
      chessboard.setPiece("e1", Some(oppositeRook))
      chessboard.setPiece("f1", Some(oppositeRook))

      // Rook h2 shields againts oppositeRook on e1 by Move(h2, e2)
      chessboard.setPiece("h2", Some(friendlyRook))
      expectResult(false)(kingBeingTested.isCheckmated(chessboard, "e4"))
    }

    it should "not be checkmated when attacker can be taken" in {
      val chessboard = fixture(kingBeingTested)
      chessboard.setPiece("d1", Some(oppositeRook))
      chessboard.setPiece("e1", Some(oppositeRook))
      chessboard.setPiece("f1", Some(oppositeRook))

      // Knight c2 can take Rook e1
      chessboard.setPiece("c2", Some(friendlyKnight))
      expectResult(false)(kingBeingTested.isCheckmated(chessboard, "e4"))
    }

    it can "be checkmated 2" in {
      val chessboard = fixture(kingBeingTested)
      chessboard.setPiece("d1", Some(oppositeRook))
      chessboard.setPiece("e1", Some(oppositeRook))
      chessboard.setPiece("e8", Some(oppositeRook))
      chessboard.setPiece("f1", Some(oppositeRook))

      chessboard.setPiece("c2", Some(friendlyKnight))
      expectResult(true)(kingBeingTested.isCheckmated(chessboard, "e4"))
    }
  }

}
