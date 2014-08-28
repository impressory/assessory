package com.assessory.auth.controllers

import play.api.mvc.{Controller, Action, Request}
import play.api.libs.ws.WS
import play.api.libs.json.Json
import play.api.Play
import Play.current
import com.wbillingsley.handy._
import com.wbillingsley.handy.playoauth.{OAuthDetails, UserRecord}
import Ref._
import play.api.mvc.AnyContent
import com.assessory.api._
import com.wbillingsley.handyplay.DataAction
import scala.util.Try

/**
 * Controller for the interstitial form confirming that a new account should be registered
 */
object InterstitialController extends Controller {
  
  implicit val dataActionConfig = com.assessory.config.AssessoryDataActionConfig 
  
  val sessionVar = "interstitialMemory"
    
  implicit val UserDAO = com.assessory.reactivemongo.UserDAO

  /**
   * Handles the completion of OAuth authorisations
   */
  def onOAuth(rur:Try[OAuthDetails]) = DataAction.returning.result { implicit request =>
    
    val res = for (
      mem <- rur.toRef;
      user <- optionally(UserDAO.bySocialIdOrUsername(mem.userRecord.service, Some(mem.userRecord.id), mem.userRecord.username))
    ) yield {
      
      user match {
        case Some(u) => {
          for {
            withSession <- UserDAO.pushSession(u.itself, ActiveSession(request.sessionKey, ip = request.remoteAddress))

            saved <- {
              // Update any missing user details
              val updated = withSession.copy(
                name = u.name orElse mem.userRecord.name,
                nickname = u.nickname orElse mem.userRecord.nickname,
                avatar = u.avatar orElse mem.userRecord.avatar
              )

              if (updated == withSession) {
                println("not changing anything")
                withSession.itself
              } else {
                UserDAO.saveDetails(updated)
              }
            }
          } yield Redirect(com.assessory.play.controllers.routes.Application.index)
        }
        case None => {          
          val session = request.session + (InterstitialController.sessionVar ->  mem.toJson.toString) - "oauth_state"
          for (u <- optionally(request.user)) yield {
		      u match {
		        case Some(user) => Ok(views.html.interstitials.addOAuth(Some(mem.userRecord.service), mem.userRecord, user)).withSession(session)
		        case None => Ok(views.html.interstitials.registerOAuth(Some(mem.userRecord.service), mem.userRecord)).withSession(session)
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
  def registerUser = DataAction.returning.result { implicit request =>
    val memString = request.session.get(sessionVar)
    val mem = for (s <- memString; m <- (Json.parse(s) \ "userRecord").asOpt[UserRecord]) yield m

    val resp = for (
      details <- Ref(mem) orIfNone Refused("There appear to be no user details to register");
      user <- {
        val i = Identity(service=details.service, value=Some(details.id), avatar=details.avatar, username=details.username);
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
  def addIdentity = DataAction.returning.result { implicit request =>
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
        val i = Identity(service=details.service, value=Some(details.id), avatar=details.avatar, username=details.username);
        UserDAO.pushIdentity(user.itself, i)
      }
    ) yield {
      val session = request.session - InterstitialController.sessionVar
      Redirect(com.assessory.play.controllers.routes.Application.index).withSession(session)      
    }
    
    resp
  }  
}