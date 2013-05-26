package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Knight extends Piece

case object WhiteKnight extends Knight{
  def getSymbol(): Char = 'N'
}

case object BlackKnight extends Knight{
  def getSymbol(): Char = WhiteKnight.getSymbol().toLower
}