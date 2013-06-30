package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Pawn extends Piece {
  def isOnStartPosition(fromField: Field): Boolean

  def expectedDiff(): Int

  def isLastLineMove(move: Move): Boolean

  def isCorrectPromotion(move: Move): Boolean = {
    move match {
      case promotionMove: PromotionMove => promotionMove.promoteTo match {
        case _: King | _: Pawn => false
        case _ => true
      }
      case _ => false
    }
  }

  def checkMoveCorrect(chessboard: Chessboard, move: Move): Boolean = {
    def isCorrectForward(): Boolean = {
      if (chessboard.getPiece(move.to).isDefined || move.from.column != move.to.column)
        return false
      var correct = false
      correct = correct || (move.to.row - move.from.row) == expectedDiff()
      correct = correct || isOnStartPosition(move.from) && move.to.row - move.from.row == 2 * expectedDiff() && !chessboard.somethingBetweenVertically(move.from, move.to)
      return correct;
    }

    def isCorrectTake(): Boolean = {
      if (!chessboard.getPiece(move.to).isDefined)
        return false

      // if there is any piece on the destination field we do not have to check if this is enemy
      // we checked if there is no friend on destination field in method areBasicCriteriaSatisfied

      (move.to.column - move.from.column).abs == 1 && move.to.row - move.from.row == expectedDiff()
    }

    val pawnCorrect = isCorrectForward() || isCorrectTake()
    if (isLastLineMove(move))
      pawnCorrect && isCorrectPromotion(move)
    else
      pawnCorrect
  }
}

object PawnFactory extends PieceFactory {
  def forColor(color: Color): Pawn = {
    color match {
      case White() => WhitePawn
      case Black() => BlackPawn
    }
  }
}

case object WhitePawn extends Pawn {
  def getSymbol(): Char = 'P'

  def getColor(): Color = new White

  def isOnStartPosition(fromField: Field) = fromField.row == 1

  def expectedDiff = 1

  def isLastLineMove(move: Move): Boolean = {
    move.to.row == 7
  }
}

case object BlackPawn extends Pawn {
  def getSymbol(): Char = WhitePawn.getSymbol().toLower

  def getColor(): Color = new Black

  def isOnStartPosition(fromField: Field) = fromField.row == 6

  def expectedDiff = -1

  def isLastLineMove(move: Move): Boolean = {
    move.to.row == 0
  }
}