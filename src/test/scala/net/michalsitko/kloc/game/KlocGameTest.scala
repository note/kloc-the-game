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
  test("Two moves in row of one color is disallowed"){
    val game = new KlocGame

    intercept[IncorrectMoveException]{
      game.applyMove(Move("e2", "e4"))
      game.applyMove(Move("e4", "e5"))
    }
  }
}
