package com.itegulov.parser

import java.text.ParseException

/**
  * @author itegulov
  */
class LexicalAnalyser(expression: String) {

  val string = expression + " "

  private var _curPos: Int = 0
  private var _curToken: Option[Token] = None

  def curPos: Int = _curPos

  def curToken: Option[Token] = _curToken

  private def nextChar(): Char = {
    _curPos = _curPos + 1
    string.charAt(_curPos - 1)
  }

  private def returnChar() = _curPos -= 1

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
    if (_curPos >= string.length) {
      _curToken = None
      return _curToken
    }

    var curChar: Char = nextChar()
    while (_curPos < string.length && Character.isWhitespace(curChar)) {
      curChar = nextChar()
    }

    if (_curPos >= string.length && Character.isWhitespace(curChar)) {
      _curToken = None
      return _curToken
    }

    val next = nextChar()

    curChar match {
      case '+' if Character.isWhitespace(next) =>
        returnChar()
        _curToken = Some(Add())
        _curToken
      case '*' if Character.isWhitespace(next) =>
        returnChar()
        _curToken = Some(Mul())
        _curToken
      case '-' if Character.isDigit(next) =>
        returnChar()
        _curToken = parseNumberToken() match {
          case Right(numberToken) => Some(new NumberToken(-numberToken.number))
          case Left(invalidNumber) => throw new ParseException("Expected number, but got '" + invalidNumber + "'", curPos)
        }
        _curToken
      case '-' if Character.isWhitespace(next) =>
        returnChar()
        _curToken = Some(Sub())
        _curToken
      case _ =>
        returnChar()
        returnChar()
        _curToken = parseNumberToken() match {
          case Right(numberToken) => Some(numberToken)
          case _ => throw new ParseException("Expected operation or number, but got '" + curChar + next + "'", curPos)
        }
        _curToken
    }
  }



}