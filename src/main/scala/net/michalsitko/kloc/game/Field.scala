package net.michalsitko.game

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
case class Field (row: Int, column: Int){
  def isKnightAccessible(anotherField: Field): Boolean = {
    val diffs = ((row - anotherField.row).abs, (column - anotherField.column).abs)
    diffs match {
      case (1, 2) => true
      case (2, 1) => true
      case _ => false
    }
  }

  require(row >= 0 && row <= 7)
  require(column >= 0 && column <= 7)

  def sameRow(anotherField: Field): Boolean = {
    row == anotherField.row
  }

  def sameColumn(anotherField: Field): Boolean = {
    column == anotherField.column
  }

  def sameDiagonal(anotherField: Field): Boolean = {
    (row - anotherField.row).abs == (column - anotherField.column).abs
  }
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
