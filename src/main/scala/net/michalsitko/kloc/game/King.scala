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

  /*private def checkingPiecesInDirection(chessboard: Chessboard, start: Field, direction: (Int, Int)): util.Collection[Field] = {
    val result = new util.ArrayList[Field]()
    for (nextField <- start.nextFields(direction)) {
      if (chessboard.getPiece(nextField).isDefined)
        if (chessboard.getPiece(nextField).get.getColor() == getColor())
          return result
        else {
          if (chessboard.isMoveCorrect(new Move(nextField, start))) {
            result.add(nextField)
            return result
          }else
            return result
        }

      return result
    }

    def allDirections() = {
      for (i <- -1 to 1; j <- -1 to 1; if (i!=0 || j!=0)) yield {(i,j)}
    }*/

    /*def getCheckingPieces(chessboard: Chessboard, field: Field): List[Field] = {
      val checkingPieces = new util.ArrayList[Field]()
      for (direction <- allDirections()) {
        checkingPieces.addAll(checkingPiecesInDirection(chessboard, field, direction))
      }
    }*/
  }

  object KingFactory extends PieceFactory {
    def forColor(color: Color) : King = {
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