package com.assessory.sjsreact

import com.assessory.api.client.WithPerms
import com.assessory.sjsreact.services.{GroupSetService, GroupService}
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{GroupSet, Course, Group}
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object GroupSetViews {

  val groupSetName = CommonComponent.latchedRender[WithPerms[GroupSet]]("GroupSetH3") { wp =>
    val name = wp.item.name.getOrElse("Untitled group set")
    <.span(name)
  }

  val groupSetIdName = ReactComponentB[Id[GroupSet,String]]("GroupSetH3Id")
    .initialStateP(id => GroupSetService.latch(id.id))
    .render({ (a, b, c) => groupSetName(c)})
    .build

}
