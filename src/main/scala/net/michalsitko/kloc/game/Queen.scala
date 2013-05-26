package net.michalsitko.game

import net.michalsitko.kloc.game.{Black, White, Color}

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
abstract trait Queen extends Piece{
  def isMoveCorrect(chessboard: Chessboard, move: Move): Boolean = ???
}

case object WhiteQueen extends Queen{
  def getSymbol(): Char = 'Q'

  def getColor(): Color = new White
}

case object BlackQueen extends Queen{
  def getSymbol(): Char = WhiteQueen.getSymbol().toLower

  def getColor(): Color = new Black
}