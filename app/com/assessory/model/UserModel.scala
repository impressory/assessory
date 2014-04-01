package com.assessory.model

import com.assessory.reactivemongo._

import com.assessory.api._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._

object UserModel {

  /**
   * Creates a user and logs them in
   */
  def signUp(oEmail:Option[String], oPassword:Option[String], session:ActiveSession) = {
    for (
      email <- Ref(oEmail) orIfNone UserError("Email must not be blank");
      password <- Ref(oPassword) orIfNone UserError("Password must not be blank");
      user <- {
        val u = UserDAO.unsaved
        val set = u.copy(
          pwlogin=u.pwlogin.copy(email=Some(email), pwhash=u.pwlogin.hash(password)),
          activeSessions=Seq(session)
        )
        UserDAO.saveNew(set)
      }
    ) yield user
  }

  /**
   * Logging a user in involves finding the user (if the password hash matches), and pushing the
   * current session key as an active session
   */
  def logIn(oEmail:Option[String], oPassword:Option[String], session:ActiveSession) = {
    for (
      email <- Ref(oEmail) orIfNone UserError("Email must not be blank");
      password <- Ref(oPassword) orIfNone UserError("Password must not be blank");
      user <- UserDAO.byEmailAndPassword(email, password);
      updated <- UserDAO.pushSession(user.itself, session)
    ) yield updated
  }

  /**
   * Logs a user in using their system-set secret
   */
  def secretLogIn(ru:Ref[User], secret:String, activeSession:ActiveSession) = {
    for {
      oldUser <- optionally(UserDAO.deleteSession(ru, activeSession))
      u <- ru if (u.secret == secret)
      pushed <- UserDAO.pushSession(u.itself, activeSession)
    } yield pushed
  }

  /**
   * To log a user out, we just have to remove the current session from their active sessions
   */
  def logOut(rUser:Ref[User], session:ActiveSession) = {
    for (
      u <- rUser;
      user <- UserDAO.deleteSession(u.itself, session)
    ) yield {
      user
    }
  }

  def findMany(oIds:Option[Seq[String]]) = {
    for (
      ids <- Ref(oIds) orIfNone UserError("No ids requested");
      u <- RefManyById(ids).of[User]
    ) yield u
  }

}