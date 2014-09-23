package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 9/22/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
class Result

class Winner(val color: Color) extends Result {
  override def toString(): String = {
    color.toString
  }
}

object Draw extends Result {
  override def toString(): String = {
    "draw"
  }
}
