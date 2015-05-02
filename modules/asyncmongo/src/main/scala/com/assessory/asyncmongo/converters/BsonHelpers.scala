package com.assessory.asyncmongo.converters

import com.mongodb.client.model.Filters
import com.wbillingsley.handy.{Ids, Id}
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson._
import org.bson.conversions.Bson
import org.bson.types.ObjectId

import scala.language.implicitConversions
import scala.util.Try

import scala.collection.JavaConverters._

/*
  TODO: merge this into handy
 */
object BsonHelpers {

  implicit def mapToDoc(m:Map[String, BsonValue]):BsonValue = {
    val d = new BsonDocument()
    for { (k, v) <- m } d.append(k,v)
    d
  }

  def bsonDoc(tuples:(String,BsonValue)*) = {
    val d = new BsonDocument()
    for { (k, v) <- tuples } d.append(k,v)
    d
  }


  /*
   * Object converters
   */
  implicit val identityB = IdentityB
  implicit val identityLookupB = IdentityLookupB
  implicit val pwLoginB = PwLoginB
  implicit val activeSessionB = ActiveSessionB
  implicit val userB = UserB
  implicit val courseB = CourseB
  implicit val groupSetB = GroupSetB
  implicit val groupB = GroupB
  implicit val courseRegB = RegistrationB.courseRegB
  implicit val groupRegB = RegistrationB.groupRegB
  implicit val coursePreenrolB = PreenrolmentB.course
  implicit val groupPreenrolB = PreenrolmentB.group
  implicit val targetB = TargetB
  implicit val taskB = TaskB
  implicit val taskBodyB = TaskBodyB
  implicit val taskOutputB = TaskOutputB
  implicit val taskOutputBodyB = TaskOutputBodyB
  implicit val critAllocationB = CritAllocationB

  /*
   * To BSON
   */
  implicit def toBson[T](item:T)(implicit tb:ToBson[T]):BsonValue = tb.toBson(item)

  implicit def toBsonSeq[T](items:Seq[T])(implicit tb:ToBson[T]):BsonValue = {
    val a = items.map(tb.toBson(_))
    new BsonArray(a.asJava)
  }

  implicit def toBsonDoc[T](item:T)(implicit w:BsonDocumentConverter[T]):BsonValue = w.write(item)

  implicit def toBsonDocOpt[T](o:Option[T])(implicit w:BsonDocumentConverter[T]):BsonValue = o match {
    case Some(i) => w.write(i)
    case _ => new BsonNull
  }

  implicit def toBsonDocSeq[T](item:Seq[T])(implicit w:BsonDocumentConverter[T]):BsonValue = {
    val items = item.map(w.write(_))
    new BsonArray(items.asJava)
  }

  implicit def IdToBson[T]:ToBson[Id[T,String]] = new ToBson[Id[T, String]] {
    def toBson(i:Id[T, String]) = new BsonObjectId(new ObjectId(i.id))
  }

  implicit object StringToBson extends ToBson[String] {
    def toBson(s:String) = new BsonString(s)
  }

  implicit object BoolToBson extends ToBson[Boolean] {
    def toBson(s:Boolean) = new BsonBoolean(s)
  }

  implicit object IntToBson extends ToBson[Int] {
    def toBson(s:Int) = new BsonInt32(s)
  }

  implicit def optToBson[T](implicit tb:ToBson[T]):ToBson[Option[T]] = new ToBson[Option[T]] {
    def toBson(o:Option[T]) = o match {
      case Some(s) => tb.toBson(s)
      case _ => new BsonNull
    }
  }

  /*
   * From BSON
   */


  implicit def fromBson[T](b:BsonValue)(implicit f:FromBson[T]):T = f.fromBson(b)

  implicit def optFromBson[T](b:BsonValue)(implicit f:FromBson[T]):Option[T] = {
    Option(b).map(f.fromBson(_))
  }

  implicit def DocToObj[T](implicit r:BsonDocumentConverter[T]):FromBson[T] = {
    new FromBson[T] {
      def fromBson(d:BsonValue) = r.read(d.asDocument).get
    }
  }

