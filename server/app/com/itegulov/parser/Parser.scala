package com.itegulov.parser

/**
  * @author itegulov
  */
class Parser {

  private val epsilon = new Tree("ε", "epsilon", Nil)

  private def parseE(lexicalAnalyser: LexicalAnalyser): Either[String, Tree] = {
    lexicalAnalyser.currentToken match {
      case Some(numberToken: NumberToken) =>
        lexicalAnalyser.nextToken()
        parseEPrime(lexicalAnalyser) match {
          case Right(rightTree) =>
            val numberTree: Tree = Tree(numberToken.toString, "number", Nil)
            Right(Tree("E", "nonterminal", Seq(numberTree, rightTree)))
          case left: Left[String, Tree] => left
        }
      case _ => Left("Expected number, but got " + lexicalAnalyser.currentToken)
    }
  }

  private def parseEPrime(lexicalAnalyser: LexicalAnalyser): Either[String, Tree] = {
    if (lexicalAnalyser.currentToken.exists(_.isInstanceOf[NumberToken])) {
      parseE(lexicalAnalyser) match {
        case Right(left) =>
          parseEPrimePrime(lexicalAnalyser) match {
            case Right(right) => Right(Tree("E'", "nonterminal", Seq(left, right)))
            case left: Left[String, Tree] => left
          }
        case left: Left[String, Tree] => left
      }
    } else {
      Right(Tree("E'", "nonterminal", Seq(epsilon)))
    }
  }

  private def parseEPrimePrime(lexicalAnalyser: LexicalAnalyser): Either[String, Tree] = {
    lexicalAnalyser.currentToken match {
      case Some(op: BinaryOperation) =>
        val operationTree = new Tree(op.toString, "operation", Nil)
        lexicalAnalyser.nextToken()
        parseEPrime(lexicalAnalyser) match {
          case Right(right) => Right(Tree("E''", "nonterminal", Seq(operationTree, right)))
          case left: Left[String, Tree] => left
        }
      case _ => Left("Expected operation, but got " + lexicalAnalyser.currentToken)
    }
  }

  def parse(string: String): Either[String, Tree] = {
    val lexicalAnalyser = new LexicalAnalyser(string)
    lexicalAnalyser.nextToken()
    parseE(lexicalAnalyser) match {
      case right: Right[String, Tree] if lexicalAnalyser.currentToken.nonEmpty =>
        Left("Expected end of input, but got " + lexicalAnalyser.currentToken)
      case either => either
    }
  }
}