package com.assessory

import _root_.reactivemongo.api._
import _root_.reactivemongo.bson._
import _root_.reactivemongo.bson.BSONString
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.UserProvider

import com.assessory.api._
import course._
import group._
import critique._
import com.assessory.api.IdentityLookup
import scala.Some

package object reactivemongo {

  def r[T](d:BSONDocument, n:String)(implicit lu:LookUpOne[T, String]):RefWithId[T] = {
    d.getAs[BSONObjectID](n) match {
      case Some(id) => LazyId(id.stringify).of(lu)
      case _ => RefNone
    }
  }

  implicit def refReader[T <: HasStringId](implicit lu:LookUp[T, String]) = new BSONReader[BSONObjectID, Ref[T]] {
    def read(id:BSONObjectID) = LazyId(id.stringify).of(lu)
  }

  implicit def refWithStringIdReader[T <: HasStringId](implicit lu:LookUp[T, String]) = new BSONReader[BSONObjectID, RefWithId[T]] {
    def read(id:BSONObjectID) = LazyId(id.stringify).of(lu)
  }

  implicit def refManyReader[T <: HasStringId](implicit lu:LookUp[T, String]) = new BSONReader[BSONArray, RefManyById[T, String]] {
    def read(ids:BSONArray) = {
      val arr = ids.as[Seq[BSONObjectID]].map(_.stringify)
      RefManyById(arr).of(lu)
    }
  }

  implicit class DocRefGetter(val doc: BSONDocument) extends AnyVal {
    def getRef[T](key:String)(implicit lu:LookUp[T, String]) = {
      val o:Option[BSONObjectID] = doc.getAs[BSONObjectID](key)
      o match {
        case Some(id) => LazyId(id.stringify).of(lu)
        case None => RefNone
      }
    }

    def getRefMany[T](key:String)(implicit lu:LookUp[T, String]):RefManyById[T, String] = {
      val arr = doc.getAs[Seq[BSONObjectID]](key).getOrElse(Seq.empty)
      val strings = arr.map(_.stringify)
      RefManyById(strings).of(lu)
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

  implicit val lookupCritAllocation = CritAllocationDAO.LookUp

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

  /**
   * Our domain objects use String IDs, but we want them converted to BSONObjectIDs
   */
  def id[T <: HasStringId](r:Ref[T]) = for (id <- r.refId) yield new BSONObjectID(id)

  implicit def RefWithStringIdWriter[T <: HasStringId] = new BSONWriter[RefWithId[T], BSONValue] {
    def write(r:RefWithId[T]) = {
      r.getId.map(new BSONObjectID(_)).getOrElse(BSONNull)
    }
  }
  

}