package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 8/12/13
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
case class GameState (shortCastlingLegal: Map[Color, Boolean], longCastlingLegal: Map[Color, Boolean], enpassantLegal: Map[Color, Option[Char]]) {

}

object GameState {
  def default() = GameState(Map(White() -> true, Black() -> true), Map(White() -> true, Black() -> true), Map(White() -> None, Black() -> None))
}
