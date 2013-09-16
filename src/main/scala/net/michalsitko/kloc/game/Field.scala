package net.michalsitko.kloc.game

import java.util

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
case class Field (row: Int, column: Int){
  require(Field.inRange(row))
  require(Field.inRange(row))

  def nextFields(direction: (Int, Int)): List[Field] = {
    var currentRow = row + direction._1
    var currentColumn = column + direction._2
    var result = List[Field]()

    while (Field.inRange(currentRow) && Field.inRange(currentColumn)){
      result = result :+  Field(currentRow, currentColumn)
      currentRow = currentRow + direction._1
      currentColumn = currentColumn + direction._2
    }

    result
  }

  def nextFieldsTo(direction: (Int, Int), endField: Field): List[Field] = {
    var currentRow = row + direction._1
    var currentColumn = column + direction._2
    var result = List[Field]()

    while (Field.inRange(currentRow) && Field.inRange(currentColumn) && Field(currentRow, currentColumn) != endField){
      result = result :+  Field(currentRow, currentColumn)
      currentRow = currentRow + direction._1
      currentColumn = currentColumn + direction._2
    }

    result
  }

  def nextField(direction: (Int, Int)): Option[Field] = {
    val newRow = row + direction._1
    val newColumn = column + direction._2
    if(newRow >= 0 && newRow <= 7 && newColumn >= 0 && newColumn <= 7)
      Some(Field(newRow, newColumn))
    else
      None
  }

  def isKnightAccessible(anotherField: Field): Boolean = {
    val diffs = ((row - anotherField.row).abs, (column - anotherField.column).abs)
    diffs match {
      case (1, 2) => true
      case (2, 1) => true
      case _ => false
    }
  }

  def sameRow(anotherField: Field): Boolean = {
    row == anotherField.row
  }

  def sameColumn(anotherField: Field): Boolean = {
    column == anotherField.column
  }

  def sameDiagonal(anotherField: Field): Boolean = {
    (row - anotherField.row).abs == (column - anotherField.column).abs
  }

  override def toString: String = {
    Field.columnLetterFromInt(column).toString + (row + 1)
  }
}

object Field{
  def AsciiLower = 97;
  def AsciiUpper = 65;

  def inRange(i: Int): Boolean = {
    i >= 0 && i <= 7
  }

  def columnLetterFromInt(columnInt: Int): Char = {
    (columnInt + AsciiLower).toChar
  }

  implicit def fromString(fieldStr: String): Field = {
    require(fieldStr.size == 2)
    require(fieldStr(0).isLetter)
    require(fieldStr(1).isDigit)

    val column = if (fieldStr(0).isLower) fieldStr(0).toInt - AsciiLower else fieldStr(0).toInt - AsciiUpper
    val row = fieldStr(1).getNumericValue - 1
    Field(row, column)
  }
}
