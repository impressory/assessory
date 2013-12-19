package com.assessory

import _root_.reactivemongo.api._
import _root_.reactivemongo.bson._
import com.wbillingsley.handy.reactivemongo._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._
import group._
import groupcrit._

package object reactivemongo {
  
  def RefReader[T <: HasStringId](c:Class[T]) = new BSONReader[BSONObjectID, Ref[T]] {
    def read(id:BSONObjectID) = new LazyId(c, id.stringify)
  }

   def RefManyByIdReader[T <: HasStringId](c:Class[T]) = new BSONReader[BSONArray, RefManyById[T, String]] {
    def read(ids:BSONArray) = {
      val s = for (v <- ids.values) yield {
        v.asInstanceOf[BSONObjectID].stringify
      }
      new RefManyById(c, s.toSeq)
    }
  }
  
   
  implicit val RefCourseReader = RefReader(classOf[Course])
  implicit val RefUserReader = RefReader(classOf[User])
  implicit val RefGroupReader = RefReader(classOf[Group])
  implicit val RefGroupSetReader = RefReader(classOf[GroupSet])
  implicit val RefGPreenrolReader = RefReader(classOf[GPreenrol])
  implicit val RefTaskReader = RefReader(classOf[Task])
  implicit val RefTaskOutputReader = RefReader(classOf[TaskOutput])
  implicit val RefManyUserReader = RefManyByIdReader(classOf[User])
  implicit val RefManyGroupReader = RefManyByIdReader(classOf[Group])
  
  implicit object identityLookupFormat extends BSONHandler[BSONDocument, IdentityLookup] {
    def read(doc:BSONDocument) = {
      IdentityLookup(
        service=doc.getAs[String]("service").get,
        value=doc.getAs[String]("value"),
        username=doc.getAs[String]("username"),
        used=doc.getAs[Boolean]("used").getOrElse(false)
      )
    }
    
    def write(i:IdentityLookup) = BSONDocument(
      "service"->i.service, "value" -> i.value, "username" -> i.username, "used" -> i.used
    )
  }
  

}