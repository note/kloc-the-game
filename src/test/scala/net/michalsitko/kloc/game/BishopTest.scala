package net.michalsitko.kloc.game

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */

//TODO: refaktoryzacja testow
//TODO: http://doc.scalatest.org/1.9.1/index.html#org.scalatest.fixture.FunSpec, http://jlaskowski.blogspot.com/2013/06/taskassin-na-githubie-dzien-3-5-trait.html
class BishopTest extends FunSuite with ShouldMatchers with PositionGenerator with MoveAssertions {

  test("can move diagonally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))
    chessboard.applyMove(Move("d7", "d6"))

    expectLegal(chessboard, Move("f1", "e2"))
    expectLegal(chessboard, Move("f1", "c4"))
    expectLegal(chessboard, Move("c8", "d7"))
    expectLegal(chessboard, Move("c8", "g4"))
  }

  test("can take enemy piece") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))
    chessboard.applyMove(Move("b7", "b5"))

    expectLegal(chessboard, Move("f1", "b5"))
  }

  test("cannot move other way than diagonally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))
    chessboard.applyMove(Move("f2", "f4"))

    expectIllegal(getInitialPosition(), Move("f1", "f3"))
    expectIllegal(getInitialPosition(), Move("f1", "f2"))
    expectIllegal(getInitialPosition(), Move("f1", "g3"))
  }

  test("cannot overleap") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))
    chessboard.applyMove(Move("g7", "g5"))
    chessboard.applyMove(Move("d2", "d3"))

    expectIllegal(chessboard, Move("f1", "d3"))
    expectIllegal(chessboard, Move("f1", "c4"))
    expectIllegal(chessboard, Move("c1", "h6"))
  }

  test("can be pinned") {
    val chessboard = new Chessboard
    chessboard.setPiece("e1", Some(WhiteKing))
    chessboard.setPiece("b4", Some(BlackBishop))
    chessboard.setPiece("d2", Some(WhiteBishop))

    expectResult(true){chessboard.isPinned(Move("d2", "c1"))}
    expectResult(true){chessboard.isPinned(Move("d2", "e3"))}
    expectResult(false){chessboard.isPinned(Move("d2", "c3"))}
  }

  test("isAnyMovePossible can return false"){
    val chessboard = new Chessboard
    chessboard.setPiece("a1", Some(WhiteBishop))
    chessboard.setPiece("b2", Some(WhitePawn))

    expectResult(false)(WhiteBishop.isAnyMovePossible(chessboard, "a1"))
  }

  test("isAnyMovePossible can return true"){
    val chessboard = new Chessboard
    chessboard.setPiece("a1", Some(WhiteBishop))

    expectResult(true)(WhiteBishop.isAnyMovePossible(chessboard, "a1"))
  }
}
