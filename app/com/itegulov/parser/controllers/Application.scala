package com.itegulov.parser.controllers

import java.text.ParseException

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
    Ok(views.html.com.itegulov.parser.index())
  }

  def parseExpression(expression: String) = Action {
    Logger.info(s"<-- New request for parsing '$expression'")
    val answer = try {
      Ok(Json.toJson(parser.parse(expression)))
    } catch {
      case e: ParseException => BadRequest(JsObject(Map("error" -> JsString("Couldn't parse"))))
      case e: Exception => BadRequest(JsObject(Map("error" -> JsString("Oops, something went wrong"))))
    }
    Logger.info(s"--> Responding with '${answer}'")
    answer
  }

}