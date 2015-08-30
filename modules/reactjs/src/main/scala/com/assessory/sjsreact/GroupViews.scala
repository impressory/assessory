package com.assessory.sjsreact

import com.assessory.api.client.WithPerms
import com.assessory.sjsreact.services.GroupService
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{Group, Course}
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object GroupViews {

  val groupInfo = ReactComponentB[WithPerms[Group]]("GroupInfo")
    .render({ wp =>
      val group = wp.item
      val name = group.name.getOrElse("Untitled group")
      <.h3(
        <.small(GroupSetViews.groupSetIdName(group.set)), <.br(),
        <.a(^.href := MainRouter.GroupP.path(group.id), name)
      )
    })
    .build

  val groupName = ReactComponentB[WithPerms[Group]]("GroupName").render(wp => <.span(wp.item.name.getOrElse("Untitled group"):String)).build
  val groupNameL = CommonComponent.latchedRender[WithPerms[Group]]("GroupNameL") { wp => groupName(wp) }

  val groupNameId = ReactComponentB[Id[Group,String]]("GroupName").render(id => groupNameL(GroupService.latch(id))).build

  val groupInfoList = CommonComponent.latchedRender[Seq[WithPerms[Group]]]("GroupInfoList") { groups =>
    <.div(
      for { g <- groups } yield groupInfo(g)
    )
  }

  val myGroups = ReactComponentB[Id[Course, String]]("MyGroups")
    .initialStateP(s => GroupService.myGroupsInCourse(s))
    .render { (a, b, c) => groupInfoList(c) }
    .build

}
