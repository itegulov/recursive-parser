package com.itegulov.parser

/**
  * @author itegulov
  */
sealed trait Token

case class NumberToken(number: Int) extends Token {
  override def toString = number.toString
}

class BinaryOperation() extends Token

case class Add() extends BinaryOperation {
  override def toString = "+"
}

case class Sub() extends BinaryOperation {
  override def toString = "-"
}

case class Mul() extends BinaryOperation {
  override def toString = "*"
}
