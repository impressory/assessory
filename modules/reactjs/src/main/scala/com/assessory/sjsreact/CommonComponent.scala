package com.assessory.sjsreact

import japgolly.scalajs.react.{ReactElement, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object CommonComponent {


  def latchedRender[T](name:String)(render: T => ReactElement) = {
    val inner = ReactComponentB[T](name)
      .render(render)
      .build

    ReactComponentB[Latched[T]]("Latched"+name)
      .render({ l:Latched[T] =>
        l.request.value match {
          case Some(Success(x)) => inner(x)
          case Some(Failure(x)) => <.span(^.className := "error", x.getMessage)
          case _ => <.i(^.className := "fa fa-spinner fa-spin")
        }
      })
      .build
  }

  def futureRender[T](name:String)(render: T => ReactElement) = {
    val inner = ReactComponentB[T](name)
      .render(render)
      .build

    ReactComponentB[Future[T]]("Future"+name)
      .render({ f:Future[T] =>
        f.value match {
          case Some(Success(x)) => inner(x)
          case Some(Failure(x)) => <.span(^.className := "error", x.getMessage)
          case _ => <.i(^.className := "fa fa-spinner fa-spin")
        }
      })
      .build
  }


}
