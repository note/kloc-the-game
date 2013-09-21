package net.michalsitko.kloc.game

import scala.collection.mutable.ListBuffer

/**
 *
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 9/21/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
class MoveInverter {
  def Move(from: String, to: String): Move = {
    Move(from, to).invert()
  }
}
