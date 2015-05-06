package com.assessory.sjsreact

import com.assessory.sjsreact.services.GroupService
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{Group, Course}
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object GroupViews {

  val groupInfo = ReactComponentB[Group]("GroupInfo")
    .render({ group =>
      val name = group.name.getOrElse("Untitled group")
      <.h3(
        <.small(GroupSetViews.groupSetIdName(group.set)), <.br(),
        <.a(^.href := "TODO", name)
      )
    })
    .build


  val groupInfoList = CommonComponent.latchedRender[Seq[Group]]("GroupInfoList") { groups =>
    <.div(
      for { g <- groups } yield groupInfo(g)
    )
  }

  val myGroups = ReactComponentB[Id[Course, String]]("MyGroups")
    .initialStateP(s => GroupService.myGroups(s))
    .render { (a, b, c) => groupInfoList(c) }
    .build

}
