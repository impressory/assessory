package com.assessory.sjsreact

import com.assessory.api.client.WithPerms
import com.assessory.sjsreact.services.{CourseService, GroupService}
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.Course
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object CourseViews {

  val courseAdmin = ReactComponentB[WithPerms[Course]]("CourseAdmin")
    .render({ wp =>
      if (wp.perms("edit")) {
        <.div(<.a(^.href:=s"api/course/${wp.item.id.id}/autolinks.csv", "autolinks.csv"))
      } else <.div()
    }).build


  val courseInfo = ReactComponentB[WithPerms[Course]]("CourseInfo")
    .render({ wp =>
      <.div(^.className := "course-info",
        <.div(^.className := "media",
          <.div(^.className := "pull-left",
            <.span(^.className := "cover-image", <.img(^.src := wp.item.coverImage.getOrElse("http://placehold.it/100x100")))
          )
        ),
        <.div(^.className := "media-body",
          <.h4(^.className := "media-heading",  wp.item.shortName),
          <.h2(^.className := "media-heading", <.a(^.href := MainRouter.CourseP.path( wp.item.id), wp.item.title)),
          courseAdmin(wp),
          <.p(wp.item.shortDescription)
        )

      )
    })
    .build

  val courseInfoL = CommonComponent.latchedRender[WithPerms[Course]]("CourseInfoL") { wp =>
    courseInfo(wp)
  }

  val courseFrontL = CommonComponent.latchedRender[WithPerms[Course]]("CourseFront") { wp =>
    <.div(
      Front.siteHeader(""),
      <.div(^.className := "course-view",
        <.div(^.className := "container",

          courseInfo(wp),

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

  val courseFront = ReactComponentB[Id[Course,String]]("CourseFront")
    .initialStateP(id => CourseService.latch(id))
    .render({ (p, r, s) =>
       courseFrontL(s)
    })
    .build

  val createCourse = ReactComponentB[Unit]("CreateCourse")
    .initialState(Course(id=invalidId, addedBy=invalidId))
    .render({ (p, c, s) =>
      <.div(
        Front.siteHeader(),
        <.div(^.cls := "container",
          <.h3("New course"),
          <.div(^.cls := "form"

          )
        )
      )
    })
    .buildU

}
