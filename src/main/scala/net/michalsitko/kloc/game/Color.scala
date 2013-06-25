package net.michalsitko.kloc.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/26/13
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
sealed abstract class Color () {
  def opposite(): Color
}

case class White() extends Color (){
  def opposite(): Color = return Black()
}

case class Black() extends Color (){
  def opposite(): Color = return White()
}
