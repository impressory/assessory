package com.assessory.play.json

import com.wbillingsley.handy.appbase.JsonConverter
import com.assessory.api._
import course._
import group._
import groupcrit._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object GroupCritAllocationToJson extends JsonConverter[GroupCritAllocation, User] {
  
  
  implicit val gpFormat = Json.writes[IdentityLookup]
  implicit val gcacFormat = Json.writes[GCAllocatedCrit]
  implicit val gcaFormat = Json.writes[GroupCritAllocation]
  
  def toJsonFor(gca:GroupCritAllocation, a:Approval[User]) = {
    gcaFormat.writes(gca).itself
  }
  
  def toJson(gca:GroupCritAllocation) = gcaFormat.writes(gca).itself
  
  /**
   * Produces an update Course object
   */
  def update(gp:GPreenrol, json:JsValue) = {
    gp.copy(
        // TODO
    )
  }

}