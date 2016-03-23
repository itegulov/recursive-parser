package com.itegulov.parser

/**
  * @author itegulov
  */
case class Tree(node: String, ne: Option[String], children: Seq[Tree])