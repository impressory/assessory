package com.assessory.play.controllers

import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.UserError
import com.wbillingsley.handyplay.{DataAction, HeaderInfo, WithHeaderInfo}
import play.api.mvc.{Action, Controller}

object Application extends Controller {

  /**
   * A response to all OPTIONS requests, to support CORS
   */
  def options(path:String) = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      Status(200).itself,
      HeaderInfo(headers=Seq(
        "Access-Control-Allow-Origin" -> uiBaseUrl,
        "Access-Control-Allow-Headers" -> request.headers.get("Access-Control-Request-Headers").getOrElse("accept, content-type"),
        "Access-Control-Allow-Method" -> request.headers.get("Access-Control-Request-Method").getOrElse("GET, POST, OPTIONS"),
        "Access-Control-Allow-Credentials" -> "true"
      )).itself
    )
  }

  /**
   * The HTML and Javascript for the client side of the app.
   * This also ensures the user's Play session cookie includes
   * a value for sessionKey.
   */
  def index = DataAction.forceSession(
    Action {
      Ok(views.html.index())
    }
  )

  /**
   * As we're using Angular.js, there may be routes (paths) in the browser
   * that don't correspond to routes on the server. In which case, if a user
   * hits refresh, we want to ensure they don't suddenly receive a 404 from the
   * server.
   *
   * Instead, we have a default route that returns the index page.
   */
  def defaultRoute(path:String) = index

  def notFound = DataAction.returning.result { RefNone }

  def userError = DataAction.returning.result { implicit request =>
    RefFailed(UserError("Testing a user error"))
  }

}
