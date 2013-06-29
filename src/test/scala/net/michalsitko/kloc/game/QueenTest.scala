package net.michalsitko.kloc.game

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.PM
 * User: michal
 * Date: 5/31/13
 * Time: 3:11 
 * To change this template use File | Settings | File Templates.
 */
class QueenTest extends FunSuite with ShouldMatchers with PositionGenerator{
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  test("can move vertically") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("d2", "e3"))
    chessboard.applyMove(Move("d7", "e6"))

    expectLegal(chessboard, Move("d1", "d2"))
    expectLegal(chessboard, Move("d1", "d6"))
    expectLegal(chessboard, Move("d8", "d7"))
    expectLegal(chessboard, Move("d8", "d4"))
  }

  test("can move horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("d2", "d4"))
    chessboard.applyMove(Move("d1", "d3"))

    expectLegal(chessboard, Move("d3", "b3"))
    expectLegal(chessboard, Move("d3", "g3"))
  }

  test("can move diagonally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "d4"))
    chessboard.applyMove(Move("c2", "c3"))

    expectLegal(chessboard, Move("d1", "c2"))
    expectLegal(chessboard, Move("d1", "g4"))
  }

  test("can take enemy piece") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("d2", "e3"))
    chessboard.applyMove(Move("d7", "e6"))

    expectLegal(chessboard, Move("d1", "d8"))
    expectLegal(chessboard, Move("d8", "d1"))
  }

  test("cannot move other way than diagonally, vertically and horizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("d2", "d4"))
    chessboard.applyMove(Move("e2", "e4"))

    expectIllegal(getInitialPosition(), Move("d1", "e3"))
    expectIllegal(getInitialPosition(), Move("d1", "c3"))
  }

  test("cannot overleap") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("d2", "d4"))
    chessboard.applyMove(Move("d7", "e6"))

    expectIllegal(chessboard, Move("d1", "d4"))
    expectIllegal(chessboard, Move("d1", "d5"))
    expectIllegal(chessboard, Move("d1", "a4"))
    expectIllegal(chessboard, Move("d8", "d2"))
    expectIllegal(chessboard, Move("d8", "d1"))
  }

  test("can be pinned") {
    val chessboard = new Chessboard
    chessboard.setPiece("e1", Some(WhiteKing))
    chessboard.setPiece("b4", Some(BlackBishop))
    chessboard.setPiece("d2", Some(WhiteQueen))

    expectResult(true){chessboard.isPinned(Move("d2", "f4"))}
    expectResult(true){chessboard.isPinned(Move("d2", "d4"))}
    expectResult(false){chessboard.isPinned(Move("d2", "c3"))}
    expectResult(false){chessboard.isPinned(Move("d2", "b4"))}
  }
}
