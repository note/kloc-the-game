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
  def isPinned(move: Move): Boolean = false

  def nothingBetween(field: Field, field1: Field): Boolean = true

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

  private val fields = Array.ofDim[Option[Piece]](8, 8)
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
