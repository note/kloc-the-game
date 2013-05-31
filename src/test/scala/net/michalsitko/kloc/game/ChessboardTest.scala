package net.michalsitko.kloc.game

import java.io.FileNotFoundException
import net.michalsitko.game._
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.PrivateMethodTester.PrivateMethod
import org.scalatest.PrivateMethodTester

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/31/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
class ChessboardTest extends FunSuite with ShouldMatchers with PrivateMethodTester with PositionGenerator{
  def getPieceInvoker (chessboard: Chessboard) (row: Int, column: Int) : Option[Piece] = {
    val getPiece = PrivateMethod[Option[Piece]]('getPiece)
    chessboard invokePrivate getPiece(row, column)
  }

  test("chessboard can be loaded from file") {
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

  test("loadFromFile throw FileNotFoundException for non-existing file"){
    intercept[FileNotFoundException]{
      val chessboard = Chessboard.loadFromFile("/non_existing_file.position")
    }
  }

  test("applyMove should change state of chessboard") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(new Move("e2", "e4"))
    expectResult(null)(chessboard.getPiece(Field.fromString("e2")).getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece(Field.fromString("e4")).getOrElse(null))
  }

  // it's job of other classes to verify if move is correct
  test("applyMove allows you to apply incorrect moves") {
    val chessboard = getInitialPosition()

    chessboard.applyMove(new Move("b2", "b6"))
    expectResult(null)(chessboard.getPiece(Field.fromString("b2")).getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece(Field.fromString("b6")).getOrElse(null))
  }

  test("applyMove allows you to apply incorrect moves 2") {
    val chessboard = getInitialPosition()

    chessboard.applyMove(new Move("b2", "b7"))
    expectResult(null)(chessboard.getPiece(Field.fromString("b2")).getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece(Field.fromString("b7")).getOrElse(null))
  }
}
