package com.assessory.play.controllers

import com.assessory.api.client.EmailAndPassword
import com.assessory.api.wiring.Lookups._
import com.assessory.clientpickle.Pickles._
import com.assessory.model._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{ActiveSession, User, UserError}
import com.wbillingsley.handyplay.RefConversions._
import com.wbillingsley.handyplay.{DataAction, WithHeaderInfo}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{BodyParsers, Controller, Result, Results}

import scala.concurrent.Future
import scala.language.implicitConversions



object UserController extends Controller {

  implicit def userToResult(rc:Ref[User]):Ref[Result] = {
    rc.map(c => Results.Ok(upickle.write(c)).as("application/json"))
  }

  implicit def userToFResult(rc:Ref[User]):Future[Result] = userToResult(rc).toFuture

  implicit def manyCourseToResult(rc:RefMany[User]):Ref[Result] = {
    val strings = rc.map(c => upickle.write(c))

    for {
      enum <- strings.enumerateR
    } yield Results.Ok.chunked(enum.stringifyJsArr).as("application/json")
  }


  def self = DataAction.returning.resultWH(BodyParsers.parse.empty) { implicit request =>
    WithHeaderInfo(request.approval.who, headerInfo)
  }

  /**
   * Creates a user and logs them in
   */
  def signUp = DataAction.returning.resultWH { implicit request =>
    def ru = for {
      text <- request.body.asText.toRef orIfNone UserError("You must supply an email and password")
      ep = upickle.read[EmailAndPassword](text)
      u <- UserModel.signUp(
        oEmail = Some(ep.email),
        oPassword = Some(ep.password),
        session = ActiveSession(request.sessionKey, ip=request.remoteAddress)
      )
    } yield u

    WithHeaderInfo(ru, headerInfo)
  }

  /**
   * Logging a user in involves finding the user (if the password hash matches), and pushing the
   * current session key as an active session
   */
  def logIn = DataAction.returning.resultWH { implicit request =>
    def ru = for {
      text <- request.body.asText.toRef orIfNone UserError("You must supply an email and password")
      ep = upickle.read[EmailAndPassword](text)
      u <- UserModel.logIn(
        oEmail = Some(ep.email),
        oPassword = Some(ep.password),
        session = ActiveSession(request.sessionKey, ip=request.remoteAddress)
      )
    } yield u

    WithHeaderInfo(ru, headerInfo)
  }

  def autologin(user:Id[User,String], secret:String) = DataAction.returning.resultWH { implicit request =>
    val loggedIn = for {
      u <- UserModel.secretLogIn(
        ru = user.lazily,
        secret = secret,
        activeSession = ActiveSession(request.sessionKey, request.remoteAddress)
      )
    } yield Redirect(routes.Application.index())

    WithHeaderInfo(
      loggedIn,
      headerInfo
    )
  }

  /**
   * To log a user out, we just have to remove the current session from their active sessions
   */
  def logOut = DataAction.returning.resultWH { implicit request =>
    WithHeaderInfo(
      UserModel.logOut(
        rUser = request.user,
        session = ActiveSession(request.sessionKey, ip=request.remoteAddress)
      ),
      headerInfo
    )
  }

  def findMany = DataAction.returning.resultWH { implicit request =>
    def wp = for {
      text <- request.body.asText.toRef
      ids = upickle.read[Ids[User,String]](text)
      wp <- UserModel.findMany(request.approval, ids)
    } yield wp

    WithHeaderInfo(wp, headerInfo)
  }

}
