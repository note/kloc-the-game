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
    (from.sameRow(to) || from.sameColumn(to) || from.sameDiagonal(to)) && !chessboard.somethingBetween(from ,to)
  }

  def getDirections(): List[(Int, Int)] = King.getDirections()

  def checkMoveCorrect(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
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