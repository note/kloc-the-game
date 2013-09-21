package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
/*abstract class AbstractMove(from: Field, to: Field){
}*/

class Move(source: Field, destination: Field){
  val from = source
  val to = destination

  def invertRow(row: Int): Int = {
    7 - row
  }

  def invert(): Move = {
    val invertedFrom = Field(invertRow(from.row), from.column)
    val invertedTo = Field(invertRow(to.row), to.column)
    Move(invertedFrom, invertedTo)
  }

  override def toString: String = from + "-" + to
}

class PromotionMove(from: Field, to: Field, pieceToPromote: Piece) extends Move(from: Field, to: Field){
  val promoteTo = pieceToPromote
}

object Move{
  def apply(fromStr: String, toStr: String) = {
    new Move(Field.fromString(fromStr), Field.fromString(toStr))
  }

  def apply(from: Field, to: Field) = {
    new Move(from, to)
  }

  def apply(fromStr: String, toStr: String, promoteTo: Piece) = {
    new PromotionMove(Field.fromString(fromStr), Field.fromString(toStr), promoteTo)
  }

  def forDestinations(sourceField: Field, destinationFields: List[Field]) = {
    destinationFields.map(Move(sourceField, _))
  }
}