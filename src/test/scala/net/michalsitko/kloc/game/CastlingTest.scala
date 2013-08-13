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
    chessboard
  }

  test("can move forward by 2 fields from start position") {
    val chessboard = fixture
    expectLegal(chessboard, Move("e1", "f1"))
  }
}
