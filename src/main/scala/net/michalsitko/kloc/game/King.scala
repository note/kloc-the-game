package net.michalsitko.game

import net.michalsitko.kloc.game.{Black, White, Color}

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait King extends Piece{
  def isKingMove(chessboard: Chessboard, from: Field, to: Field): Boolean = {
    val columnDiff = (from.column - to.column).abs
    val rowDiff = (from.row - to.row).abs
    (rowDiff > 0 || columnDiff > 0) && rowDiff < 2 && columnDiff < 2
  }

  def isMoveCorrect(chessboard: Chessboard, move: Move): Boolean = {
    if (super.areBasicCriteriaSatisfied(chessboard, move))
      isKingMove(chessboard, move.from, move.to)
    else
      false
  }
}

case object WhiteKing extends King{
  def getSymbol(): Char = 'K'

  def getColor(): Color = new White
}

case object BlackKing extends King{
  def getSymbol(): Char = WhiteKing.getSymbol().toLower

  def getColor(): Color = new Black
}
