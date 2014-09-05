package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class KlocGame (customChessboard: Chessboard){
  private var gameState = GameState.default()
  private val chessboard = customChessboard
  var status = GameStatus(chessboard, None)
  private var whoseTurn: Color = White()
  private var proposal: Option[Color] = None
  var propositionRejectedInThisTurn = false

  def this() = {
    this(Chessboard.initialPosition())
  }

  def applyMove(move: Move): GameStatus = {
    if(status.isFinished())
      throw new IncorrectMoveException

    if(!turnOk(move))
      throw new IncorrectMoveException

    if(!chessboard.isMoveCorrect(move, gameState))
      throw new IncorrectMoveException()

    gameState = chessboard.applyMove(move, gameState)
    propositionRejectedInThisTurn = false
    val result = chessboard.getResult(whoseTurn, gameState)
    status = GameStatus(chessboard, result)

    whoseTurn = whoseTurn.opposite()
    status
  }

  def pendingProposition() = proposal

  def proposeDraw(color: Color) = {
    if(status.isFinished())
      throw new IncorrectMoveException()

    if(!propositionRejectedInThisTurn)
      proposal = Some(color)
  }

  def acceptDraw(color: Color) = {
    if(status.isFinished())
      throw new IncorrectMoveException()

    if(proposal.isDefined && proposal.get == color.opposite()){
      status = GameStatus(chessboard, Some(Draw))
      proposal = None
    }

    status
  }

  def rejectDraw(color: Color) = {
    if(status.isFinished())
      throw new IncorrectMoveException()

    proposal = None
    propositionRejectedInThisTurn = true
  }

  def resign(resigningColor: Color) = {
    if(status.isFinished())
      throw new IncorrectMoveException()

    val result = new Winner(resigningColor.opposite())
    status = GameStatus(chessboard, Some(result))
  }

  private def turnOk(move: Move): Boolean = {
    val piece = chessboard.getPiece(move.from)
    piece.isDefined && piece.get.getColor() == whoseTurn
  }
}
