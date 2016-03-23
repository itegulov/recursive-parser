package com.itegulov.parser

import java.text.ParseException

/**
  * @author itegulov
  */
class Parser {

  private val epsilon = new Tree("ε", Some("epsilon"), Nil)

  private def parseE(lexicalAnalyser: LexicalAnalyser): Tree = {
    lexicalAnalyser.curToken match {
      case Some(numberToken: NumberToken) =>
        lexicalAnalyser.nextToken()
        val rightTree: Tree = parseEPrime(lexicalAnalyser)
        Tree("E", Some("nonterminal"), Seq(Tree(numberToken.toString, Some("number"), Nil), rightTree))
      case _ => throw new ParseException("Expected number, but got " + lexicalAnalyser.curToken, 228) // TODO: fix
    }
  }

  private def parseEPrime(lexicalAnalyser: LexicalAnalyser): Tree = {
    lexicalAnalyser.curToken match {
      case Some(numberToken: NumberToken) =>
        val left = parseE(lexicalAnalyser)
        val right = parseEPrimePrime(lexicalAnalyser)
        Tree("E'", Some("nonterminal"), Seq(left, right))
      case _ => Tree("E'", Some("nonterminal"), Seq(epsilon))
    }
  }

  private def parseEPrimePrime(lexicalAnalyser: LexicalAnalyser): Tree = {
    lexicalAnalyser.curToken match {
      case Some(binaryOperation: BinaryOperation) =>
        lexicalAnalyser.nextToken()
        val right = parseEPrime(lexicalAnalyser)
        Tree("E''", Some("nonterminal"), Seq(Tree(binaryOperation.toString, Some("operation"), Nil), right))
      case _ => throw new ParseException("Expected operator, but got " + lexicalAnalyser.curToken, 228) // TODO: fix
    }
  }

  def parse(string: String): Tree = {
    val lexicalAnalyser = new LexicalAnalyser(string)
    lexicalAnalyser.nextToken()
    val answer = parseE(lexicalAnalyser)
    if (lexicalAnalyser.curToken.isEmpty)
      answer
    else 
      throw new ParseException("Expected end of expression, but got " + lexicalAnalyser.curToken, 228)
  }
}

object Main extends App {
  val parser = new Parser
  val tree = parser.parse("2 3 + 7 -")
  println(tree)
}
