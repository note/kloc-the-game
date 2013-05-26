package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Rook extends Piece{
  val a = 0
}

case object WhiteRook extends Rook{
  def getSymbol(): Char = 'R'
}

case object BlackRook extends Rook{
  def getSymbol(): Char = WhiteRook.getSymbol().toLower
}