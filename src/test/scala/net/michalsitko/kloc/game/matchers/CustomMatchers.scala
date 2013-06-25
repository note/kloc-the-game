package net.michalsitko.kloc.game.matchers

import org.scalatest.matchers.{MatchResult, Matcher}
import net.michalsitko.game.{Move, Chessboard}

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 6/25/13
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates.
 */
trait CustomMatchers {
  val beLegal =
    new Matcher[(Chessboard, Move)] {
      def apply(left: (Chessboard, Move)) =
        MatchResult(
          left._1.isMoveCorrect(left._2),
          left._2 + " was not legal",
          left._2 + " was legal"
        )
    }
}

object CustomMatchers extends CustomMatchers
