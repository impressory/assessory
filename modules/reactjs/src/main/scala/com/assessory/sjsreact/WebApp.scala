package com.assessory.sjsreact

import com.assessory.sjsreact.Navigation.NCourse

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

import JSImports._
import JSReact._

object WebApp extends JSApp {

/*  val c = React.createClass(
    j(
      displayName = "CommentBox",
      render = () => {
        div("myclass")(
          "using div func",
          "far"
        )
      }
    )
  )

  val c2 = TypedReact.createClass(component("TypedBox",
    render = () => div("typedBox")(
      "using typed functions",
      "far"
    )
  ))*/

  @JSExport
  override def main(): Unit = {

    val s = React.renderToStaticMarkup(e(Navigation.BreadCrumbs, null))
    println(s)

    Navigation.path.value = NCourse("hello") :: Nil
  }


}
