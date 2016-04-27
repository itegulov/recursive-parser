package com.itegulov.parser

import java.text.ParseException

/**
  * @author itegulov
  */
class LexicalAnalyser(string: String) {
  private var curPos: Int = 0
  private var curToken: Option[Token] = None

  def currentPosition: Int = curPos

  def currentToken: Option[Token] = curToken

  private def nextChar(): Char = {
    curPos = curPos + 1
    string.charAt(curPos - 1)
  }

  private def returnChar() = curPos -= 1

  private def parseSignedNumberToken(): NumberToken = {
    val sb = new StringBuilder
    var ch = nextChar()
    if (ch == '-') {
      sb += '-'
      ch = nextChar()
    }
    while (Character.isDigit(ch)) {
      sb.append(ch)
      ch = nextChar()
    }
    returnChar()
    new NumberToken(sb.toString.toInt)
  }

  def nextToken(): Option[Token] = {
    if (curPos >= string.length) {
      curToken = None
      return curToken
    }

    val curChar: Char = nextChar()
    if (Character.isWhitespace(curChar)) {
      nextToken()
    } else {
      curChar match {
        case _ if "^-?[0-9]+".r.findFirstIn(string.substring(curPos - 1)).nonEmpty =>
          returnChar()
          curToken = Some(parseSignedNumberToken())
          curToken
        case '+' =>
          curToken = Some(Add())
          curToken
        case '*' =>
          curToken = Some(Mul())
          curToken
        case '-' =>
          curToken = Some(Sub())
          curToken
        case _ =>
          throw new ParseException("Expected operation or number, but got '" + curChar + "'", currentPosition)
      }
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