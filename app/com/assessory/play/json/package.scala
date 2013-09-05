package com.assessory.play

import com.wbillingsley.handy.{Ref, HasStringId, LazyId}
import com.assessory.api._
import course._
import group._
import play.api.libs.json.{Json, JsValue, Writes, Reads}

package object json {

  implicit def refToJson[T <: HasStringId](ref:Ref[T]) = Json.toJson(ref.getId)
  
  implicit object writesRef extends Writes[Ref[HasStringId]] {
    def writes(r:Ref[HasStringId]) = Json.toJson(r.getId)
  }
  
  def readsRef[T <: HasStringId](c:Class[T]) = new Reads[Ref[T]] {
    def reads(j:JsValue) = Reads.StringReads.reads(j).map(s => new LazyId(c, s))
  }
  
  implicit val readsRefUser = readsRef(classOf[User])
  implicit val readsRefGroupSet = readsRef(classOf[GroupSet])
  implicit val readsRefCourse = readsRef(classOf[Course])
  implicit val readsRefGPreenrol = readsRef(classOf[GPreenrol])
}