package com.assessory.play.controllers

import play.api.mvc.Controller
import com.assessory.play.json.UserToJson

import com.assessory.model._

import com.assessory.api._
import com.assessory.reactivemongo._
import com.wbillingsley.handy.appbase.{WithHeaderInfo, DataAction}
import com.wbillingsley.handy._
import Ref._
import com.assessory.reactivemongo.UserDAO

object UserController extends Controller {
  
  implicit val userToJson = UserToJson
  
  def self = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      request.approval.who,
      headerInfo
    )
  }
  
  /**
   * Creates a user and logs them in
   */  
  def signUp = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      UserModel.signUp(
        oEmail = (request.body \ "email").asOpt[String],
        oPassword = (request.body \ "password").asOpt[String],
        session = ActiveSession(request.sessionKey, ip=request.remoteAddress)
      ),
      headerInfo
    )
  }  
  
  /**
   * Logging a user in involves finding the user (if the password hash matches), and pushing the
   * current session key as an active session
   */
  def logIn = DataAction.returning.oneWH(parse.json) { implicit request =>
    WithHeaderInfo(
      UserModel.logIn(
        oEmail = (request.body \ "email").asOpt[String],
        oPassword = (request.body \ "password").asOpt[String],
        session = ActiveSession(request.sessionKey, ip=request.remoteAddress)
      ),
      headerInfo
    )
  }

  def autologin(userId:String, secret:String) = DataAction.returning.resultWH { implicit request =>
    val loggedIn = for {
      u <- UserModel.secretLogIn(
        ru = LazyId(userId).of[User],
        secret = secret,
        activeSession = ActiveSession(request.sessionKey, request.remoteAddress)
      )
    } yield Redirect(uiBaseUrl)

    WithHeaderInfo(
      loggedIn,
      headerInfo
    )
  }
    
  /**
   * To log a user out, we just have to remove the current session from their active sessions
   */
  def logOut = DataAction.returning.oneWH { implicit request =>
    WithHeaderInfo(
      UserModel.logOut(
        rUser = request.user,
        session = ActiveSession(request.sessionKey, ip=request.remoteAddress)
      ),
      headerInfo
    )
  }
  
  def findMany = DataAction.returning.manyWH(parse.json) { implicit request =>
    WithHeaderInfo(
      UserModel.findMany(
        oIds = (request.body \ "ids").asOpt[Seq[String]]
      ),
      headerInfo
    )
  }

}