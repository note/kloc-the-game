package net.michalsitko.kloc.game

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 8/13/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
class CastlingTest extends FunSuite with ShouldMatchers with MoveAssertions with PositionGenerator{
  def fixture():Chessboard = {
    val chessboard = new Chessboard
    allFields.map(chessboard.setPiece(_, None))
    chessboard.setPiece("e1", Some(WhiteKing))
    chessboard.setPiece("a1", Some(WhiteRook))
    chessboard.setPiece("h1", Some(WhiteRook))
    chessboard
  }

  test("It is possible to perform a short castling") {
    val chessboard = fixture
    expectLegal(chessboard, Move("e1", "g1"))
  }

  test("It is possible to perform a long castling") {
    val chessboard = fixture
    expectLegal(chessboard, Move("e1", "c1"))
    expectIllegal(chessboard, Move("e1", "b1"))
    expectIllegal(chessboard, Move("e1", "c2"))
  }

  test("It is impossible to perform short castling if it is not the first move of the king") {
    val chessboard = Chessboard.initialPosition()
    val gameState = chessboard.applyMove(Move("e2", "e4"))
    gameState.longCastlingLegal(White()) should be (true)
    gameState.shortCastlingLegal(White()) should be (true)

    val gameState2 = chessboard.applyMove(Move("e1", "e2"))
    gameState.longCastlingLegal(White()) should be (false)
    gameState.shortCastlingLegal(White()) should be (false)
  }

  test("It is impossible to perform short castling if it is not the first move of the rook") {
    val chessboard = Chessboard.initialPosition()
    val gameState = chessboard.applyMove(Move("a2", "a4"))
    gameState.longCastlingLegal(White()) should be (true)
    gameState.shortCastlingLegal(White()) should be (true)

    val gameState2 = chessboard.applyMove(Move("a1", "a2"))
    gameState.longCastlingLegal(White()) should be (false)
    gameState.shortCastlingLegal(White()) should be (true)
  }

  test("It is impossible to perform short castling when there is some piece between") {
    val chessboard = fixture()
    chessboard.setPiece("g1", Some(WhiteKnight))
    expectIllegal(chessboard, Move("e1", "g1"))
    expectLegal(chessboard, Move("e1", "c1"))
  }

  test("It is impossible to perform long castling when there is some piece between") {
    val chessboard = fixture()
    chessboard.setPiece("b1", Some(WhiteKnight))
    expectIllegal(chessboard, Move("e1", "c1"))
    expectLegal(chessboard, Move("e1", "g1"))
  }

  test("It is impossible to perform short castling when king is checked") {
    val chessboard = fixture()
    chessboard.setPiece("b4", Some(BlackBishop))
    expectIllegal(chessboard, Move("e1", "g1"))
  }

  test("It is impossible to perform long castling when king is checked") {
    val chessboard = fixture()
    chessboard.setPiece("b4", Some(BlackBishop))
    expectIllegal(chessboard, Move("e1", "c1"))
  }

  test("It is impossible to perform short castling when any field king travels is checked") {
    val chessboard = fixture()
    chessboard.setPiece("c4", Some(BlackBishop))
    expectIllegal(chessboard, Move("e1", "g1"))
  }

  test("It is impossible to perform long castling when any field king travels is checked") {
    val chessboard = fixture()
    chessboard.setPiece("g4", Some(BlackBishop))
    expectIllegal(chessboard, Move("e1", "c1"))
  }

  test("It is possible to perform short castling when some field rook travels is attacked") {
    val chessboard = fixture()
    chessboard.setPiece("e4", Some(BlackBishop))
    expectLegal(chessboard, Move("e1", "g1"))
  }

  test("It is possible to perform long castling when some field rook travels is attacked") {
    val chessboard = fixture()
    chessboard.setPiece("e4", Some(BlackBishop))
    expectLegal(chessboard, Move("e1", "c1"))
  }

  test("After performed short castling rook is in correct place") {
    val chessboard = fixture()
    chessboard.applyMove(Move("e1", "g1"))
    expectResult(Some(WhiteKing))(chessboard.getPiece("g1"))
    expectResult(Some(WhiteRook))(chessboard.getPiece("f1"))
  }

  test("After performed long castling rook is in correct place") {
    val chessboard = fixture()
    chessboard.applyMove(Move("e1", "c1"))
    expectResult(Some(WhiteKing))(chessboard.getPiece("c1"))
    expectResult(Some(WhiteRook))(chessboard.getPiece("d1"))
  }
}
