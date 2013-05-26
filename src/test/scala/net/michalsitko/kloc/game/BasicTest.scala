package net.michalsitko.kloc.game

import org.scalatest.{PrivateMethodTester, Informer, FunSuite, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import net.michalsitko.game._
import net.michalsitko.game.Move

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

  test("chessboard can be load from file") {
    val chessboard = Chessboard.loadFromFile("/initial.position")
    val getPiece = getPieceInvoker(chessboard)_
    expectResult(WhiteRook)(getPiece(0, 0).getOrElse(null))
    expectResult(WhiteKnight)(getPiece(0, 1).getOrElse(null))
    expectResult(WhitePawn)(getPiece(1, 7).getOrElse(null))
    expectResult(BlackQueen)(getPiece(7, 3).getOrElse(null))
    expectResult(BlackKing)(getPiece(7, 4).getOrElse(null))
    expectResult(BlackPawn)(getPiece(6, 5).getOrElse(null))

    expectResult(null)(getPiece(2, 0).getOrElse(null))
    expectResult(null)(getPiece(4, 5).getOrElse(null))
  }

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
    val chessboard = getPromotionPosition()
  }

  test("cannot overleap"){
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))
    chessboard.applyMove(Move("e7", "e5"))pending
    expectIllegal(chessboard, Move("e4", "e5"))
    expectIllegal(chessboard, Move("e5", "e4"))
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
