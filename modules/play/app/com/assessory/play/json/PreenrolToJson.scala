package com.assessory.play.json

import com.wbillingsley.handyplay.JsonConverter
import com.assessory.api._
import com.assessory.reactivemongo._
import course._
import group._
import com.wbillingsley.handy._
import Ref._
import play.api.libs.json.{Writes, Json, JsValue}

import com.assessory.api.wiring.Lookups._

object PreenrolToJson extends JsonConverter[Preenrol, User] {
  
  implicit val ppFormat = Json.writes[IdentityLookup]
  implicit val peFormat = new Writes[Preenrol] {
    def writes(p:Preenrol) = Json.obj(
      "id" -> p.id,
      "name" -> p.name,
      "roles" -> p.roles,
      "course" -> p.course,
      "identities" -> p.identities,
      "created" -> p.created
    )
  }
  
  def toJsonFor(preenrol:Preenrol, a:Approval[User]) = {
    
    val permissions = for (
      course <- a.cache(preenrol.course);
      view <- optionally(a ask Permissions.ViewCourse(course.itself));
      edit <- optionally(a ask Permissions.EditCourse(course.itself))
    ) yield Json.obj(
      "view" -> view.isDefined,
      "edit" -> edit.isDefined
    )
    
    for (p <- permissions) yield peFormat.writes(preenrol) ++ Json.obj("permissions" -> p)
  }
  
  def toJson(p:Preenrol) = peFormat.writes(p).itself
  
  /**
   * Produces an update Course object
   */
  def update(p:Preenrol, json:JsValue) = {
    p.copy(
        name = (json \ "name").asOpt[String]
    )
  }
  
  /**
   * Produces an update Course object
   */
  def updateWithCsv(p:Preenrol, json:JsValue) = {
    
    Preenrol.fromCsv(
      id = p.id.id,
      name = (json \ "name").asOpt[String], 
      roles = (json \ "roles").asOpt[Seq[String]].getOrElse(Seq.empty).toSet,
      course = p.course, 
      csv = (json \ "csv").asOpt[String].get
    )
  }   

}