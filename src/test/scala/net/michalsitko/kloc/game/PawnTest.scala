package net.michalsitko.kloc.game

import org.scalatest.{PrivateMethodTester, FunSuite}
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
class PawnTest extends FunSuite with ShouldMatchers with PositionGenerator with MoveAssertions {

  test("can move forward by 2 fields from start position") {
    expectLegal(getInitialPosition(), Move("e2", "e4"))
    expectLegal(getInitialPosition(), Move("e7", "e5"))
    expectLegal(getInitialPosition(), Move("a7", "a5"))

    expectIllegal(getInitialPosition(), Move("e2", "d4"))
    expectIllegal(getInitialPosition(), Move("e2", "e5"))
  }

  test("can move forward by 1 field from start position") {
    expectLegal(getInitialPosition(), Move("e2", "e3"))
    expectLegal(getInitialPosition(), Move("e7", "e6"))
  }

  test("cannot move forward by 2 fields from non-start position"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e3"))
    chessboard.applyMove(Move("e7", "e6"))

    expectIllegal(chessboard, Move("e3", "e5"))
    expectIllegal(chessboard, Move("e7", "e6"))
  }

  test("move forward by 1 field from non-start position"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e3"))
    chessboard.applyMove(Move("e7", "e6"))

    expectLegal(chessboard, Move("e3", "e4"))
    expectLegal(chessboard, Move("e6", "e5"))
    expectIllegal(chessboard, Move("e3", "e2"))
    expectIllegal(chessboard, Move("e6", "e7"))
  }

  test("take on diagonal 1 field forward"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))
    chessboard.applyMove(Move("d7", "d5"))

    expectLegal(chessboard, Move("e4", "d5"))
    expectLegal(chessboard, Move("d5", "e4"))
    expectIllegal(chessboard, Move("e4", "f5"))
    expectIllegal(chessboard, Move("d5", "c4"))
  }

  test("enpassant right after enemy pawn move"){
    val chessboard = getTypicalPosition()
  }

  test("cannot enpassant later") {
    val chessboard = getTypicalPosition()

  }

  test("be promoted to any piece") {
    val chessboard = new Chessboard
    chessboard.setPiece("a7", Some(WhitePawn))
    expectLegal(chessboard, Move("a7", "a8", WhiteQueen))
    expectLegal(chessboard, Move("a7", "a8", WhiteRook))
    expectLegal(chessboard, Move("a7", "a8", WhiteBishop))
    expectLegal(chessboard, Move("a7", "a8", WhiteKnight))

    expectIllegal(chessboard, Move("a7", "a8", WhiteKing))
    expectIllegal(chessboard, Move("a7", "a8", WhitePawn))
    expectIllegal(chessboard, Move("a7", "a8"))
  }

  test("cannot overleap when forward by one field"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))
    chessboard.applyMove(Move("e7", "e5"))

    expectIllegal(chessboard, Move("e4", "e5"))
    expectIllegal(chessboard, Move("e5", "e4"))
  }

  test("cannot overleap when forward by two fields"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e6"))

    expectIllegal(chessboard, Move("e7", "e5"))
  }

  test("can be pinned") {
    val chessboard = new Chessboard
    chessboard.setPiece("e1", Some(WhiteKing))
    chessboard.setPiece("b4", Some(BlackBishop))
    chessboard.setPiece("d2", Some(WhitePawn))

    expectResult(true){chessboard.isPinned(Move("d2", "d3"))}
    expectResult(true){chessboard.isPinned(Move("d2", "d4"))}
  }

  test("isAnyMovePossible can return true"){
    val chessboard = new Chessboard
    chessboard.setPiece("e2", Some(WhitePawn))
    chessboard.setPiece("d3", Some(BlackBishop))

    expectResult(true)(WhitePawn.isAnyMovePossible(chessboard, "e2"))
  }

  test("isAnyMovePossible can return false"){
    val chessboard = new Chessboard
    chessboard.setPiece("e2", Some(WhitePawn))
    chessboard.setPiece("e3", Some(BlackBishop))

    expectResult(false)(WhitePawn.isAnyMovePossible(chessboard, "h8"))
  }
}
