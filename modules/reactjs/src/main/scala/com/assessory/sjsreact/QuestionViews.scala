package com.assessory.sjsreact

import com.assessory.api.{TargetTaskOutput, Task, Target}
import com.wbillingsley.handy.appbase._
import japgolly.scalajs.react.{ReactEventI, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

object QuestionViews {

  val shortTextQ = ReactComponentB[(ShortTextQuestion,ShortTextAnswer)]("questionnaireEdit")
    .render { tuple =>
      val (q,a) = tuple

      <.div(
        <.textarea(^.value := (a.answer.getOrElse(""):String),
          ^.onChange ==> { (evt:ReactEventI) => a.answer = Some(evt.target.value); WebApp.rerender() }
        )
      )
    }
    .build

  val booleanQ = ReactComponentB[(BooleanQuestion,BooleanAnswer)]("questionnaireEdit")
    .render({ tuple =>
    val (q,a) = tuple

    <.div(
      <.label(^.className := "radio-inline", <.input(^.`type` := "radio",
        ^.checked := a.answer == Some(true),
        ^.onChange ==> { (evt:ReactEventI) => a.answer = Some(evt.target.checked); WebApp.rerender() }, "Yes")
      ),
      <.label(^.className := "radio-inline", <.input(^.`type` := "radio",
        ^.checked := a.answer == Some(false),
        ^.onChange ==> { (evt:ReactEventI) => a.answer = Some(!evt.target.checked); WebApp.rerender() }, "No")
      )
    )
  })
    .build



  val questionnaire = ReactComponentB[Seq[(Question,Answer[_])]]("questionnaireEdit")
    .render({ seq =>
      <.div(
        for (pair <- seq) yield pair match {
          case (q:ShortTextQuestion, a:ShortTextAnswer) => <.div(<.label(q.prompt), shortTextQ((q,a)))
          case (q:BooleanQuestion, a:BooleanAnswer) => <.div(<.label(q.prompt), booleanQ((q,a)))
        }
      )
    })
    .build




}
