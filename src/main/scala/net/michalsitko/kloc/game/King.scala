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
  def isKingMove(chessboard: Chessboard, from: Field, to: Field, gameState: GameState): Boolean = {
    val columnDiff = (from.column - to.column).abs
    val rowDiff = (from.row - to.row).abs
    val isRegularMove = (rowDiff > 0 || columnDiff > 0) && rowDiff < 2 && columnDiff < 2
    isRegularMove || isCastlingMove(chessboard, from, to, gameState)
  }

  def isCorrectCastling(chessboard: Chessboard, from: Field, to: Field, castling: Castling, gameState: GameState): Boolean = {
    !chessboard.somethingBetween(from, castling.rookSource) && !chessboard.getFieldsBetween(from, to).exists((f: Field) => wouldBeChecked(chessboard, new Move(from, f), gameState))
  }

  def isCastlingMove(chessboard: Chessboard, from: Field, to: Field, gameState: GameState): Boolean = {
    val castling = Castling.getAppropriateCastling(from, to, gameState)

    val enabled = castling match {
      case Some(c: ShortCastling) => gameState.forColor(chessboard.getPiece(from).get.getColor()).shortCastlingEnabled
      case Some(c: LongCastling) => gameState.forColor(chessboard.getPiece(from).get.getColor()).longCastlingEnabled
      case _ => false
    }

    if(enabled)
      !isChecked(chessboard, from, gameState) && isCorrectCastling(chessboard, from, to, castling.get, gameState)
    else
      false

    // TODO: checking whose turn is now this way is a at least ugly
  }

  def checkMoveCorrect(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    // TODO: move wouldBeChecked somewhere else, it is more general
    isKingMove(chessboard, move.from, move.to, gameState) && !wouldBeChecked(chessboard, move, gameState)
  }

  def isChecked(chessboard: Chessboard, field: Field, gameState: GameState = GameState.default()): Boolean = getCheckingPieces(chessboard, field, gameState).nonEmpty

  def isCheckmated(chessboard: Chessboard, field: Field, gameState: GameState = GameState.default()): Boolean = {
    val attackers: List[Field] = getCheckingPieces(chessboard, field, gameState)
    if (attackers.isEmpty)
      return false

    val canEscape = canMove(chessboard, field)
    !canEscape && (attackers.size > 1 || (!canBeShielded(chessboard, field, attackers.head) && !canBeTaken(chessboard, attackers.head)))
  }

  def canBeTaken(chessboard: Chessboard, attacker: Field): Boolean = {
    chessboard.getFieldsOfPieces(getColor()).exists((from: Field) => chessboard.isMoveCorrect(Move(from, attacker)))
  }

  def wouldBeChecked(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    chessboard.withAppliedMove(move) {
      isChecked(_, move.to, gameState)
    }
  }

  def canBeShielded(chessboard: Chessboard, kingField: Field, attacker: Field): Boolean = {
    val friendlyPiecesFields: IndexedSeq[Field] = chessboard.getFieldsOfPieces(getColor())
    for (friendlyPieceField <- friendlyPiecesFields) {
      val moves = Move.forDestinations(friendlyPieceField, chessboard.getFieldsBetween(kingField, attacker))
      if (moves.exists(chessboard.isMoveCorrect(_)))
        return true
    }
    false
  }

  private def checkingPiecesInDirection(chessboard: Chessboard, start: Field, direction: (Int, Int), gameState: GameState): List[Field] = {
    val noCastlingsGameState = gameState.noCastlings()

    for (nextField <- start.nextFields(direction)
         if (chessboard.getPiece(nextField).isDefined);
         if (chessboard.getPiece(nextField).get.getColor() != getColor());
         if (chessboard.isMoveAttacking(new Move(nextField, start), noCastlingsGameState))
    ) yield nextField
  }

  private def canMove(chessboard: Chessboard, start: Field): Boolean = {
    getDirections().exists(
      (direction) => start.nextField(direction).map((destinationField: Field) => chessboard.isMoveCorrect(Move(start, destinationField))).getOrElse(false)
    )
  }

  def getDirections(): List[(Int, Int)] = {
    King.getDirections()
  }

  def getCheckingPieces(chessboard: Chessboard, field: Field, gameState: GameState) = {
    val attackingKnights =
      for ((i, j) <- Knight.getDirections()
           if Field.inRange(field.row + i) && Field.inRange(field.column + j)
           if chessboard.getPiece(Field(field.row + i, field.column + j)).isDefined
           if chessboard.getPiece(Field(field.row + i, field.column + j)).get.getColor() != getColor()
           if chessboard.isMoveAttacking(Move(Field(field.row + i, field.column + j), field), gameState)
      ) yield Field(field.row + i, field.column + j)
    val otherAttackingPieces = (for (direction <- getDirections()) yield checkingPiecesInDirection(chessboard, field, direction, gameState)).flatten
    attackingKnights ++ otherAttackingPieces
  }
}

object King {
  def getDirections(): List[(Int, Int)] = {
    (for (i <- -1 to 1; j <- -1 to 1; if (i != 0 || j != 0)) yield {
      (i, j)
    }).toList
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