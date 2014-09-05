package net.michalsitko.kloc.game

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 9/19/13
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
class KlocGameTest extends FunSuite with ShouldMatchers{
  test("White starts a game"){
    val game = new KlocGame

    intercept[IncorrectMoveException]{
      game.applyMove(Move("e7", "e5"))
    }

    game.applyMove(Move("e2", "e4"))
    game.applyMove(Move("e7", "e5"))
  }

  test("Color in turn is altering after each move"){
    val game = new KlocGame

    game.applyMove(Move("e2", "e4"))
    game.applyMove(Move("e7", "e5"))
    game.applyMove(Move("g1", "f3"))
    game.applyMove(Move("g8", "f6"))

    intercept[IncorrectMoveException]{
      game.applyMove(Move("b8", "c6"))
    }
  }

  test("Two moves in row of one color is disallowed"){
    val game = new KlocGame

    game.applyMove(Move("e2", "e4"))

    intercept[IncorrectMoveException]{
      game.applyMove(Move("e4", "e5"))
    }
  }

  test("Both players can propose a draw in any time"){
    val game = new KlocGame

    game.applyMove(Move("e2", "e4"))

    game.proposeDraw(White())
    game.pendingProposition().isDefined should be (true)
    game.rejectDraw(Black())

    game.pendingProposition().isDefined should be (false)

    game.applyMove(Move("e7", "e5"))
    game.proposeDraw(White())
    game.pendingProposition().isDefined should be (true)
  }

  test("Player can accept a draw proposal"){
    val game = new KlocGame

    game.applyMove(Move("e2", "e4"))
    game.proposeDraw(White())

    game.status.isFinished should be (false)

    game.acceptDraw(Black())

    game.status.isFinished should be (true)
    game.status.result match {
      case Some(draw: Draw.type) => assert(1 == 1)
      case _ => fail()
    }
  }

  test("Player can refuse a draw proposal"){
    val game = new KlocGame

    game.applyMove(Move("e2", "e4"))
    game.proposeDraw(White())

    game.rejectDraw(Black())
    game.pendingProposition().isDefined should be (false)
    game.status.isFinished should be (false)
  }

  def canResign(resigningColor: Color){
    val game = new KlocGame

    game.resign(resigningColor)

    game.status.isFinished should be (true)
    game.status.result match {
      case Some(winner: Winner) if winner.color == resigningColor.opposite() => assert(1 == 1)
      case _ => fail()
    }
  }

  test("White can resign"){
    canResign(White())
  }

  test("Black can resign"){
    canResign(Black())
  }

  test("After resignation no move is possible"){
    val game = new KlocGame

    game.resign(White())

    intercept[IncorrectMoveException]{
      game.resign(Black())
    }

    intercept[IncorrectMoveException]{
      game.applyMove(Move("e2", "e4"))
    }
  }

  test("Game may end with checkmate"){
    val game = new KlocGame

    game.applyMove(Move("e2", "e4"))
    game.applyMove(Move("f7", "f5"))
    game.applyMove(Move("d2", "d3"))
    game.applyMove(Move("g7", "g5"))
    val gameStatus = game.applyMove(Move("d1", "h5"))

    gameStatus.isFinished should be (true)

    gameStatus.result match {
      case Some(winner: Winner) if winner.color == White() => assert(1 == 1)
      case _ => fail()
    }
  }

  def justBeforeStalemate(): Chessboard = {
    val chessboard = new Chessboard
    chessboard.setPiece("a1", Some(BlackKing))
    chessboard.setPiece("a2", Some(BlackPawn))
    chessboard.setPiece("e1", Some(WhiteKing))
    chessboard.setPiece("c8", Some(WhiteRook))
    chessboard
  }

  test("Game may end with stalemate"){
    val game = new KlocGame(justBeforeStalemate())

    game.applyMove(Move("c8", "b8"))

    game.status.isFinished() should be (true)
    game.status.result match {
      case Some(draw: Draw.type) => assert(1 == 1)
      case _ => fail()
    }
  }
}
