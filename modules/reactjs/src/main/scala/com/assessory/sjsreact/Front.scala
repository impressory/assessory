package com.assessory.sjsreact

import com.assessory.api.Permissions
import com.assessory.api.client.WithPerms
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
            <.a(^.className := "navbar-brand", "Assessory", ^.href := MainRouter.Home.path),
            <.ul(^.className := "nav navbar-nav"),
            loginStatus(UserService.self)
          )
        )
      )
    }
    .buildU


  val myCourses = CommonComponent.latchedRender[Option[Seq[WithPerms[Course]]]]("MyCourses") {
    case Some(courses) =>
      <.div(^.className := "container",
        <.h2("My Courses"),
        <.div(for { course <- courses } yield CourseViews.courseInfo(course))
      )
    case _ =>
      <.div(^.className := "container")
  }


  val front = ReactComponentB[Unit]("Front")
    .render({ router =>
      <.div(
        siteHeader("Hello"),
        myCourses(CourseService.myCourses)
      )
    })
    .buildU


  def createCourse() = CommonComponent.ifPermitted(Permissions.CreateCourse)(
    <.div(^.cls := "container",
      <.a(^.cls := "btn btn-default", ^.href := MainRouter.CreateCourseP.path,
        "Create a course"
      )
    )
  )

  val loginStatus = CommonComponent.latchedRender[Option[User]]("LoginStatus") {
    case Some(u) =>
      val name: String = u.name.getOrElse("Anonymous")

      <.ul(^.className := "nav navbar-nav pull-right",
        <.li(<.a(^.onClick ==> { _: Any => UserService.logOut() }, "Log out")),
        <.li(<.a(name))
      )
    case _ =>
      <.ul(^.className := "nav navbar-nav pull-right",
        <.li(<.a(^.href:=MainRouter.LogIn.path, "Log in")),
        <.li(<.a(^.href:=MainRouter.SignUp.path,"Sign up"))
      )
  }

}

