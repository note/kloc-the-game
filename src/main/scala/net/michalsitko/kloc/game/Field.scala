package net.michalsitko.game

import java.util

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
case class Field (row: Int, column: Int){

  def inRange(i: Int): Boolean = {
    i >= 0 && i <= 7
  }

  def nextFields(direction: (Int, Int)) = {
    var currentRow = row + direction._1
    var currentColumn = column + direction._2
    val result = new util.ArrayList[Field]()

    while (inRange(currentRow) && inRange(currentColumn)){
      result.add(Field(currentRow, currentColumn))
      currentRow = row + direction._1
      currentColumn = column + direction._2
    }

    result
  }


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

  implicit def fromString(fieldStr: String): Field = {
    require(fieldStr.size == 2)
    require(fieldStr(0).isLetter)
    require(fieldStr(1).isDigit)

    val column = if (fieldStr(0).isLower) fieldStr(0).toInt - AsciiLower else fieldStr(0).toInt - AsciiUpper
    val row = fieldStr(1).getNumericValue - 1
    Field(row, column)
  }
}
