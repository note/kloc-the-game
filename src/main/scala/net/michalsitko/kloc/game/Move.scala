package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class Move(fromStr: String, toStr: String){
  val from: Field = Field.fromString(fromStr)
  val to: Field = Field.fromString(toStr)
}
