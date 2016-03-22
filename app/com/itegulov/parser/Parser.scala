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
      case _ => throw new ParseException("Expected number, but got " + lexicalAnalyser.curToken, 228); // TODO: fix
    }
  }

  private def parseEPrime(lexicalAnalyser: LexicalAnalyser): Tree = {
    var left: Tree = null
    try {
      left = parseE(lexicalAnalyser)
    } catch {
      case e: ParseException => return new Tree("E'", Some("nonterminal"), Seq(epsilon))
    }
    lexicalAnalyser.curToken match {
      case Some(op: BinaryOperation) =>
        lexicalAnalyser.nextToken()
        try {
          val right: Tree = parseEPrime(lexicalAnalyser)
          new Tree("E'", Some("nonterminal"), Seq(left, new Tree(op.toString, Some("operation"), Nil), right))
        } catch {
          case e: ParseException => new Tree("E'", Some("nonterminal"), Seq(left, new Tree(op.toString, Some("operation"), Nil)))
        }
      case _ => throw new ParseException("Expected operation, but got gavno", 228) // TODO: fix
    }
  }

  def parse(string: String): Tree = {
    val lexicalAnalyser = new LexicalAnalyser(string)
    lexicalAnalyser.nextToken()
    parseE(lexicalAnalyser)
  }
}

object Main extends App {
  val parser = new Parser
  val tree = parser.parse("2 3 + 7 -")
  println(tree)
}
