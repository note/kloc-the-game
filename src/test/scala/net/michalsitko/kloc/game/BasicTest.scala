package net.michalsitko.kloc.game

import org.scalatest.{PrivateMethodTester, Informer, FunSuite, FlatSpec}
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 12:08 AM
 * To change this template use File | Settings | File Templates.
 */
class BasicTest extends FunSuite with ShouldMatchers with PrivateMethodTester with PositionGenerator{
  private def expectLegal(chessboard: Chessboard, move: Move) {
    expectResult(true)(chessboard.isMoveCorrect(move))
  }

  def expectIllegal(chessboard: Chessboard, move: Move) {
    expectResult(false)(chessboard.isMoveCorrect(move))
  }

  def getPieceInvoker (chessboard: Chessboard) (row: Int, column: Int) : Option[Piece] = {
    val getPiece = PrivateMethod[Option[Piece]]('getPiece)
    chessboard invokePrivate getPiece(row, column)
  }

/*  behavior of "KlocRules"
  it should "has two chessboards"
  it can "be finished"
  it can "have winner"
  it can "be draw"

  behavior of "Pawn"*/

  test("can be created from String") {
    val field = Field.fromString("a2")
    expectResult(1)(field.row)
    expectResult(0)(field.column)
  }

  test("can be created from String 2") {
    val field = Field.fromString("c8")
    expectResult(7)(field.row)
    expectResult(2)(field.column)
  }


 /* behavior of "Rock"
  it can "move vertically and horizontally" in
  kloc.game("rock cannot overleap") {pending}

  behavior of "Knight"
  it can "move knight moves" in pending
  kloc.game("knight cannot overleap")

  behavior of "Bishop"
  it can "move diagonally" in pending
  kloc.game("bishop cannot overleap")

  behavior of "Queen"
  it can "move vertically, horizontally and diagonally" in pending
  kloc.game("queen cannot overleap")

  behavior of "King"
  it can "move vertically, horizontally and diagonally by 1 field" in pending
  kloc.game("cannot move to field where would be attacked")
  it can "be checked" in pending
  it can "be checkmated" in pending

  behavior of "Castling"
  it can "be performed in some cirucmstances" in pending
  kloc.game("cannot be performed when king has already moved")
  kloc.game("cannot be performed if active rock has already moved")
  kloc.game("cannot be performed if king is checked")
  kloc.game("cannot be performed if king would be attacked on the way")
  kloc.game("cannot be performed if there is any piece between")

  behavior of "Stalemate"
  behavior of "Kloc specific"*/


}
