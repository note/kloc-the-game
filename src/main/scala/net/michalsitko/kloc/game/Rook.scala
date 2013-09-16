package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Rook extends Piece {
  def isRookMove(chessboard: Chessboard, from: Field, to: Field): Boolean = {
    (from.sameRow(to) || from.sameColumn(to)) && !chessboard.somethingBetween(from, to)
  }

  def checkMoveCorrect(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    isRookMove(chessboard, move.from, move.to)
  }

  def getDirections(): List[(Int, Int)] = Rook.getDirections()
}

object Rook{
  def getDirections(): List[(Int, Int)] = {
    List((0, 1), (0, -1), (1, 0), (-1, 0))
  }
}

object RookFactory extends PieceFactory {
  def forColor(color: Color): Rook = {
    color match {
      case White() => WhiteRook
      case Black() => BlackRook
    }
  }
}

case object WhiteRook extends Rook {
  def getSymbol(): Char = 'R'

  def getColor(): Color = new White
}

case object BlackRook extends Rook {
  def getSymbol(): Char = WhiteRook.getSymbol().toLower

  def getColor(): Color = new Black
}