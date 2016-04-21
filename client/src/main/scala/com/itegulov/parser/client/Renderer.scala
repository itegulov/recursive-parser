package com.itegulov.parser.client

import org.scalajs.dom
import org.scalajs.dom._

import scala.scalajs.js
import org.scalajs.jquery.jQuery

import js.Dynamic.{global => g}
import js.annotation.JSExport

/**
  * @author Daniyar Itegulov
  */
@JSExport
object Renderer {

  val fs = g.require("fs")

  @JSExport
  def main(): Unit = {
    val ipc = g.require("electron").ipcRenderer
    val sentenceInput = dom.document.getElementById("sentence_input").asInstanceOf[html.Input]
    sentenceInput.onkeypress = (e: KeyboardEvent) => {
      if (e.charCode == 13) {
        println("Got: " + sentenceInput.value)
        ipc.send("parseAction", sentenceInput.value)
        e.preventDefault()
      }
    }

    jQuery("body").append("<p>Hello World from Scala.js</p>")
    val filenames = listFiles(".")
    display(filenames)
  }

  def display(filenames: Seq[String]) = {
    jQuery("body").append("<p>Listing the files in the '.' using io.js API:")
    jQuery("body").append("<ul>")
    filenames.foreach { filename =>
      jQuery("body").append(s"<li>$filename</li>")
    }
    jQuery("body").append("</ul></p>")
  }

  def listFiles(path: String): Seq[String] = {
    fs.readdirSync(path).asInstanceOf[js.Array[String]]
  }
}
