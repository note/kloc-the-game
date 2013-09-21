package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Pawn extends Piece {
  def expectedDiff(): Int

  def isLastLineMove(move: Move): Boolean

  def startPosition(): Int

  def isOnStartPosition(fromField: Field) = fromField.row == startPosition()

  def isRowEnpassantDestination(row: Int): Boolean = row == (PawnFactory.forColor(getColor().opposite()).startPosition - expectedDiff)

  def isCorrectPromotion(move: Move): Boolean = {
    move match {
      case promotionMove: PromotionMove => promotionMove.promoteTo match {
        case _: King | _: Pawn => false
        case _ => true
      }
      case _ => false
    }
  }

  def checkMoveCorrect(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    def isCorrectForward(): Boolean = {
      if (chessboard.getPiece(move.to).isDefined || move.from.column != move.to.column)
        return false
      var correct = false
      correct = correct || (move.to.row - move.from.row) == expectedDiff()
      correct = correct || isOnStartPosition(move.from) && move.to.row - move.from.row == 2 * expectedDiff() && !chessboard.somethingBetween(move.from, move.to)
      return correct;
    }

    def isTake(): Boolean = {
      (move.to.column - move.from.column).abs == 1 && move.to.row - move.from.row == expectedDiff()
    }

    def isCorrectTake(): Boolean = {
      if (!chessboard.getPiece(move.to).isDefined)
        return false

      isTake()
    }

    def isCorrectEnpassant(): Boolean = {
      val enpassantColumn = gameState.forColor(getColor()).enpassantColumn
      if(enpassantColumn.isDefined)
        isTake() && (Field.columnLetterFromInt(move.to.column) == gameState.forColor(getColor()).enpassantColumn.get) && isRowEnpassantDestination(move.to.row)
      else
        false
    }

    val pawnCorrect = isCorrectForward() || isCorrectTake() || isCorrectEnpassant()
    if (isLastLineMove(move))
      pawnCorrect && isCorrectPromotion(move)
    else
      pawnCorrect
  }

  def getDirections(): List[(Int, Int)] = {
    List((expectedDiff(), -1), (expectedDiff(), 0), (expectedDiff(), 1), (2*expectedDiff(), 0))
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

  def expectedDiff = 1

  def isLastLineMove(move: Move): Boolean = {
    move.to.row == 7
  }

  def startPosition(): Int = 1
}

case object BlackPawn extends Pawn {
  def getSymbol(): Char = WhitePawn.getSymbol().toLower

  def getColor(): Color = new Black

  def expectedDiff = -1

  def isLastLineMove(move: Move): Boolean = {
    move.to.row == 0
  }

  def startPosition(): Int = 6
}