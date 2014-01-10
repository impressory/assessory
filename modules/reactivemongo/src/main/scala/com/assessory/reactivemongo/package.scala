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
  
  def RefReader[T <: HasStringId](c:Class[T])(implicit l:LookUp[T, String]) = new BSONReader[BSONObjectID, Ref[T]] {
    def read(id:BSONObjectID) = new LazyId(c, id.stringify)
  }

  def RefManyByIdReader[T <: HasStringId](c:Class[T])(implicit l:LookUp[T, String]) = new BSONReader[BSONArray, RefManyById[T, String]] {
    def read(ids:BSONArray) = {
      val s = for (v <- ids.values) yield {
        v.asInstanceOf[BSONObjectID].stringify
      }
      new RefManyById(c, s.toSeq)
    }
  }
  
  implicit val lookupUser = UserDAO.LookUp
  implicit val lookupGroup = GroupDAO.LookUp
  implicit val lookupGroupSet = GroupSetDAO.LookUp
  implicit val lookupGPreenrol = GPreenrolDAO.LookUp
  implicit val lookupCourse = CourseDAO.LookUp
  implicit val lookupPreenrol = PreenrolDAO.LookUp
  implicit val lookupTask = TaskDAO.LookUp
  implicit val lookupTaskOutput = TaskOutputDAO.LookUp

  implicit val lookupGroupCritAllocation = GroupCritAllocationDAO.LookUp
  
  implicit val RefCourseReader = RefReader(classOf[Course])(CourseDAO.LookUp)
  implicit val RefUserReader = RefReader(classOf[User])(UserDAO.LookUp)
  implicit val RefGroupReader = RefReader(classOf[Group])(GroupDAO.LookUp)
  implicit val RefGroupSetReader = RefReader(classOf[GroupSet])(GroupSetDAO.LookUp)
  implicit val RefGPreenrolReader = RefReader(classOf[GPreenrol])(GPreenrolDAO.LookUp)
  implicit val RefTaskReader = RefReader(classOf[Task])(TaskDAO.LookUp)
  implicit val RefTaskOutputReader = RefReader(classOf[TaskOutput])(TaskOutputDAO.LookUp)
  implicit val RefManyUserReader = RefManyByIdReader(classOf[User])(UserDAO.LookUp)
  implicit val RefManyGroupReader = RefManyByIdReader(classOf[Group])(GroupDAO.LookUp)
  
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