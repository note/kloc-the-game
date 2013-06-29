package net.michalsitko.kloc.game

import scala.collection.immutable.IndexedSeq

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
      isKingMove(chessboard, move.from, move.to) && !wouldBeChecked(chessboard, move)
    else
      false
  }

  def isChecked(chessboard: Chessboard, field: Field): Boolean = getCheckingPieces(chessboard, field).nonEmpty

  def isCheckmated(chessboard: Chessboard, field: Field): Boolean = {
    val attackers: List[Field] = getCheckingPieces(chessboard, field)
    if(attackers.isEmpty)
      return false

    val canEscape = canMove(chessboard, field)
    !canEscape && (attackers.size > 1 || (!canBeShielded(chessboard, field, attackers.head) && !canBeTaken(chessboard, attackers.head)))
  }

  def canBeTaken(chessboard: Chessboard, attacker: Field): Boolean = {
    chessboard.getFieldsOfPieces(getColor()).exists((from: Field) => chessboard.isMoveCorrect(Move(from, attacker)))
  }

  def wouldBeChecked(chessboard: Chessboard, move: Move): Boolean = {
    /*val old = chessboard.getPiece(move.to)
    chessboard.applyMove(move)
    val result = isChecked(chessboard, move.to)
    chessboard.setPiece(move.to, old)
    chessboard.setPiece(move.from, Some(this))
    result*/
    chessboard.withAppliedMove(move){
      isChecked(_, move.to)
    }
  }

  def canBeShielded(chessboard: Chessboard, kingField: Field, attacker: Field):Boolean = {
    val friendlyPiecesFields: IndexedSeq[Field] = chessboard.getFieldsOfPieces(getColor())
    for(friendlyPieceField <- friendlyPiecesFields){
      val moves = Move.forDestinations(friendlyPieceField, chessboard.getFieldsBetween(kingField, attacker))
      if(moves.exists(chessboard.isMoveCorrect(_)))
        return true
    }
    false
  }

  private def checkingPiecesInDirection(chessboard: Chessboard, start: Field, direction: (Int, Int)): List[Field] = {
    for (nextField <- start.nextFields(direction)
         if (chessboard.getPiece(nextField).isDefined);
         if (chessboard.getPiece(nextField).get.getColor() != getColor());
          //isMoveCorrect is too strong. even when piece is pinned can be attacker in the sense of check
          //TODO: write a test for this
         if (chessboard.isMoveCorrect(new Move(nextField, start)))
    ) yield nextField
  }

  private def canMove(chessboard: Chessboard, start: Field): Boolean = {
    allDirections().exists(
      (direction) => start.nextField(direction).map((destinationField: Field) => chessboard.isMoveCorrect(Move(start, destinationField))).getOrElse(false)
    )
  }

  def allDirections() = {
    for (i <- -1 to 1; j <- -1 to 1; if (i != 0 || j != 0)) yield {
      (i, j)
    }
  }

  def knightMoves() = {
    List((1, 2), (2, 1), (-1, 2), (-2, 1), (1, -2), (2, -1), (-1, -2), (-2, -1))
  }

  def getCheckingPieces(chessboard: Chessboard, field: Field) = {
    val attackingKnights =
      for ((i, j) <- knightMoves()
           if Field.inRange(field.row + i) && Field.inRange(field.column + i)
           if chessboard.getPiece(Field(field.row + i, field.column + j)).isDefined
           if chessboard.getPiece(Field(field.row + i, field.column + j)).get.getColor() != getColor()
           if chessboard.isMoveCorrect(Move(Field(field.row + i, field.column + j), field))
      ) yield Field(field.row + i, field.column + j)
    val otherAttackingPieces = (for (direction <- allDirections()) yield checkingPiecesInDirection(chessboard, field, direction)).flatten
    attackingKnights ++ otherAttackingPieces
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