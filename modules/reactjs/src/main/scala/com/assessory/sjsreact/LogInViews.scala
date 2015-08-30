package com.assessory.sjsreact

import com.assessory.api.client.EmailAndPassword
import com.assessory.sjsreact.services.{UserService, CourseService}
import com.wbillingsley.handy.appbase.UserError
import japgolly.scalajs.react.{ReactEventI, BackendScope, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.concurrent.Future

object LogInViews {

  class LogInBackend($: BackendScope[_, (EmailAndPassword, Latched[String])]) {

    def email(e: ReactEventI) = $.modState { case (ep, ls) => (ep.copy(email = e.target.value), ls) }

    def password(e: ReactEventI) = $.modState { case (ep, ls) => (ep.copy(password = e.target.value), ls) }

    def logIn(e: ReactEventI) = $.modState { case (ep, ls) =>
      val v = UserService.logIn($.state._1).map(_ => "Logged in").recoverWith { case x => Future.failed(UserError("Log in failed")) }
      (ep, Latched.lazily(v))
    }
  }

  val socialLogIn = ReactComponentB[Unit]("Social Login")
    .render(_ =>
      <.div(^.className := "col-sm-6",
        <.h1("or"),
        <.form(^.action := "/oauth/github", ^.method := "POST",
          <.button(^.className:= "btn btn-default", ^.`type` := "submit",
            <.i(^.className := "icon-github"), "Sign in with ", <.b("GitHub")
          )
        )
      )
    )
    .buildU

  val logIn = ReactComponentB[Unit]("Front")
    .initialState((EmailAndPassword("", ""), Latched.immediate("")))
    .backend(new LogInBackend(_))
    .render({ (props, children, state, backend) =>
      <.div(
        Front.siteHeader("Hello"),

        <.div(^.className := "container",
          <.div(^.className := "row",

            <.div(^.className := "col-sm-6",
              <.h1("Log In"),
              <.form(^.className := "form",
                <.div(^.className := "form-group",
                  <.input(^.`type`:="text", ^.placeholder:="Email address", ^.onChange==>backend.email)
                ),
                <.div(^.className := "form-group",
                  <.input(^.`type`:="password", ^.placeholder:="Password", ^.onChange==>backend.password)
                ),
                <.div(^.className := "form-group",
                  <.button(^.className:="btn btn-primary", ^.disabled := !state._2.isCompleted, ^.onClick==>backend.logIn, "Log In"),
                  CommonComponent.latchedString(state._2)
                )
              )
            ),

            socialLogIn()
          )
        )

      )
    })
    .buildU


  class SignUpBackend($: BackendScope[_, (EmailAndPassword, Latched[String])]) {

    def email(e: ReactEventI) = $.modState { case (ep, ls) => (ep.copy(email = e.target.value), ls) }

    def password(e: ReactEventI) = $.modState { case (ep, ls) => (ep.copy(password = e.target.value), ls) }

    def logIn(e: ReactEventI) = $.modState { case (ep, ls) =>
      val v = UserService.signUp($.state._1).map(_ => "Logged in").recoverWith { case x => Future.failed(UserError("Sign up failed")) }
      (ep, Latched.lazily(v))
    }
  }


  val signUp = ReactComponentB[Unit]("SignUp")
    .initialState((EmailAndPassword("", ""), Latched.immediate("")))
    .backend(new SignUpBackend(_))
    .render({ (props, children, state, backend) =>
    <.div(
      Front.siteHeader("Hello"),

      <.div(^.className := "container",
        <.div(^.className := "row",

          <.div(^.className := "col-sm-6",
            <.h1("Sign Up"),
            <.form(^.className := "form",
              <.div(^.className := "form-group",
                <.input(^.`type`:="text", ^.placeholder:="Email address", ^.onChange==>backend.email)
              ),
              <.div(^.className := "form-group",
                <.input(^.`type`:="password", ^.placeholder:="Password", ^.onChange==>backend.password)
              ),
              <.div(^.className := "form-group",
                <.button(^.className:="btn btn-primary", ^.disabled := !state._2.isCompleted, ^.onClick==>backend.logIn, "Log In"),
                CommonComponent.latchedString(state._2)
              )
            )
          ),

          socialLogIn()
        )
      )

    )
  })
  .buildU

}
