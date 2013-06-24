package net.michalsitko.kloc.game

import net.michalsitko.game.{Move, Chessboard}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */

//TODO: refaktoryzacja testow
//TODO: http://doc.scalatest.org/1.9.1/index.html#org.scalatest.fixture.FunSpec, http://jlaskowski.blogspot.com/2013/06/taskassin-na-githubie-dzien-3-5-trait.html
class BishopTest extends FunSuite with ShouldMatchers with PositionGenerator {
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

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
}
