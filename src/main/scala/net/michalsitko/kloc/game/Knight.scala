package net.michalsitko.game

import net.michalsitko.kloc.game.{Black, White, Color}

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Knight extends Piece{

  def isMoveCorrect(chessboard: Chessboard, move: Move): Boolean = {
    if (super.areBasicCriteriaSatisfied(chessboard, move))
      move.to.isKnightAccessible(move.from)
    else
      false
  }
}

case object WhiteKnight extends Knight{
  def getSymbol(): Char = 'N'

  def getColor(): Color = new White
}

case object BlackKnight extends Knight{
  def getSymbol(): Char = WhiteKnight.getSymbol().toLower

  def getColor(): Color = new Black
}