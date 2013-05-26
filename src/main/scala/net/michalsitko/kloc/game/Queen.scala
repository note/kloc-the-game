package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
abstract trait Queen extends Piece

case object WhiteQueen extends Queen{
  def getSymbol(): Char = 'Q'
}

case object BlackQueen extends Queen{
  def getSymbol(): Char = WhiteQueen.getSymbol().toLower
}