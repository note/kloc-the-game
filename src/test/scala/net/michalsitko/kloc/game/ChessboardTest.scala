package net.michalsitko.kloc.game

import java.io.FileNotFoundException
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
//TODO: stalemate test
//TODO: refactoring
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
    expectResult(null)(chessboard.getPiece("e2").getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece("e4").getOrElse(null))
  }

  // it's job of other classes to verify if move is correct
  test("applyMove allows you to apply incorrect moves") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("b2", "b6"))

    expectResult(null)(chessboard.getPiece("b2").getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece("b6").getOrElse(null))
  }

  test("applyMove allows you to apply incorrect moves 2") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("b2", "b7"))

    expectResult(null)(chessboard.getPiece("b2").getOrElse(null))
    expectResult(WhitePawn)(chessboard.getPiece("b7").getOrElse(null))
  }

  test("somethingBetweenHorizontally") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))

    expectResult(true)(chessboard.somethingBetween("f4", "d4"))
    expectResult(true)(chessboard.somethingBetween("h4", "a4"))
    expectResult(true)(chessboard.somethingBetween("f2", "c2"))

    expectResult(false)(chessboard.somethingBetween("f3", "c3"))
    expectResult(false)(chessboard.somethingBetween("f3", "e3"))
    expectResult(false)(chessboard.somethingBetween("e4", "f4"))
  }

  test("somethingBetween") {
    val chessboard = getInitialPosition()
    chessboard.applyMove(Move("e2", "e4"))

    expectResult(true)(chessboard.somethingBetween("e3", "e6"))
    expectResult(true)(chessboard.somethingBetween("e1", "e7"))
    expectResult(true)(chessboard.somethingBetween("b1", "b3"))

    expectResult(false)(chessboard.somethingBetween("f3", "f6"))
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

        expectResult(expectedResult)(chessboard.somethingBetween(field, anotherField))
      }
    else
      test("nothing between diagonally between " + field + " and " + anotherField) {
        val chessboard = getInitialPosition()
        chessboard.applyMove(Move("e2", "e4"))

        expectResult(expectedResult)(chessboard.somethingBetween(field, anotherField))
      }
  }

  test("there can be stalemate"){
    val chessboard = new Chessboard
    chessboard.setPiece("a1", Some(WhiteKing))
    chessboard.setPiece("c2", Some(BlackQueen))

    expectResult(true)(chessboard.isStalemate(White()))
    expectResult(false)(chessboard.isStalemate(Black()))
  }

  test("there is not stalemate when any piece can move"){
    val chessboard = new Chessboard
    chessboard.setPiece("a1", Some(WhiteKing))
    chessboard.setPiece("c2", Some(BlackQueen))
    chessboard.setPiece("h2", Some(WhitePawn))

    expectResult(false)(chessboard.isStalemate(White()))
    expectResult(false)(chessboard.isStalemate(Black()))
  }

  test("there can be stalemate2"){
    val chessboard = new Chessboard
    chessboard.setPiece("a1", Some(WhiteKing))
    chessboard.setPiece("c2", Some(BlackQueen))
    chessboard.setPiece("h2", Some(WhitePawn))
    chessboard.setPiece("h3", Some(BlackPawn))

    expectResult(true)(chessboard.isStalemate(White()))
    expectResult(false)(chessboard.isStalemate(Black()))
  }
}
