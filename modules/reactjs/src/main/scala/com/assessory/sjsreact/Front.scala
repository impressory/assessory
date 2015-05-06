package com.assessory.sjsreact

import com.assessory.sjsreact.services.{CourseService, UserService}
import com.wbillingsley.handy.appbase.{Course, User}
import com.wbillingsley.handy.Ref._
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object Front {

  val siteHeader = ReactComponentB[Unit]("SiteHeader")
    .render { router =>
      <.div(^.className := "site-header",
        <.div(^.className := "navbar navbar-static-top",
          <.div(^.className := "container",
            MainRouter.rootLink(^.className := "navbar-brand", "Assessory"),
            <.ul(^.className := "nav navbar-nav"),
            loginStatus(UserService.self)
          )
        )
      )
    }
    .buildU

  val front = ReactComponentB[MainRouter.Router]("Front")
    .render({ router =>
      <.div(
        siteHeader("Hello"),
        myCourses(CourseService.myCourses)
      )
    })
    .build


  val myCourses = CommonComponent.latchedRender[Option[Seq[Course]]]("MyCourses") {
    case Some(courses) =>
      <.div(^.className := "container",
        <.h2("My Courses"),
        <.div(for { course <- courses } yield CourseViews.courseInfo(course))
      )
    case _ =>
      <.div(^.className := "container")
  }

  val loginStatus = CommonComponent.latchedRender[Option[User]]("LoginStatus") {
    case Some(u) =>
      val name: String = u.name.getOrElse("Anonymous")

      <.ul(^.className := "nav navbar-nav pull-right",
        <.li(<.a(^.onClick ==> { _: Any => UserService.logOut() }, "Log out")),
        <.li(<.a(name))
      )
    case _ =>
      <.ul(^.className := "nav navbar-nav pull-right",
        <.li(<.a("Log in")),
        <.li(<.a("Sign up"))
      )
  }

}

