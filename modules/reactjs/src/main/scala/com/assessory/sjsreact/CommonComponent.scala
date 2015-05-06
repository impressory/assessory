package com.assessory.sjsreact

import japgolly.scalajs.react.{ReactElement, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

object CommonComponent {


  def latchedRender[T](name:String)(render: T => ReactElement) = {
    val inner = ReactComponentB[T](name)
      .render(render)
      .build

    ReactComponentB[Latched[T]]("Latched"+name)
      .render({ value:Latched[T] =>
        value.request match {
          case Some(v) => inner(v)
          case _ =>
            value.error match {
              case Some(t) => <.span(^.className := "error", t.getMessage)
              case _ => <.span("Loading...")
            }
        }
      })
      .build

  }


  val latched = ReactComponentB[(Latched[_],ReactElement)]("LatchedVar")
    .render({ arg =>
      val latched = arg._1
      val el = arg._2

      latched.request match {
        case Some(v) => el
        case _ => <.span("Waiting")
      }
    })
    .build

}
