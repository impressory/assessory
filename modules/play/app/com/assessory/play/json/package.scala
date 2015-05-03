package com.assessory.play

import com.assessory.api._
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.Ids._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{User, Course, Group, GroupSet}
import play.api.libs.json.{Format, JsValue, Json, Reads, Writes}

// Imports the lookups
import com.assessory.api.wiring.Lookups._


package object json {

  implicit def writesId[T] = new Writes[Id[T, String]] {
    def writes(r:Id[T, String]) = Json.toJson(r.id)
  }

  implicit def readsId[T] = new Reads[Id[T, String]] {
    def reads(j:JsValue) = Reads.StringReads.reads(j).map(s => s.asId[T])
  }

  implicit def readsIds[T] = new Reads[Ids[T, String]] {
    def reads(j:JsValue) = Reads.ArrayReads[String].reads(j).map(s => s.toSeq.asIds[T])
  }

  implicit def writesIds[T] = new Writes[Ids[T, String]] {
    def writes(r:Ids[T, String]) = Json.toJson(r.ids)
  }

  implicit def writesRefMany[T <: HasStringId[T], K] = new Writes[RefManyById[T, K]] {
    def writes(r:RefManyById[T, K]) = {
      val g = implicitly[GetsId[T,String]]
      Json.toJson(
        for {
          id <- r.getIds.ids
          c <- g.canonical(id)
        } yield c.id
      )
    }
  }

  implicit def formatRef[T <: HasStringId[T]](implicit lu:LookUp[T, String]) = new Format[RefWithId[T]] {
    def writes(r:RefWithId[T]) = Json.toJson(r.getId)
    def reads(j:JsValue) = Reads.StringReads.reads(j).map(s => LazyId(s).of(lu))
  }

  implicit val formatRU = formatRef[User]
  implicit val formatRC = formatRef[Course]
  implicit val formatGS = formatRef[GroupSet]
  implicit val formatG = formatRef[Group]
  implicit val formatGP = formatRef[Group.Preenrol]
  implicit val formatT = formatRef[Task]
  implicit val formatTO = formatRef[TaskOutput]

}
