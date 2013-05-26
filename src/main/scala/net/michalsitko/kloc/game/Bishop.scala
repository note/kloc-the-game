package net.michalsitko.game

import net.michalsitko.kloc.game.{Black, White, Color, Colored}

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Bishop extends Piece{
  def isMoveCorrect(chessboard: Chessboard, move: Move): Boolean = ???
}

case object WhiteBishop extends Bishop{
  def getSymbol(): Char = 'B'

  def getColor(): Color = new White
}

case object BlackBishop extends Bishop{
  def getSymbol(): Char = WhiteBishop.getSymbol().toLower

  def getColor(): Color = new Black
}