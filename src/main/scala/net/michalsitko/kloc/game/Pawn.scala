package net.michalsitko.game

import net.michalsitko.kloc.game.{Black, White, Color}

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Pawn extends Piece{
  def isOnStartPosition(fromField: Field): Boolean
  def expectedDiff(): Int

  def isMoveCorrect(chessboard: Chessboard, move: Move): Boolean = {
    def isPinned(): Boolean = false

    def isCorrectForward(): Boolean = {
      if (chessboard.getPiece(move.to).isDefined || move.from.column != move.to.column)
        return false
      var correct = false
      correct = correct || (move.to.row - move.from.row) == expectedDiff()
      correct = correct || isOnStartPosition(move.from) && move.to.row - move.from.row == 2 * expectedDiff() && chessboard.nothingBetween(move.from, move.to)
      return correct;
    }

    def isCorrectTake(): Boolean = {
      if (!chessboard.getPiece(move.to).isDefined)
        return false

      // if there is any piece on the destination field we do not have to check if this is enemy
      // we checked if there is no friend on destination field in method areBasicCriteriaSatisfied

      (move.to.column-move.from.column).abs == 1 && move.to.row-move.from.row == expectedDiff()
    }

    !chessboard.isPinned(move) && (isCorrectForward() || isCorrectTake())
  }
}

case object WhitePawn extends Pawn{
  def getSymbol(): Char = 'P'

  def getColor(): Color = new White

  def isOnStartPosition(fromField: Field) = fromField.row == 1

  def expectedDiff = 1
}

case object BlackPawn extends Pawn{
  def getSymbol(): Char = WhitePawn.getSymbol().toLower

  def getColor(): Color = new Black

  def isOnStartPosition(fromField: Field) = fromField.row == 6

  def expectedDiff = -1
}