package net.michalsitko.kloc.game

import net.michalsitko.game.Chessboard

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
trait PositionGenerator {
  private val InitialPositionFileName = "/initial.position"
  private val TypicalPositionFileName = "/typical.position"
  private val PromotionPositionFileName = "/promotion.position"
  protected val allFields = for (letter <- 'a' to 'h'; number <- 1 to 8) yield {letter.toString() + number}

  def getInitialPosition(): Chessboard = {
    Chessboard.loadFromFile(InitialPositionFileName)
  }

  def getTypicalPosition(): Chessboard = {
    Chessboard.loadFromFile(TypicalPositionFileName)
  }

  def getPromotionPosition(): Chessboard = {
    Chessboard.loadFromFile(PromotionPositionFileName)
  }
}
