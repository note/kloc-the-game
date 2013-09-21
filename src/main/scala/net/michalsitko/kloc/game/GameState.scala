package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 8/12/13
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
case class GameState(gameStateForWhite: GameStateForColor, gameStateForBlack: GameStateForColor){
  def forColor(color: Color): GameStateForColor = color match {
    case White() => gameStateForWhite
    case Black() => gameStateForBlack
  }

  def next(colorToChange: Color, newGameStateForColor: GameStateForColor): GameState = {
    colorToChange match {
      case White() => GameState(newGameStateForColor, gameStateForBlack)
      case Black() => GameState(gameStateForWhite, newGameStateForColor)
    }
  }

  def noCastlings(): GameState = {
    val white = GameStateForColor(false, false, this.forColor(White()).enpassantColumn)
    val black = GameStateForColor(false, false, this.forColor(Black()).enpassantColumn)
    GameState(white, black)
  }
}

object GameState {
  def default() = GameState(GameStateForColor.default(), GameStateForColor.default())
}

case class GameStateForColor(shortCastlingEnabled: Boolean, longCastlingEnabled: Boolean, enpassantColumn: Option[Char])

object GameStateForColor{
  def default() = GameStateForColor(true, true, None)
}
