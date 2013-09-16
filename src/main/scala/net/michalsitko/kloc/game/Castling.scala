package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 9/17/13
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */

case class Castling(kingSource: Field, kingDestination: Field, rookSource: Field, rookDestination: Field) {

}

object Castling {
  val castlingTypes = List(
    Castling("e1", "g1", "h1", "f1"),
    Castling("e1", "c1", "a1", "d1"),
    Castling("e8", "g8", "h8", "f8"),
    Castling("e8", "c8", "a8", "d8"))

  def getAppropriateCastling(from: Field, to: Field, gameState: GameState): Option[Castling] = {
    for (castlingType <- castlingTypes)
      if (from == castlingType.kingSource && to == castlingType.kingDestination)
        return Some(castlingType)
    return None
  }
}
