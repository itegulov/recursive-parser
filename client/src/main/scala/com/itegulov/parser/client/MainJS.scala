package com.itegulov.parser.client

import com.itegulov.parser.client.dagre.Dagre
import com.itegulov.parser.shared.SharedMessages
import org.scalajs.dom
import org.scalajs.dom.{KeyboardEvent, html}
import org.scalajs.jquery._
import org.singlespaced.d3js.d3

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSName, ScalaJSDefined}

/**
  * @author itegulov
  */
object MainJS extends JSApp {

  @JSExport
  def main: Unit = {
    val sentenceInput = dom.document.getElementById("sentence_input").asInstanceOf[html.Input]
    sentenceInput.onkeypress = (e: KeyboardEvent) => {
      if (e.charCode == 13) {
        renderText(sentenceInput.value)
        e.preventDefault()
      }
    }
    renderText(SharedMessages.defaultExpression)
  }

  @ScalaJSDefined
  class Tree(val node: String, val ne: String, val children: js.Array[Tree]) extends js.Object

  @ScalaJSDefined
  class Node(val id: Int, val label: String, val nodeClass: String) extends js.Object

  @ScalaJSDefined
  class Edge(val source: Int, val target: Int, val id: String) extends js.Object

  @JSName("moveOnZoom")
  @js.native
  object moveOnZoom extends js.Object {
    def apply(svg: js.Any, svgGroup: js.Any): Unit = js.native
  }

  private def renderText(text: String): Unit = {
    jQuery.getJSON("parse", js.Dictionary("expression" -> text), { (tree: Tree, textStatus: String, jqXHR: JQueryXHR) =>
      jQuery("svg-canvas").empty()

      val nodes = new ArrayBuffer[Node]()
      val edges = new ArrayBuffer[Edge]()

      populate(tree, nodes, edges)

      val g = Dagre.newD3Digraph

      nodes.foreach(node =>
        g.setNode(node.id.toString, js.Dictionary("label" -> node.label, "class" -> node.nodeClass, "rx" -> 5, "ry" -> 5))
      )

      edges.foreach(edge =>
        g.setEdge(edge.source.toString, edge.target.toString, js.Dictionary("lineTension" -> 0.8, "lineInterpolate" -> "bundle"))
      )

      val render = Dagre.newD3Renderer

      val svg = d3.select("#svg-canvas")
      val svgGroup = svg.append("g")

      render(d3.select("#svg-canvas g"), g)

      svgGroup.attr("transform", "translate(5, 5)")
      moveOnZoom(svg, svgGroup)
    })
  }

  private def populate(tree: Tree, nodes: ArrayBuffer[Node], edges: ArrayBuffer[Edge]): Node = {
    val newNode = new Node(nodes.length, tree.node, "ne-" + tree.ne)

    nodes += newNode

    tree.children.foreach(child => {
      val childNode = populate(child, nodes, edges)
      edges += new Edge(newNode.id, childNode.id, newNode.id + "-" + childNode.id)
    })

    newNode
  }
}
