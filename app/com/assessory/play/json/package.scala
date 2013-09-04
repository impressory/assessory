package com.assessory.play

import com.wbillingsley.handy.{Ref, HasStringId}
import com.assessory.api._
import play.api.libs.json.{Json, Writes}

package object json {

  implicit def refToJson[T <: HasStringId](ref:Ref[T]) = Json.toJson(ref.getId)
  
  implicit object writesRef extends Writes[Ref[HasStringId]] {
    def writes(r:Ref[HasStringId]) = Json.toJson(r.getId)
  }
  
}