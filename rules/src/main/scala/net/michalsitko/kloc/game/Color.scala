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

object Color {
  val whiteStr = "w"
  val blackStr = "b"

  def fromString(input: String): Option[Color] = {
    input match {
      case Color.whiteStr => Some(White())
      case Color.blackStr => Some(Black())
      case _ => None
    }
  }
}

case class White() extends Color (){
  def opposite(): Color = return Black()
  override def toString(): String = {
    Color.whiteStr
  }
}

case class Black() extends Color (){
  def opposite(): Color = return White()
  override def toString(): String = {
    Color.blackStr
  }
}
