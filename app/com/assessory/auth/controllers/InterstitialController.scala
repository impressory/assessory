package com.assessory.auth.controllers

import play.api.mvc.{Controller, Action, Request}
import play.api.libs.ws.WS
import play.api.libs.json.Json
import play.api.Play
import Play.current
import com.wbillingsley.encrypt.Encrypt
import com.wbillingsley.handy._
import com.wbillingsley.handy.playoauth.UserRecord
import Ref._
import play.api.mvc.AnyContent
import com.assessory.api._
import com.wbillingsley.handy.appbase.DataAction

/**
 * Controller for the interstitial form confirming that a new account should be registered
 */
object InterstitialController extends Controller {
  
  val sessionVar = "interstitialMemory"
    
  implicit val UserDAO = com.assessory.reactivemongo.UserDAO
  
  val dataAction = new DataAction
    
  /**
   * Handles the completion of OAuth authorisations
   */
  def onOAuth(rur:Ref[UserRecord]) = dataAction.result { implicit request =>
    
    val res = for (
      mem <- rur;
      user <- optionally(UserDAO.byIdentity("github", mem.id))
    ) yield {
      
      user match {
        case Some(u) => {
          for (
            updated <- UserDAO.pushSession(u.itself, ActiveSession(request.sessionKey, ip = request.remoteAddress))
          ) yield Redirect(com.assessory.play.controllers.routes.Application.index)
        } 
        case None => {          
          val session = request.session + (InterstitialController.sessionVar -> mem.toJsonString) - "oauth_state"
          for (u <- optionally(request.user)) yield {
		      u match {
		        case Some(user) => Ok(views.html.interstitials.addOAuth(Some(mem.service), mem, user)).withSession(session)
		        case None => Ok(views.html.interstitials.registerOAuth(Some(mem.service), mem)).withSession(session)
		      }            
          }
        }
      }
    }
    
    val flat = res.flatten    
    flat
  }
    
     
  /**
   * Register the user as a new user on this system
   */
  def registerUser = dataAction.result { implicit request => 
    val memString = request.session.get(sessionVar)
    val mem = for (s <- memString; m <- Json.parse(s).asOpt[UserRecord]) yield m
    
    val resp = for (
      details <- Ref(mem) orIfNone Refused("There appear to be no user details to register");
      user <- {
        val i = Identity(service=details.service, value=details.id, avatar=details.avatar, username=details.username);
        val u = UserDAO.unsaved.copy(
            name=details.name, nickname=details.nickname, avatar=details.avatar,
            identities = Seq(i),
            activeSessions = Seq(ActiveSession(request.sessionKey, ip = request.remoteAddress))
        )
        UserDAO.saveNew(u)
      }
    ) yield {      
      val session = request.session - InterstitialController.sessionVar
      Redirect(com.assessory.play.controllers.routes.Application.index).withSession(session)      
    }
    
    resp
  }
  
  /**
   * Adds the remembered identity to the currently logged in user.
   */
  def addIdentity = dataAction.result { implicit request => 
    val memString = request.session.get(sessionVar)
    val mem = for (s <- memString; m <- Json.parse(s).asOpt[UserRecord]) yield m
    
    val resp = for (
      details <- Ref(mem) orIfNone Refused("There appear to be no user details to register");
      user <- request.user orIfNone Refused("There is no logged in user to add that identity to");
      saved <- {
        val u = user.copy(
            name=user.name orElse details.name, nickname=user.nickname orElse details.nickname, avatar=user.avatar orElse details.avatar
        )
        UserDAO.saveDetails(u)
      };
      pushed <- {
        val i = Identity(service=details.service, value=details.id, avatar=details.avatar, username=details.username);
        UserDAO.pushIdentity(user.itself, i)
      }
    ) yield {
      val session = request.session - InterstitialController.sessionVar
      Redirect(com.assessory.play.controllers.routes.Application.index).withSession(session)      
    }
    
    resp
  }  
}