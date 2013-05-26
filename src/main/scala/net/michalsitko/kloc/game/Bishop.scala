package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Bishop extends Piece

case object WhiteBishop extends Bishop{
  def getSymbol(): Char = 'B'
}

case object BlackBishop extends Bishop{
  def getSymbol(): Char = WhiteBishop.getSymbol().toLower
}