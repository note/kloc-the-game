package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
case class Field (row: Int, column: Int){
  require(row >= 0 && row <= 7)
  require(column >= 0 && column <= 7)
}

object Field{
  def AsciiLower = 97;
  def AsciiUpper = 65;

  def fromString(fieldStr: String): Field = {
    require(fieldStr.size == 2)
    require(fieldStr(0).isLetter)
    require(fieldStr(1).isDigit)

    val column = if (fieldStr(0).isLower) fieldStr(0).toInt - AsciiLower else fieldStr(0).toInt - AsciiUpper
    val row = fieldStr(1).getNumericValue - 1
    Field(row, column)
  }
}
