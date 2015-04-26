package com.assessory.asyncmongo

import com.wbillingsley.handy.reactivemongo._
import com.assessory.api.{User, PasswordLogin, Identity, ActiveSession}
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Refused, Id, LazyId, Ref, RefNone}
import com.wbillingsley.handy.Ref._
import Id._
import com.wbillingsley.handyplay.UserProvider
import com.assessory.api.course.{Course, Registration}
import play.api.mvc.{RequestHeader, Request}

import CommonFormats._

object UserDAO extends DAO with UserProvider[User] {

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

  type DataT = User

  val clazz = classOf[User]

  val collName = "assessoryUser"

  val db = DBConnector

  val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def unsaved = User(id = allocateId.asId)

  /** Converts PasswordLogin to and from BSON */
  implicit val pwloginFormat = Macros.handler[PasswordLogin]

  /** Converts Identity to and from BSON */
  implicit val identityFormat = Macros.handler[Identity]


  /** Converts ActiveSession to and from BSON */
  implicit val activeSessionFormat = Macros.handler[ActiveSession]

  /** Converts Registration to and from BSON */
  implicit val registrationFormat = Macros.handler[Registration]

  /**
   * Understands how to read Users.
   *
   * We define this explicity rather than generating it because the macros
   * tend to fail when looking up sequences if the sequence is missing rather than empty.
   */
  implicit object bsonReader extends BSONDocumentReader[User] {
    def read(doc:BSONDocument):User = {
      val u = new User(
        id = doc.getAs[Id[User,String]]("_id").get,
        name = doc.getAs[String]("name"),
        nickname = doc.getAs[String]("nickname"),
        avatar = doc.getAs[String]("avatar"),
        pwlogin = doc.getAs[PasswordLogin]("pwlogin").getOrElse(PasswordLogin()),
        identities = doc.getAs[Seq[Identity]]("identities").getOrElse(Seq.empty),
        secret = doc.getAs[String]("secret").getOrElse(""),
        activeSessions = doc.getAs[Seq[ActiveSession]]("activeSessions").getOrElse(Seq.empty),
        created = doc.getAs[Long]("created").getOrElse(System.currentTimeMillis())
      )
      u
    }
  }

  /**
   * Saves the user's details
   */
  def saveDetails(u:User) = updateAndFetch(
    query=BSONDocument(idIs(u.id)),
    update=BSONDocument("$set" -> BSONDocument(
      "name" -> u.name,
      "nickname" -> u.nickname,
      "avatar" -> u.avatar,
      "created" -> u.created
    ))
  )

  /**
   * Save a new user. This should only be used for new users because it overwrites
   * sessions and identities.
   */
  def saveNew(u:User) = saveSafe(
    BSONDocument(
      idIs(u.id),
      "name" -> u.name,
      "nickname" -> u.nickname,
      "avatar" -> u.avatar,
      "pwlogin" -> u.pwlogin,
      "secret" -> u.secret,
      "identities" -> u.identities,
      "activeSessions" -> u.activeSessions,
      "created" -> u.created
    ),
    u
  )

  /**
   * Adds an identity to this user
   */
  def pushIdentity(ru:Ref[User], i:Identity) = {
    for {
      uid <- id(ru)
      u <- updateAndFetch(
        query = BSONDocument("_id" -> uid),
        update = BSONDocument("$push" -> BSONDocument("identities" -> i))
      )
    } yield u
  }

  /** Adds a session to this user. Typically this happens at login. */
  def pushSession(ru:Ref[User], as:ActiveSession) = {
    for {
      uid <- id(ru)
      u <- updateAndFetch(
        query = BSONDocument("_id" -> uid),
        update = BSONDocument("$push" -> BSONDocument("activeSessions" -> as))
      )
    } yield u
  }

  def deleteSession(ru:Ref[User], as:ActiveSession) = {
    for {
      uid <- id(ru)
      u <- updateAndFetch(
        query = BSONDocument("_id" -> uid),
        update = BSONDocument("$pull" -> BSONDocument("activeSessions" -> BSONDocument("key" -> as.key)))
      )
    } yield u
  }

  def bySessionKey(sessionKey:String):Ref[User] = {
    findOne(query=BSONDocument("activeSessions.key" -> sessionKey))
  }

  def byIdentity(service:String, id:String):Ref[User] = {
    findOne(query=BSONDocument("identities.service" -> service, "identities.value" -> id))
  }

  def bySocialIdOrUsername(service:String, optId:Option[String], optUserName:Option[String] = None):Ref[User] = {

    def byId(service:String, oid:Option[String]) = for {
      id <- oid.toRef
      u <- findOne(query=BSONDocument("identities.service" -> service, "identities.value" -> id))
    } yield u

    def byUsername(service:String, oun:Option[String]) = for {
      n <- oun.toRef
      u <- findOne(query=BSONDocument("identities.service" -> service, "identities.username" -> n))
    } yield u

    byId(service, optId) orIfNone byUsername(service, optUserName)
  }

  def byUsername(u:String) = findOne(BSONDocument("pwlogin.username" -> u))

  def byEmail(e:String) = findOne(BSONDocument("pwlogin.email" -> e))

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

  def byCourse(c:Ref[Course]) = findMany {
    for {
      cid <- id(c)
    } yield BSONDocument("registrations.course" -> cid)
  }

  def pushRegistration(ru:Ref[User], r:Registration) = {
    for {
      uid <- id(ru)
      updated <- updateAndFetch(
        query=BSONDocument("_id" -> uid, "registrations.course" -> r.course),
        update=BSONDocument("$addToSet" -> BSONDocument("registrations.$.roles" -> BSONDocument("$each" -> r.roles)))
      ) orIfNone updateAndFetch(
        query=BSONDocument("_id" -> uid),
        update=BSONDocument("$addToSet" -> BSONDocument("registrations" -> r))
      )
    } yield updated
  }

}
