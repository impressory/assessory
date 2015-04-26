package com.assessory.asyncmongo

import com.assessory.api._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.{Id, LazyId, Ref, Refused}
import Id._
import com.wbillingsley.handyplay.UserProvider
import com.wbillingsley.handy.mongodbasync.DAO
import play.api.mvc.RequestHeader

import converters.BsonHelpers._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

object UserDAO extends DAO(DB, classOf[User], "assessoryUser") with UserProvider[User] {

  override def user(r:RequestHeader):Ref[User] = {
    r.headers.get("Authorization") match {
      case Some(auth) if (auth.trim.startsWith("Bearer")) => {
        val trimmed = auth.trim.drop(6).trim
        val Array(userId, secret) = trimmed.split(" ")
        (for {
          u <- LazyId(userId).of(LookUp) if (u.secret == secret)
        } yield u) orIfNone Refused("Incorrect ID or secret")
      }
      case _ => super.user(r)
    }
  }


  def unsaved = User(id = allocateId.asId)

  /**
   * Saves the user's details
   */
  def saveDetails(u:User) = updateAndFetch(
    query=idIs(u.id),
    update=$set(
      "name" -> u.name,
      "nickname" -> u.nickname,
      "avatar" -> u.avatar,
      "created" -> u.created
    )
  )

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(u:User) = saveSafe(u)

  /**
   * Adds an identity to this user
   */
  def pushIdentity(ru:Ref[User], i:Identity) = {
    for {
      uid <- ru.refId
      u <- updateAndFetch(
        query = ("_id" $eq uid),
        update = $push("identities" -> toBsonDoc(i))
      )
    } yield u
  }

  /** Adds a session to this user. Typically this happens at login. */
  def pushSession(ru:Ref[User], as:ActiveSession) = {
    for {
      uid <- ru.refId
      u <- updateAndFetch(
        query = ("_id" $eq uid),
        update = $push("activeSessions" -> toBsonDoc(as))
      )
    } yield u
  }

  def deleteSession(ru:Ref[User], as:ActiveSession) = {
    for {
      uid <- ru.refId
      u <- updateAndFetch(
        query = "_id" $eq uid,
        update = $pull("activeSessions" -> bsonDoc("key" -> as.key))
      )
    } yield u
  }

  def bySessionKey(sessionKey:String):Ref[User] = {
    findOne(query="activeSessions.key" $eq sessionKey)
  }

  def byIdentity(service:String, id:String):Ref[User] = {
    findOne(query=("identities.service" $eq service) and ("identities.value" $eq id))
  }

  def bySocialIdOrUsername(service:String, optId:Option[String], optUserName:Option[String] = None):Ref[User] = {

    def byId(service:String, oid:Option[String]) = for {
      id <- oid.toRef
      u <- findOne(query=("identities.service" $eq service) and ("identities.value" $eq id))
    } yield u

    def byUsername(service:String, oun:Option[String]) = for {
      n <- oun.toRef
      u <- findOne(query=("identities.service" $eq service) and ("identities.username" $eq n))
    } yield u

    byId(service, optId) orIfNone byUsername(service, optUserName)
  }

  def byUsername(u:String) = findOne("pwlogin.username" $eq u)

  def byEmail(e:String) = findOne("pwlogin.email" $eq e)

  def byUsernameAndPassword(username:String, password:String) = {
    for (
      user <- byUsername(username) if {
       val hash = user.pwlogin.hash(password)
       hash == user.pwlogin.pwhash
      }
    ) yield user
  }

  def byEmailAndPassword(email:String, password:String) = {
    for (
      user <- byEmail(email) if user.pwlogin.checkPassword(password)
    ) yield user
  }

  def byCourse(c:Ref[Course]) = {
    c.refId map ("registrations.course" $eq _) flatMap findMany
  }


}
