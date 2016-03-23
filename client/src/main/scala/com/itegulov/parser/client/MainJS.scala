package com.itegulov.parser.client

import com.itegulov.parser.shared.SharedMessages
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

/**
  * @author itegulov
  */
class MainJS extends JSApp {

  @JSExport
  override def main(): Unit = {
    dom.document.getElementById("scalajsShoutOut").textContent = SharedMessages.itWorks
  }
}
