package com.assessory.asyncmongo.converters

import com.mongodb.client.model.Filters
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import org.bson._
import org.bson.conversions.Bson
import org.bson.types.ObjectId

import scala.language.implicitConversions

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
  implicit val pwLoginB = PwLoginB
  implicit val activeSessionB = ActiveSessionB
  implicit val userB = UserB


  /*
   * To BSON
   */
  implicit def toBson[T](item:T)(implicit tb:ToBson[T]):BsonValue = tb.toBson(item)

  implicit def toBsonDoc[T](item:T)(implicit w:com.wbillingsley.handy.mongodbasync.BsonDocumentConverter[T]):BsonValue = w.write(item)

  implicit def toBsonSeq[T](item:Seq[T])(implicit w:com.wbillingsley.handy.mongodbasync.BsonDocumentConverter[T]):BsonValue = {
    import scala.collection.JavaConverters._
    val items = item.map(w.write(_))
    new BsonArray(items.asJava)
  }

  implicit def IdToBson[T]:ToBson[Id[T,String]] = new ToBson[Id[T, String]] {
    def toBson(i:Id[T, String]) = new BsonObjectId(new ObjectId(i.id))
  }

  implicit object StringToBson extends ToBson[String] {
    def toBson(s:String) = new BsonString(s)
  }

  implicit object LongToBson extends ToBson[Long] {
    def toBson(s:Long) = new BsonInt64(s)
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



  implicit def fromBson[B <: BsonValue, T](b:B)(implicit f:FromBson[B, T]):T = f.fromBson(b)

  implicit def optFromBson[B <: BsonValue, T](b:B)(implicit f:FromBson[B, T]):Option[T] = {
    Option(b).map(f.fromBson(_))
  }

  implicit def DocToObj[T](implicit r:BsonDocumentConverter[T]):FromBson[BsonDocument, T] = {
    new FromBson[BsonDocument, T] {
      def fromBson(d:BsonDocument) = r.read(d).get
    }
  }

  implicit def ArrToSeq[B <: BsonValue, T](implicit f:FromBson[B, T]) = new FromBson[BsonArray, Seq[T]] {
    def fromBson(a:BsonArray):Seq[T] = {
      a.toArray.toSeq.asInstanceOf[Seq[B]].map(f.fromBson(_))
    }
  }

  implicit def BsonToObjectId[T] = new FromBson[BsonObjectId, Id[T, String]] {
    override def fromBson(b: BsonObjectId): Id[T, String] = Id(b.getValue.toHexString)
  }

  implicit object BsonToString extends FromBson[BsonString, String] {
    override def fromBson(b: BsonString): String = b.getValue
  }

  implicit object BsonToLong extends FromBson[BsonInt64, Long] {
    override def fromBson(b: BsonInt64): Long = b.getValue
  }

  implicit class docOps(val d:BsonDocument) extends AnyVal {
    def getObject[T](k:String)(implicit r:BsonDocumentConverter[T]):T = r.read(d.getDocument(k)).get

    def getObjSeq[T](k:String)(implicit r:BsonDocumentConverter[T]):Seq[T] = {
      r.readSeq(d.getArray(k).toArray(Array.empty[BsonDocument]).toSeq).get
    }

    def opt[T](k:String)(implicit f:FromBson[BsonValue, T]):Option[T] = {
      val v = d.get(k)
      if (v.isNull) {
        None
      } else {
        Some(f.fromBson(v))
      }
    }

    def req[T](k:String)(implicit f:FromBson[BsonValue, T]):T = {
      f.fromBson(d.get(k))
    }

  }


  /*
   * DSL
   */

  def $set(tuples:(String,BsonValue)*) = bsonDoc("$set" -> bsonDoc(tuples:_*))

  def $pull(tuples:(String,BsonValue)*) = bsonDoc("$pull" -> bsonDoc(tuples:_*))

  def $push(tuples:(String,BsonValue)*) = bsonDoc("$push" -> bsonDoc(tuples:_*))

  implicit class keyOps(val s:String) extends AnyVal {
    def $eq(bson:BsonValue) = Filters.eq(s, bson)
  }

  implicit class bsonOps(val b:Bson) extends AnyVal {
    def and(bson:Bson) = Filters.and(b, bson)
  }



}

trait ToBson[T] {
  def toBson(i:T):BsonValue
}

trait FromBson[B <: BsonValue, T] {
  def fromBson(b:B):T
}
