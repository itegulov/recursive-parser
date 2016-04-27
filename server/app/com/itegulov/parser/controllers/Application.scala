package com.itegulov.parser.controllers

import com.itegulov.parser.shared.SharedMessages
import com.itegulov.parser.{Parser, Tree}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {
  val parser = new Parser()

  implicit val treeWrites = new Writes[Tree] {
    override def writes(tree: Tree) = Json.obj(
      "node" -> tree.node,
      "ne" -> JsString(tree.ne.getOrElse("none")),
      "children" -> Json.toJson(tree.children.map(writes(_)))
    )
  }

  def index = Action {
    Ok(views.html.com.itegulov.parser.index(SharedMessages.defaultExpression))
  }

  def parseExpression(expression: String) = Action {
    Logger.info(s"<-- New request for parsing '$expression'")
    val answer = parser.parse(expression) match {
      case Right(tree) =>
        Ok(Json.toJson(tree))
      case Left(error) =>
        BadRequest(JsObject(Map("error" -> JsString(error))))
    }
    Logger.info(s"--> Responding with '$answer'")
    answer
  }
}