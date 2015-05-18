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

  val logIn = ReactComponentB[MainRouter.Router]("Front")
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
            )
          )
        )

      )
    })
    .build
}

/*
<div class="container">
<div class="row">

	<div class="col-sm-6">
		<h1>Log In</h1>
		  <div class="well">
		    <form name="signup" role="form" class="form">
		      <div class="form-group">
		        <label>Email address</label>
		        <input type="text" class="form-control" ng-model="user.email" />
		      </div>
		      <div class="form-group">
		        <label>Password</label>
		        <input type="password" class="form-control" ng-model="user.password" />
		      </div>
		      <div ng-repeat="error in errors">
		        <div class="alert alert-danger">{{ error }}</div>
		      </div>
		      <div class="form-group">
		        <button class="btn btn-primary" ng-click="submit(user)">Log In</button>
		      </div>
		    </form>
		  </div>
	</div>

	<div class="col-sm-6">
	  <h1>or</h1>

	  <social-log-in></social-log-in>
	</div>

</div>
</div>

 */