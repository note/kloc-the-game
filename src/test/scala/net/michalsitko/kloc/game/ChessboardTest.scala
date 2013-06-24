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
    chessboard.applyMove(Move("e2", "e4"))
    expectResult(null)(chessboard.getPiece(Field.fromString("e2")).getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece(Field.fromString("e4")).getOrElse(null))
  }

  // it's job of other classes to verify if move is correct
  test("applyMove allows you to apply incorrect moves") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("b2", "b6"))

    expectResult(null)(chessboard.getPiece(Field.fromString("b2")).getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece(Field.fromString("b6")).getOrElse(null))
  }

  test("applyMove allows you to apply incorrect moves 2") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("b2", "b7"))

    expectResult(null)(chessboard.getPiece(Field.fromString("b2")).getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece(Field.fromString("b7")).getOrElse(null))
  }

  test("somethingBetweenHorizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))

    expectResult(true)(chessboard.somethingBetweenHorizontally(Field.fromString("f4"), Field.fromString("d4")))
    expectResult(true)(chessboard.somethingBetweenHorizontally(Field.fromString("h4"), Field.fromString("a4")))
    expectResult(true)(chessboard.somethingBetweenHorizontally(Field.fromString("f2"), Field.fromString("c2")))

    expectResult(false)(chessboard.somethingBetweenHorizontally(Field.fromString("f3"), Field.fromString("c3")))
    expectResult(false)(chessboard.somethingBetweenHorizontally(Field.fromString("f3"), Field.fromString("e3")))
    expectResult(false)(chessboard.somethingBetweenHorizontally(Field.fromString("e4"), Field.fromString("f4")))
  }

  test("somethingBetweenHorizontally throw IllegalArgumentException") {
    val chessboard = getInitialPosition()

    intercept[IllegalArgumentException]{
      expectResult(true)(chessboard.somethingBetweenHorizontally(Field.fromString("f4"), Field.fromString("d6")))
    }
  }

  test("somethingBetweenVertically") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))

    expectResult(true)(chessboard.somethingBetweenVertically(Field.fromString("e3"), Field.fromString("e6")))
    expectResult(true)(chessboard.somethingBetweenVertically(Field.fromString("e1"), Field.fromString("e7")))
    expectResult(true)(chessboard.somethingBetweenVertically(Field.fromString("b1"), Field.fromString("b3")))

    expectResult(false)(chessboard.somethingBetweenVertically(Field.fromString("f3"), Field.fromString("f6")))
  }

  test("somethingBetweenVertically throw IllegalArgumentException") {
    val chessboard = getInitialPosition()

    intercept[IllegalArgumentException]{
      expectResult(true)(chessboard.somethingBetweenVertically(Field.fromString("f4"), Field.fromString("d6")))
    }
  }

  for((field, anotherField, expectedResult) <- List(
    ("c1", "e3", true),
    ("f8", "c5", true),
    ("d3", "f5", true),
    ("f1", "b5", false),
    ("a2", "b3", false)
  )){
    if (expectedResult)
      test("something between diagonally between " + field + " and " + anotherField) {
        val chessboard = getInitialPosition()
        chessboard.applyMove(Move("e2", "e4"))

        expectResult(expectedResult)(chessboard.somethingBetweenDiagonally(Field.fromString(field), Field.fromString(anotherField)))
      }
    else
      test("nothing between diagonally between " + field + " and " + anotherField) {
        val chessboard = getInitialPosition()
        chessboard.applyMove(Move("e2", "e4"))

        expectResult(expectedResult)(chessboard.somethingBetweenDiagonally(Field.fromString(field), Field.fromString(anotherField)))
      }
  }

  test("somethingBetweenDiagonally throw IllegalArgumentException") {
    val chessboard = getInitialPosition()

    intercept[IllegalArgumentException]{
      expectResult(true)(chessboard.somethingBetweenDiagonally(Field.fromString("e4"), Field.fromString("e2")))
    }
  }
}
