package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait King extends Piece

case object WhiteKing extends King{
  def getSymbol(): Char = 'K'
}

case object BlackKing extends King{
  def getSymbol(): Char = WhiteKing.getSymbol().toLower
}
