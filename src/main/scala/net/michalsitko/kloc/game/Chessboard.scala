package net.michalsitko.kloc.game

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
  protected val allFields = for (letter <- 'a' to 'h'; number <- 1 to 8) yield {letter.toString() + number}

  def getFieldOfKing(color: Color): Option[Field] = {
    val expectedKing = KingFactory.forColor(color)
    allFields.map(Field.fromString((_))).find(getPiece(_).getOrElse(null) == expectedKing)
  }

  def withAppliedMove[T] (move: Move) (fn: (Chessboard) => T): T = {
    val activePiece = getPiece(move.from)
    val old = getPiece(move.to)
    applyMove(move)
    val result = fn(this)
    setPiece(move.to, old)
    setPiece(move.from, activePiece)
    result
  }

  def isPinned(move: Move, gameState: GameState = GameState.default()): Boolean = {
    val activeColor = getPiece(move.from).get.getColor()
    val fieldOfKing = getFieldOfKing(activeColor)

    // TODO: refactor
    if(!fieldOfKing.isDefined || getPiece(move.from).get == WhiteKing || getPiece(move.from).get == BlackKing)
      return false

    val king = KingFactory.forColor(activeColor)
    withAppliedMove(move){
      king.isChecked(_, fieldOfKing.get, gameState)
    }
  }

  def isStalemate(color: Color): Boolean = {
    !getFieldsOfPieces(color).exists((field: Field) => getPiece(field).get.isAnyMovePossible(this, field))
  }

  def getFieldsBetween(field: Field, anotherField: Field): List[Field] = {
    if (field.row == anotherField.row)
      return field.nextFieldsTo(getDirectionForHorizontal(field, anotherField), anotherField)

    if (field.column == anotherField.column)
      return field.nextFieldsTo(getDirectionForVertical(field, anotherField), anotherField)

    if (field.sameDiagonal(anotherField))
      return field.nextFieldsTo(getDirectionForDiagonal(field, anotherField), anotherField)

    List()
  }

  def getDirectionForHorizontal(field: Field, anotherField: Field): (Int, Int) = {
    require(field.sameRow(anotherField))

    if (field.column < anotherField.column)
      (0, 1)
    else
      (0, -1)
  }

  def getDirectionForVertical(field: Field, anotherField: Field): (Int, Int) = {
    require(field.sameColumn(anotherField))

    if (field.row < anotherField.row)
      (1, 0)
    else
      (-1, 0)
  }

  def getDirectionForDiagonal(field: Field, anotherField: Field): (Int, Int) = {
    require(field.sameDiagonal(anotherField))

    val rowIncrease = if (field.row < anotherField.row) 1 else -1
    val columnIncrease = if (field.column < anotherField.column) 1 else -1
    (rowIncrease, columnIncrease)
  }

  def somethingBetween(field: Field, anotherField: Field): Boolean = {
      getFieldsBetween(field, anotherField).exists(getPiece(_).isDefined)
  }

  def setPiece(field: Field, piece: Option[Piece]) {
    fields(field.row)(field.column) = piece
  }

  def enpassantForGameState(move: Move){

  }

  /*
     This method assumes that move is legal
    */
  private def nextGameState(move: Move, gameState: GameState): GameState = {
    val activePiece = getPiece(move.from)

    require(activePiece.isDefined, "applyMove assumes that move is legal" + move.toString)
    val activeColor = activePiece.get.getColor()

    val enpassant = activePiece match {
      // we can use such simple condition because we assume than move is correct
      case Some(piece: Pawn) if (move.to.row - move.from.row).abs == 2 => Some(Field.columnLetterFromInt(move.from.column))
      case _ => None
    }

    val gameStateForOpposite = gameState.forColor(activeColor.opposite()).copy(enpassantColumn = enpassant)
    val gameStateTmp = gameState.next(activeColor.opposite(), gameStateForOpposite)

    activePiece match {
      case Some(piece: King) => gameStateTmp.next(activeColor, gameState.forColor(activeColor).copy(shortCastlingEnabled = false, longCastlingEnabled = false))
      case Some(piece: WhiteRook.type) if move.from.column == 0 && move.from.row == 0 => gameStateTmp.next(activeColor, gameState.forColor(activeColor).copy(longCastlingEnabled = false))
      case Some(piece: WhiteRook.type) if move.from.column == 7 && move.from.row == 0 => gameStateTmp.next(activeColor, gameState.forColor(activeColor).copy(shortCastlingEnabled = false))
      case Some(piece: BlackRook.type) if move.from.column == 0 && move.from.row == 7 => gameStateTmp.next(activeColor, gameState.forColor(activeColor).copy(longCastlingEnabled = false))
      case Some(piece: BlackRook.type) if move.from.column == 7 && move.from.row == 7 => gameStateTmp.next(activeColor, gameState.forColor(activeColor).copy(shortCastlingEnabled = false))
      case _ => gameStateTmp.copy()
    }
  }

  /*
    This method assumes that move is legal
     */
  def applyMove(move: Move, gameState: GameState = GameState.default()): GameState = {
    val newGameState = nextGameState(move, gameState)
    setPiece(move.to, getPiece(move.from))
    setPiece(move.from, None)

    // handle castling
    val castlingOption = Castling.getAppropriateCastling(move.from, move.to, gameState)
    castlingOption.map((castling: Castling) => setPiece(castling.rookDestination, getPiece(castling.rookSource)))
    castlingOption.map((castling: Castling) => setPiece(castling.rookSource, None))

    newGameState
  }

  def isMoveCorrect(move: Move, gameState: GameState = GameState.default()): Boolean = {
    if (getPiece((move.from)).isDefined)
      return getPiece(move.from).get.isMoveCorrect(this, move, gameState)
    return false
  }

  def isMoveAttacking(move: Move, gameState: GameState): Boolean = {
    if (getPiece((move.from)).isDefined)
      return getPiece(move.from).get.isMoveAttacking(this, move, gameState)
    return false
  }

  def getPiece(field: Field): Option[Piece] = {
    getPiece(field.row, field.column)
  }

  def getPiece(row: Int, column: Int): Option[Piece] = {
    fields(row)(column)
  }

  def getFieldsOfPieces(color: Color) = {
    allFields.filter(getPiece(_).map(_.getColor() == color).getOrElse(false)).map(Field.fromString(_))
  }

  def getKing(color: Color): King = {
    //TODO: ugly
    val field = getFieldOfKing(color).get
    getPiece(field) match {
      case Some(king: King) => king
      case _ => throw new ClassCastException
    }
  }

  def getResult(lastMoveColor: Color, gameState: GameState): Option[Result] = {
    if(getKing(lastMoveColor.opposite()).isCheckmated(this, getFieldOfKing(lastMoveColor.opposite()).get, gameState))
      return Some(new Winner(lastMoveColor))

    if(isStalemate(lastMoveColor.opposite()))
      return Some(Draw)

    return None
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

  def initialPosition(): Chessboard = {
    loadFromFile("/initial.position")
  }

  private def createSymbolToPieceMap(): Map[Char, Piece] = {
    (for (piece <- Piece.values) yield (piece.getSymbol -> piece)).toMap
  }
}
