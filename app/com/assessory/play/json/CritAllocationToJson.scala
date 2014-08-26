package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import group._
import critique._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json._

object CritAllocationToJson extends JsonConverter[CritAllocation, User] {

  import CritiqueJson._
  
  implicit val gpFormat = Json.writes[IdentityLookup]
  implicit val gcacFormat = Json.writes[AllocatedCrit]
  implicit val gcaFormat = new Writes[CritAllocation] {
    def writes(ca:CritAllocation) = Json.obj(
      "id" -> ca.id,
      "task" -> ca.task,
      "user" -> ca.user,
      "allocation" -> ca.allocation
    )
  }
  
  def toJsonFor(gca:CritAllocation, a:Approval[User]) = {
    val perm = for (write <- optionally(a ask Permissions.WriteCritique(gca.itself))) yield Json.obj(
      "critique" -> write.isDefined
    )
    
    for (p <- perm) yield Json.obj("permissions" -> p) ++ gcaFormat.writes(gca)
  }
  
  def toJson(gca:CritAllocation) = gcaFormat.writes(gca).itself
  
  /**
   * Produces an update Course object
   */
  def update(gp:GPreenrol, json:JsValue) = {
    gp.copy(
        // TODO
    )
  }

}