  implicit def ArrToSeq[T](implicit f:FromBson[T]):FromBson[Seq[T]] = new FromBson[Seq[T]] {
    def fromBson(a:BsonValue):Seq[T] = {
      a.asArray().toArray.toSeq.asInstanceOf[Seq[BsonValue]].map(f.fromBson(_))
    }
  }

  implicit def BsonToObjectId[T]:FromBson[Id[T, String]] = new FromBson[Id[T, String]] {
    override def fromBson(b: BsonValue): Id[T, String] = Id(b.asObjectId().getValue.toHexString)
  }

  implicit def BsonToObjectIds[T]:ToFromBson[Ids[T, String]] = new ToFromBson[Ids[T, String]] {
    def toBson(i:Ids[T, String]) = {
      val ids = for { id <- i.ids } yield new BsonObjectId(new ObjectId(id))
      new BsonArray(ids.asJava)
    }

    override def fromBson(b: BsonValue): Ids[T, String] = {
      Ids(b.asArray().toArray(Array.empty[BsonObjectId]).map(_.getValue.toHexString).toSeq)
    }
  }


  implicit object BsonToString extends FromBson[String] {
    override def fromBson(b: BsonValue): String = b.asString().getValue
  }

  implicit object BsonToLong extends ToFromBson[Long] {
    def toBson(s:Long) = new BsonInt64(s)
    override def fromBson(b: BsonValue): Long = b.asInt64().getValue
  }

  implicit object BsonToInt extends FromBson[Int] {
    override def fromBson(b: BsonValue): Int = b.asInt32().getValue
  }

  implicit object BsonToBoolean extends FromBson[Boolean] {
    override def fromBson(b: BsonValue): Boolean = b.asBoolean().getValue
  }

  implicit class docOps(val d:BsonDocument) extends AnyVal {
    def getObject[T](k:String)(implicit r:BsonDocumentConverter[T]):T = r.read(d.getDocument(k)).get

    def getObjSeq[T](k:String)(implicit r:BsonDocumentConverter[T]):Seq[T] = {
      r.readSeq(d.getArray(k).toArray(Array.empty[BsonDocument]).toSeq).get
    }

    def opt[T](k:String)(implicit f:FromBson[T]):Option[T] = {
      val v = d.get(k)
      if (v.isNull) {
        None
      } else {
        Some(f.fromBson(v))
      }
    }

    def req[T](k:String)(implicit f:FromBson[T]):T = {
      f.fromBson(d.get(k))
    }

  }


  /*
   * StringKeys
   */

  implicit def idToFromSK[T]:ToFromStringKey[Id[T,String]] = new ToFromStringKey[Id[T,String]] {
    override def toSK(i: Id[T,String]): String = i.id

    override def fromSK(i: String): Id[T, String] = Id(i)
  }

  /*
   * DSL
   */

  def $set(tuples:(String,BsonValue)*) = bsonDoc("$set" -> bsonDoc(tuples:_*))

  def $pull(tuples:(String,BsonValue)*) = bsonDoc("$pull" -> bsonDoc(tuples:_*))

  def $push(tuples:(String,BsonValue)*) = bsonDoc("$push" -> bsonDoc(tuples:_*))

  def $addToSet(tuples:(String,BsonValue)*) = bsonDoc("$addToSet" -> bsonDoc(tuples:_*))

  implicit class keyOps(val s:String) extends AnyVal {
    def $eq(bson:BsonValue) = Filters.eq(s, bson)
  }

  implicit class bsonOps(val b:Bson) extends AnyVal {
    def and(bson:Bson) = Filters.and(b, bson)

    def or(bson:Bson) = Filters.or(b, bson)
  }

  def and(b:Bson*) = Filters.and(b:_*)

  def or(b:Bson*) = Filters.and(b:_*)

  val $null = new BsonNull

}

trait ToBson[T] {
  def toBson(i:T):BsonValue
}

trait FromBson[T] {
  def fromBson(b:BsonValue):T

  def tryFromBson(b:BsonValue) = Try { fromBson(b) }
}

trait ToFromBson[T] extends ToBson[T] with FromBson[T]

trait ToStringKey[K] {
  def toSK(i:K):String
}

trait FromStringKey[K] {
  def fromSK(i:String):K
}

trait ToFromStringKey[K] extends ToStringKey[K] with FromStringKey[K]
