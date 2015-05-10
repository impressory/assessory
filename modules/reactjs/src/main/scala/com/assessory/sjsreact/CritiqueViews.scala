package com.assessory.sjsreact

import com.assessory.api.{Target, Task}
import com.assessory.api.critique.{CritAllocation, Critique}
import com.assessory.sjsreact.services.TaskOutputService
import japgolly.scalajs.react.{ReactElement, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object CritiqueViews {

  val allocationsSwitch = CommonComponent.latchedRender[Seq[Target]]("critAllocationSelection") { seq =>
    <.div(
      for (a <- seq) yield <.div(a.toString)
    )
  }

  val front = ReactComponentB[Task]("critiqueTaskView")
    .initialStateP(task => TaskOutputService.myAllocations(task.id))
    .render((a, b, c) =>
      allocationsSwitch(c)
    )
    .build

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
