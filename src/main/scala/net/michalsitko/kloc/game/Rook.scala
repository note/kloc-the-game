package net.michalsitko.game

import net.michalsitko.kloc.game.{Black, White, Color}

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Rook extends Piece{
  def isMoveCorrect(chessboard: Chessboard, move: Move): Boolean = ???
}

case object WhiteRook extends Rook{
  def getSymbol(): Char = 'R'

  def getColor(): Color = new White
}

case object BlackRook extends Rook{
  def getSymbol(): Char = WhiteRook.getSymbol().toLower

  def getColor(): Color = new Black
}