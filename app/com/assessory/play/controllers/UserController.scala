package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json.UserToJson
import play.api.mvc.AnyContent

import com.assessory.api._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.DataAction

object UserController extends Controller {
  
  implicit val userToJson = UserToJson
  
  // TODO: Fix this. It's an ugly hack around making the implicits work
  val dataAction = new DataAction
  
  def self = dataAction.one { implicit request =>
    request.approval.who 
  }
  
  /**
   * Creates a user and logs them in
   */  
  def signUp = dataAction.one(parse.json) { implicit request =>     
    for (
      email <- Ref((request.body \ "email").asOpt[String]) orIfNone UserError("Email must not be blank");
      password <- Ref((request.body \ "password").asOpt[String]) orIfNone UserError("Password must not be blank");
      user <- {
        val u = UserDAO.unsaved  
        val set = u.copy(
            pwlogin=u.pwlogin.copy(email=Some(email), pwhash=u.pwlogin.hash(password)),
            activeSessions=Seq(ActiveSession(request.sessionKey, ip=request.remoteAddress))
        )
        UserDAO.saveNew(set)
      }      
    ) yield user     
  }  
  
  /**
   * Logging a user in involves finding the user (if the password hash matches), and pushing the
   * current session key as an active session
   */
  def logIn = dataAction.one(parse.json) { implicit request =>     
    for (
      email <- Ref((request.body \ "email").asOpt[String]) orIfNone UserError("Email must not be blank");
      password <- Ref((request.body \ "password").asOpt[String]) orIfNone UserError("Password must not be blank");
      user <- UserDAO.byEmailAndPassword(email, password);
      updated <- UserDAO.pushSession(user.itself, ActiveSession(request.sessionKey, ip=request.remoteAddress))
    ) yield updated     
  }
    
  /**
   * To log a user out, we just have to remove the current session from their active sessions
   */
  def logOut = dataAction.one { implicit request =>     
    for (      
      u <- request.user;  
      user <- UserDAO.deleteSession(u.itself, ActiveSession(request.sessionKey, ip=request.remoteAddress))
    ) yield {
      user     
    }
  }
  
  def findMany = DataAction.returning.many(parse.json) { implicit request => 
    for (
      ids <- Ref((request.body \ "ids").asOpt[Seq[String]]) orIfNone UserError("No ids requested");
      u <- new RefManyById(classOf[User], ids)
    ) yield u
  }

}