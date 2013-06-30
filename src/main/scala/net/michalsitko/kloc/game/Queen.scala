package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
abstract trait Queen extends Piece {
  def isQueenMove(chessboard: Chessboard, from: Field, to: Field): Boolean = {
    if (from.sameRow(to))
      return !chessboard.somethingBetweenHorizontally(from, to)

    if (from.sameColumn(to))
      return !chessboard.somethingBetweenVertically(from, to)

    if (from.sameDiagonal(to))
      return !chessboard.somethingBetweenDiagonally(from, to)

    false
  }

  def checkMoveCorrect(chessboard: Chessboard, move: Move): Boolean = {
    isQueenMove(chessboard, move.from, move.to)
  }
}

object QueenFactory extends PieceFactory {
  def forColor(color: Color): Queen = {
    color match {
      case White() => WhiteQueen
      case Black() => BlackQueen
    }
  }
}

case object WhiteQueen extends Queen {
  def getSymbol(): Char = 'Q'

  def getColor(): Color = new White
}

case object BlackQueen extends Queen {
  def getSymbol(): Char = WhiteQueen.getSymbol().toLower

  def getColor(): Color = new Black
}