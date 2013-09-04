package com.assessory

import _root_.reactivemongo.api._
import _root_.reactivemongo.bson._
import com.wbillingsley.handy.reactivemongo._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._


package object reactivemongo {

  implicit object RefCourseReader extends BSONReader[BSONObjectID, Ref[Course]] {
    def read(id:BSONObjectID) = new LazyId(classOf[Course], id.stringify)
  }

  implicit object RefUserReader extends BSONReader[BSONObjectID, Ref[User]] {
    def read(id:BSONObjectID) = new LazyId(classOf[User], id.stringify)
  }
  
  
}