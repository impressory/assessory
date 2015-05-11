package com.assessory.sjsreact

import com.assessory.api._
import com.assessory.api.client.WithPerms
import com.assessory.api.critique.{CritiqueTask, CritAllocation, Critique}
import com.assessory.sjsreact.services.TaskOutputService
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{ShortTextAnswer, BooleanAnswer, Answer, Question}
import japgolly.scalajs.react.{BackendScope, ReactEventI, ReactElement, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object CritiqueViews {

  case class Selection[T,C](private var _selected: Option[T], seq:Seq[T], context:C) {
    def selected_=(o:Option[T]) = {
      this._selected = o
      WebApp.rerender()
    }

    def selected = _selected
  }

  case class Saveable[T,R](item: T, l:Latched[R])


  val allocationsSwitch = ReactComponentB[Selection[Target,Task]]("critAllocationSelection")
    .render { sel =>
       <.ul(^.className := "nav nav-pills", ^.role := "group",
         for ((targ, idx) <- sel.seq.zipWithIndex) yield {
           <.li(^.className := (if (sel.selected == Some(targ)) "active" else ""), ^.role := "presentation",
             <.a(^.onClick ==> { (e:ReactEventI) => sel.selected = Some(targ) },
               targ match {
                 case TargetGroup(g) => GroupViews.groupNameId(g)
                 case TargetTaskOutput(to) => idx
                 case x => "Unrecognised " + x.kind
               }
             )
           )
         }
       )
    }
    .build

  val reviewTarget = ReactComponentB[Selection[Target,Task]]("reviewTarget")
    .render { sel =>
      sel.selected match {
        case Some(TargetTaskOutput(to)) => <.div(to.id)
        case _ => <.div()
      }
    }
    .build


  class CompleteCritBackend($: BackendScope[(Task, WithPerms[TaskOutput]), (Seq[Answer[_]], Latched[String])]) {

    def save(e:ReactEventI) = {
      println("SAVE")
      $.props._2.item.body match {
        case crit:Critique =>
          $.modState { case (answers, latchStr) =>
            val toSave = $.props._2.item.copy(body = crit.copy(answers = answers))
            val f = TaskOutputService.updateBody(toSave)
            val newLatch = Latched.future(f.map(_ => ""))
            (answers, newLatch)
          }
        case _ => $.modState { case (answers, latchStr) => (answers, Latched.immediate("Case mismatch")) }
      }
    }

    def unchanged: Boolean = {
      $.props._2.item.body match {
        case crit: Critique => crit.answers == $.state._1
      }
    }

  }

  def copyAns(a:Answer[_]):Answer[_] = a match {
    case a:ShortTextAnswer => a.copy()
    case a:BooleanAnswer => a.copy()
  }


  val status = CommonComponent.latchedRender[String]("status") { str => <.span(str) }

  val completeCrit = CommonComponent.latchedX[(Task, WithPerms[TaskOutput])]("completeCrit") { comp =>
    comp.initialStateP { case (task, wp) => wp.item.body match {
      case c:Critique => (c.answers.map(copyAns), Latched.immediate(""))
    }}
    .backend(new CompleteCritBackend(_))
    .render({ (props, children, state, backend) =>
      <.div(
        props._1.body match {
          case critTask:CritiqueTask => QuestionViews.questionnaire(critTask.questionnaire.zip(state._1))
          case _ => <.div("Unexpected content - didn't seem to be a critique")
        },
        <.button(^.className := "btn btn-primary ", ^.disabled := backend.unchanged, ^.onClick ==> backend.save, "Save"),
        status(state._2)
      )
    })
    .componentWillReceiveProps { case (scope, (task, wp)) =>
      if (scope.props != (task, wp)) {
        scope.setState(wp.item.body match {
          case c:Critique => (c.answers.map(copyAns), Latched.future(Future.successful("")))
        })
      }
    }
    .build
  }


  val critFormTargF = CommonComponent.latchedRender[(Task, Id[TaskOutput, String])]("critFormTarg") {
    case (task, id) =>
      val fPair = TaskOutputService.future(id).map((task, _))
      completeCrit(Latched.future(fPair))
  }


  /**
   * Find and load the critique for this target
   */
  val critFormTarg = ReactComponentB[(Target,Task)]("critFormTarg")
    .initialStateP { case (target,task) => TaskOutputService.findOrCreateCrit(task.id, target) }
    .render { (props, children, state) =>
      val lpair = for {
        outputId <- state
      } yield (props._2, outputId)

      critFormTargF(Latched.future(lpair))
    }
    .componentWillReceiveProps { case (scope, (target, task)) =>
      if (scope.props != (target,task)) {
        scope.setState(
          TaskOutputService.findOrCreateCrit(task.id, target)
        )
      }
    }
    .build



  val frontInt = CommonComponent.latchedRender[Selection[Target,Task]]("critiqueTaskViewInt") { sel =>
    <.div(
      allocationsSwitch(sel),
      reviewTarget(sel),
      sel.selected match {
        case Some(t) => critFormTarg((t, sel.context))
        case _ => <.div()
      }
    )
  }

  val front = ReactComponentB[Task]("critiqueTaskView")
    .initialStateP(task => Latched.future{
      for (alloc <- TaskOutputService.myAllocations(task.id)) yield new Selection(None, alloc, task)
    })
    .render((a, b, c) => frontInt(c))
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
