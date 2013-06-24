package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
case class Move(from: Field, to: Field){
}

object Move{
  def apply(fromStr: String, toStr: String) = {
    new Move(Field.fromString(fromStr), Field.fromString(toStr))
  }
}