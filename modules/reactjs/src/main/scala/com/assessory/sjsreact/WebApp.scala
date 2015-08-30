package com.assessory.sjsreact

import com.assessory.api.client.invalidId
import com.wbillingsley.handy.appbase.Course
import com.assessory.clientpickle.Pickles._
import japgolly.scalajs.react.extra.router.{Router, BaseUrl}
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._


object WebApp extends JSApp {

  val mountNode = dom.document.body
  val root = React.render(MainRouter.router(), mountNode)

  def rerender() = root.forceUpdate()

  @JSExport
  override def main(): Unit = {

  }


}
