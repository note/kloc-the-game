package net.michalsitko.kloc.game

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import net.michalsitko.game._
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 6/1/13
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
class KingTest extends FunSuite with ShouldMatchers with PositionGenerator {
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  test("can move all directions by one field") {
    val chessboard = new Chessboard
    Piece
    chessboard.setPiece(Field.fromString("e4"), Some(WhiteKing))

    expectLegal(chessboard, Move("e4", "e3"))
    expectLegal(chessboard, Move("e4", "e5"))
    expectLegal(chessboard, Move("e4", "d4"))
    expectLegal(chessboard, Move("e4", "f4"))
    expectLegal(chessboard, Move("e4", "f5"))
    expectLegal(chessboard, Move("e4", "f3"))
  }

  test("cannot move by more than one field") {
    val chessboard = new Chessboard
    Piece
    chessboard.setPiece(Field.fromString("e4"), Some(WhiteKing))

    expectIllegal(chessboard, Move("e4", "e2"))
    expectIllegal(chessboard, Move("e4", "e8"))
    expectIllegal(chessboard, Move("e4", "c4"))
    expectIllegal(chessboard, Move("e4", "g3"))
    expectIllegal(chessboard, Move("e4", "g2"))
  }

  test("can take enemy piece") {
    val chessboard = new Chessboard
    Piece
    chessboard.setPiece(Field.fromString("e4"), Some(WhiteKing))
    chessboard.setPiece(Field.fromString("d5"), Some(BlackRook))

    expectLegal(chessboard, Move("e4", "d5"))
  }

  test("cannot overleap") {
    val chessboard = new Chessboard
    Piece
    chessboard.setPiece(Field.fromString("e4"), Some(WhiteKing))
    chessboard.setPiece(Field.fromString("e5"), Some(WhitePawn))

    expectIllegal(chessboard, Move("e4", "e5"))
  }

  /*test("can be checked") {
    val chessboard = new Chessboard
    Piece
    chessboard.setPiece(Field.fromString("e4"), Some(WhiteKing))
    chessboard.setPiece(Field.fromString("e8"), Some(BlackRook))

    expectResult(true)(WhiteKing.isChecked("e4"))
  }*/
}
