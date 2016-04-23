package com.itegulov.parser

import java.text.ParseException

/**
  * @author itegulov
  */
class LexicalAnalyser(s: String) {
  private val string = s + " "
  private var curPos: Int = 0
  private var curToken: Option[Token] = None

  def currentPosition: Int = curPos

  def currentToken: Option[Token] = curToken

  private def nextChar(): Char = {
    curPos = curPos + 1
    string.charAt(curPos - 1)
  }

  private def returnChar() = curPos -= 1

  private def parseNumberToken(): Either[String, NumberToken] = {
    val sb = new StringBuilder
    var ch = nextChar()
    while (Character.isDigit(ch)) {
      sb.append(ch)
      ch = nextChar()
    }
    returnChar()
    try {
      Right(new NumberToken(sb.toString.toInt))
    } catch {
      case e: NumberFormatException => Left(sb.toString)
    }
  }

  def nextToken(): Option[Token] = {
    if (curPos >= string.length) {
      curToken = None
      return curToken
    }

    var curChar: Char = nextChar()
    while (curPos < string.length && Character.isWhitespace(curChar)) {
      curChar = nextChar()
    }

    if (curPos >= string.length && Character.isWhitespace(curChar)) {
      curToken = None
      return curToken
    }

    val next = if (curPos >= string.length) '\n' else nextChar()

    curChar match {
      case '+' if Character.isWhitespace(next) =>
        returnChar()
        curToken = Some(Add())
        curToken
      case '*' if Character.isWhitespace(next) =>
        returnChar()
        curToken = Some(Mul())
        curToken
      case '-' if Character.isDigit(next) =>
        returnChar()
        curToken = parseNumberToken() match {
          case Right(numberToken) => Some(new NumberToken(-numberToken.number))
          case Left(invalidNumber) => throw new ParseException("Expected number, but got '" + invalidNumber + "'", currentPosition)
        }
        curToken
      case '-' if Character.isWhitespace(next) =>
        returnChar()
        curToken = Some(Sub())
        curToken
      case _ =>
        returnChar()
        returnChar()
        curToken = parseNumberToken() match {
          case Right(numberToken) => Some(numberToken)
          case _ => throw new ParseException("Expected operation or number, but got '" + curChar + next + "'", currentPosition)
        }
        curToken
    }
  }

  def hasNext: Boolean = {
    val pos = curPos
    val token = curToken
    nextToken() match {
      case Some(_) =>
        curPos = pos
        curToken = token
        true
      case _ => false
    }
  }
}