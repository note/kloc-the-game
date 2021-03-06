package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Bishop extends Piece {
  def isBishopMove(chessboard: Chessboard, from: Field, to: Field): Boolean = {
    from.sameDiagonal(to) && !chessboard.somethingBetween(from, to)
  }

  def checkMoveCorrect(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    isBishopMove(chessboard, move.from, move.to)
  }

  def getDirections(): List[(Int, Int)] = Bishop.getDirections()
}

object Bishop{
  def getDirections() = {
    List((1, 1), (-1, -1), (-1, 1), (1, -1))
  }
}

object BishopFactory extends PieceFactory {
  def forColor(color: Color): Bishop = {
    color match {
      case White() => WhiteBishop
      case Black() => BlackBishop
    }
  }
}

case object WhiteBishop extends Bishop {
  def getSymbol(): Char = 'B'

  def getColor(): Color = new White
}

case object BlackBishop extends Bishop {
  def getSymbol(): Char = WhiteBishop.getSymbol().toLower

  def getColor(): Color = new Black
}