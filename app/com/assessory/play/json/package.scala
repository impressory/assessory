package com.assessory.play

import com.wbillingsley.handy._
import com.assessory.api._
import course._
import group._
import play.api.libs.json.{Json, JsValue, Writes, Reads, Format}

// Imports the lookups
import com.assessory.reactivemongo._

package object json {

  import scala.language.implicitConversions;
  implicit def refToJson[T <: HasStringId](ref:RefWithId[T]) = Json.toJson(ref.getId)
  

  implicit def writesRefMany[T <: HasStringId, K] = new Writes[RefManyById[T, K]] {
    def writes(r:RefManyById[T, K]) = Json.toJson(r.getIds)
  }

  implicit def formatRef[T <: HasStringId](implicit lu:LookUp[T, String]) = new Format[RefWithId[T]] {
    def writes(r:RefWithId[T]) = Json.toJson(r.getId)
    def reads(j:JsValue) = Reads.StringReads.reads(j).map(s => LazyId(s).of(lu))
  }

  implicit def writesRef[T <: HasStringId] = new Writes[RefWithId[T]] {
    def writes(r:RefWithId[T]) = Json.toJson(r.getId)
  }

  implicit def readsRef[T <: HasStringId](implicit lu:LookUp[T, String]) = new Reads[RefWithId[T]] {
    def reads(j:JsValue) = Reads.StringReads.reads(j).map(s => LazyId(s).of(lu))
  }
  
}