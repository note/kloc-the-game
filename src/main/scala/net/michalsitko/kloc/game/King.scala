package net.michalsitko.game

import net.michalsitko.kloc.game.{Black, White, Color}
import java.util

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait King extends Piece {
  def isKingMove(chessboard: Chessboard, from: Field, to: Field): Boolean = {
    val columnDiff = (from.column - to.column).abs
    val rowDiff = (from.row - to.row).abs
    (rowDiff > 0 || columnDiff > 0) && rowDiff < 2 && columnDiff < 2
  }

  def isMoveCorrect(chessboard: Chessboard, move: Move): Boolean = {
    if (super.areBasicCriteriaSatisfied(chessboard, move))
      isKingMove(chessboard, move.from, move.to)
    else
      false
  }

  def isChecked(chessboard: Chessboard, field: Field): Boolean = getCheckingPieces(chessboard, field).nonEmpty

  private def checkingPiecesInDirection(chessboard: Chessboard, start: Field, direction: (Int, Int)): List[Field] = {
    for (nextField <- start.nextFields(direction)
         if (chessboard.getPiece(nextField).isDefined);
         if (chessboard.getPiece(nextField).get.getColor() != getColor());
         if (chessboard.isMoveCorrect(new Move(nextField, start)))
    ) yield nextField
  }

  def allDirections() = {
    for (i <- -1 to 1; j <- -1 to 1; if (i != 0 || j != 0)) yield {
      (i, j)
    }
  }

  def getCheckingPieces(chessboard: Chessboard, field: Field) = {
    (for (direction <- allDirections()) yield checkingPiecesInDirection(chessboard, field, direction)).flatten
  }
}

object KingFactory extends PieceFactory {
  def forColor(color: Color): King = {
    color match {
      case White() => WhiteKing
      case Black() => BlackKing
    }
  }
}

case object WhiteKing extends King {

  def getSymbol(): Char = 'K'

  def getColor(): Color = new White
}

case object BlackKing extends King {
  def getSymbol(): Char = WhiteKing.getSymbol().toLower

  def getColor(): Color = new Black
}