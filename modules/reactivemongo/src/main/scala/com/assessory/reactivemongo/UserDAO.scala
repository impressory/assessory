package com.assessory.reactivemongo

import com.wbillingsley.handy.reactivemongo._
import com.assessory.api.{User, PasswordLogin, Identity, ActiveSession}
import reactivemongo.api._
import reactivemongo.bson._
import com.wbillingsley.handy.{Ref, RefNone}
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider
import com.assessory.api.course.Registration

object UserDAO extends DAO with UserProvider[User] {
  
  type DataT = User
  
  val clazz = classOf[User]
  
  val collName = "assessoryUser"
    
  val db = DBConnector
  
  def unsaved = User(id = allocateId)

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
        id = doc.getAs[BSONObjectID]("_id").get.stringify,    
        name = doc.getAs[String]("name"),
        nickname = doc.getAs[String]("nickname"),
        avatar = doc.getAs[String]("avatar"),
        pwlogin = doc.getAs[PasswordLogin]("pwlogin").getOrElse(PasswordLogin()),
        identities = doc.getAs[Seq[Identity]]("identities").getOrElse(Seq.empty),
        activeSessions = doc.getAs[Seq[ActiveSession]]("activeSessions").getOrElse(Seq.empty),
        registrations = doc.getAs[Seq[Registration]]("registrations").getOrElse(Seq.empty),
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
      "identities" -> u.identities,
      "activeSessions" -> u.activeSessions,
      "registrations" -> u.registrations,
      "created" -> u.created
    ),
    u
  )  
  
  /**
   * Adds an identity to this user
   */
  def pushIdentity(ru:Ref[User], i:Identity) = {
    updateAndFetch(
        query = BSONDocument("_id" -> ru), 
        update = BSONDocument("$push" -> BSONDocument("identities" -> i)) 
    )
  }  
  
  /** Adds a session to this user. Typically this happens at login. */
  def pushSession(ru:Ref[User], as:ActiveSession) = updateAndFetch(
    query = BSONDocument("_id" -> ru), 
    update = BSONDocument("$push" -> BSONDocument("activeSessions" -> as))
  )
  
  def deleteSession(ru:Ref[User], as:ActiveSession) = updateAndFetch(
    query = BSONDocument("_id" -> ru), 
    update = BSONDocument("$pull" -> BSONDocument("activeSessions" -> BSONDocument("key" -> as.key))) 
  )
  
  def bySessionKey(sessionKey:String):Ref[User] = {
    findOne(query=BSONDocument("activeSessions.key" -> sessionKey))
  }
  
  def byIdentity(service:String, id:String):Ref[User] = {
    findOne(query=BSONDocument("identities.service" -> service, "identities.value" -> id))
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
      user <- byEmail(email) if {
       val hash = user.pwlogin.hash(password)
       hash == user.pwlogin.pwhash
      }      
    ) yield user
  }
  
  def pushRegistration(ru:Ref[User], r:Registration) = {
    for (
      updated <- updateAndFetch(
        query=BSONDocument("_id" -> ru, "registrations.course" -> r.course),
        update=BSONDocument("$addToSet" -> BSONDocument("registrations.$.roles" -> BSONDocument("$each" -> r.roles)))
      ) orIfNone updateAndFetch(
        query=BSONDocument("_id" -> ru),
        update=BSONDocument("$addToSet" -> BSONDocument("registrations" -> r))
      )
    ) yield updated
  }
  
}