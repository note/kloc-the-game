package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */

abstract trait Piece extends Piece.Value with Colored{
  def checkMoveCorrect(chesssboard: Chessboard, move: Move, gameState: GameState): Boolean

  def getDirections(): List[(Int, Int)]

  def isMoveCorrect(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    areBasicCriteriaSatisfied(chessboard, move, gameState) && checkMoveCorrect(chessboard, move, gameState)
  }

  def isAnyMovePossible(chessboard: Chessboard, field: Field): Boolean = {
    for ((i, j) <- getDirections()
         if Field.inRange(field.row + i) && Field.inRange(field.column + j)
         if chessboard.isMoveCorrect(Move(field, Field(field.row + i, field.column + j)))){
      return true
    }
    false
  }

  def isMoveAttacking(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    isDestinationFieldAccessible(chessboard, move) && checkMoveCorrect(chessboard, move, gameState)
  }

  def getSymbol(): Char

  protected def areBasicCriteriaSatisfied(chessboard: Chessboard, move: Move, gameState: GameState): Boolean = {
    !chessboard.isPinned(move, gameState) && isDestinationFieldAccessible(chessboard, move)
  }

  private def isDestinationFieldAccessible(chessboard: Chessboard, move: Move): Boolean = {
    if (chessboard.getPiece(move.to).isDefined){
      val activePiece = chessboard.getPiece(move.from).get
      val passivePiece = chessboard.getPiece(move.to).get
      activePiece.getColor() != passivePiece.getColor()
    }else
      true
  }
}

abstract trait PieceFactory {
  def forColor(color: Color): Piece
}

object Piece extends Enum[Piece]{
  // Not nice, I have to use all of enum instances to be visible (they're not loaded until I use them)
  BlackRook
  WhiteRook
  BlackKnight
  WhiteKnight
  BlackBishop
  WhiteBishop
  BlackQueen
  WhiteQueen
  BlackKing
  WhiteKing
  BlackPawn
  WhitePawn
}