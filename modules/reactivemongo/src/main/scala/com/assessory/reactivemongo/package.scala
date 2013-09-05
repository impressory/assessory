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

package object reactivemongo {

  def RefReader[T <: HasStringId](c:Class[T]) = new BSONReader[BSONObjectID, Ref[T]] {
    def read(id:BSONObjectID) = new LazyId(c, id.stringify)
  }
  
  implicit val RefCourseReader = RefReader(classOf[Course])
  implicit val RefUserReader = RefReader(classOf[User])
  implicit val RefGroupReader = RefReader(classOf[Group])
  implicit val RefGroupSetReader = RefReader(classOf[GroupSet])
  implicit val RefGPreenrolReader = RefReader(classOf[GPreenrol])
  
}