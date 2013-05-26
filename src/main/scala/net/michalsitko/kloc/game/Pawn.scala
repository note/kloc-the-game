package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Pawn extends Piece

case object WhitePawn extends Pawn{
  def getSymbol(): Char = 'P'
}

case object BlackPawn extends Pawn{
  def getSymbol(): Char = WhitePawn.getSymbol().toLower
}