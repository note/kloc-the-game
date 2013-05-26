package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Piece extends Piece.Value {
  def getSymbol(): Char
}

object Piece extends Enum[Piece]{
  // Not nice, I have to use all of enum instances to be visible (they're not loaded until I use them)
  BlackRook
  WhiteRook
  BlackKnight
  WhiteKnight
  BlackBishop
  WhiteBishop
  BlackQueen
  WhiteQueen
  BlackKing
  WhiteKing
  BlackPawn
  WhitePawn
}