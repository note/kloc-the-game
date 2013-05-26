package net.michalsitko.game

import java.nio.file.{Files, Paths}
import io.Source

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
class Chessboard {
  def applyMove(move: Move) {

  }

  def isMoveCorrect(move: Move): Boolean = {
    false
  }

  /*protected def getPiece(field: String) = {

  }*/

  protected def getPiece(row: Int, column: Int) = {
    require(row >= 0 && row <= 7)
    require(column >= 0 && column <= 7)

    fields(row)(column)
  }

  private val fields = Array.ofDim[Option[Piece]](8, 8)
}

object Chessboard{
  val symbolToPieceMap = createSymbolToPieceMap()

  def loadFromFile(fileName: String): Chessboard = {
    val chessboard = new Chessboard
    val source = Source.fromURL(getClass.getResource(fileName))
    val content = source.mkString.replaceAll("""\s+""", "")

    val iterator = content.iterator
    for (row <- chessboard.fields.reverse; i <- 0 to (row.size - 1)){
      row(i) = symbolToPieceMap.get(iterator.next())
    }
    chessboard
  }

  private def createSymbolToPieceMap(): Map[Char, Piece] = {
    (for (piece <- Piece.values) yield (piece.getSymbol -> piece)).toMap
  }
}
