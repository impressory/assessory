package com.assessory.sjsreact

import com.assessory.api.client.WithPerms
import com.assessory.sjsreact.services.GroupService
import com.wbillingsley.handy.appbase.Course
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object CourseViews {

  val courseInfo = ReactComponentB[Course]("CourseInfo")
    .render({ course =>
      <.div(^.className := "course-info",
        <.div(^.className := "media",
          <.div(^.className := "pull-left",
            <.span(^.className := "cover-image", <.img(^.src := course.coverImage.getOrElse("http://placehold.it/100x100")))
          )
        ),
        <.div(^.className := "media-body",
          <.h4(^.className := "media-heading", course.shortName),
          <.h2(^.className := "media-heading", <.a(^.href := MainRouter.courseHome(course.id), course.title)),
          <.p(course.shortDescription)
        )

      )
    })
    .build

  val courseInfoL = CommonComponent.latchedRender[WithPerms[Course]]("CourseInfoL") { wp =>
    courseInfo(wp.item)
  }

  val courseFront = CommonComponent.latchedRender[WithPerms[Course]]("CourseFront") { wp =>
    <.div(
      Front.siteHeader(""),
      <.div(^.className := "course-view",
        <.div(^.className := "container",

          courseInfo(wp.item),

          <.div(^.className := "row",
            <.div(^.className := "col-sm-8",
              <.h3("Tasks"),
              <.div(
                TaskViews.courseTasks(wp.item.id)
              )
            ),

            <.div(^.className := "col-sm-4",
              <.h3("Groups"),
              GroupViews.myGroups(wp.item.id)
            )
          )

        )
      )
    )
  }

}
