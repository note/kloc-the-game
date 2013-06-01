package net.michalsitko.game

import java.nio.file.{Files, Paths}
import io.Source
import java.io.FileNotFoundException

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
class Chessboard {
  private val fields = Array.fill[Option[Piece]](8, 8)(None)

  def isPinned(move: Move): Boolean = false

  def somethingBetweenHorizontally(field: Field, anotherField: Field): Boolean = {
    require(field.sameRow(anotherField))

    val row = field.row
    val (smaller, bigger) = if (field.column < anotherField.column) (field, anotherField) else (anotherField, field)
    for (i <- (smaller.column + 1) to (bigger.column - 1))
      if (getPiece(row, i).isDefined)
        return true

    return false
  }

  def somethingBetweenVertically(field: Field, anotherField: Field): Boolean = {
    require(field.sameColumn(anotherField))

    val column = field.column
    val (smaller, bigger) = if (field.row < anotherField.row) (field, anotherField) else (anotherField, field)
    for (i <- (smaller.row + 1) to (bigger.row - 1))
      if (getPiece(i, column).isDefined)
        return true

    false
  }

  def somethingBetweenDiagonally(field: Field, anotherField: Field): Boolean = {
    require(field.sameDiagonal(anotherField))

    val columnIncrease = if (field.column < anotherField.column) 1 else -1
    val rowIncrease = if (field.row < anotherField.row) 1 else -1
    var column = field.column + columnIncrease
    var row = field.row + rowIncrease

    var iterations = (field.column - anotherField.column).abs - 1
    while(iterations > 0){
      if (getPiece(row, column).isDefined)
        return true

      column += columnIncrease
      row += rowIncrease
      iterations -= 1
    }

    false
  }

  def somethingBetween(field: Field, anotherField: Field): Boolean = {
    if (field.row == anotherField.row)
      return somethingBetweenHorizontally(field, anotherField)

    if (field.column == anotherField.column)
      return somethingBetweenVertically(field, anotherField)

    if (field.sameDiagonal(anotherField))
      return somethingBetweenDiagonally(field, anotherField)

    return true
  }

  def setPiece(field: Field, piece: Option[Piece]) {
    fields(field.row)(field.column) = piece
  }

  def applyMove(move: Move) {
     // this method assumes that move is legal
    setPiece(move.to, getPiece(move.from))
    setPiece(move.from, None)
  }

  def isMoveCorrect(move: Move): Boolean = {
    if (getPiece((move.from)).isDefined)
      return getPiece(move.from).get.isMoveCorrect(this, move)
    return false
  }

  def getPiece(field: Field): Option[Piece] = {
    getPiece(field.row, field.column)
  }

  def getPiece(row: Int, column: Int): Option[Piece] = {
    fields(row)(column)
  }
}

object Chessboard{
  val symbolToPieceMap = createSymbolToPieceMap()

  def loadFromFile(fileName: String): Chessboard = {
    val chessboard = new Chessboard
    try{
      val source = Source.fromURL(getClass.getResource(fileName))
      val content = source.mkString.replaceAll("""\s+""", "")

      val iterator = content.iterator
      for (row <- chessboard.fields.reverse; i <- 0 to (row.size - 1)){
        row(i) = symbolToPieceMap.get(iterator.next())
      }
      chessboard
    } catch {
      case ex: NullPointerException => throw new FileNotFoundException()
    }
  }

  private def createSymbolToPieceMap(): Map[Char, Piece] = {
    (for (piece <- Piece.values) yield (piece.getSymbol -> piece)).toMap
  }
}
