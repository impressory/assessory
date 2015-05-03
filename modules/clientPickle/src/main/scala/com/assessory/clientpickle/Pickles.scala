package com.assessory.clientpickle

import com.assessory.api.critique.{CritTargetStrategy, CritiqueTask, OfMyGroupsStrategy}
import com.assessory.api.{EmptyTaskBody, TaskBody}
import com.wbillingsley.handy.{Ids, Id}
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.Ids._
import com.wbillingsley.handy.appbase._
import upickle.Js

object Pickles {

  implicit def idWriter[T] = upickle.Writer[Id[T, String]] { case id => Js.Str(id.id) }
  implicit def idReader[T] = upickle.Reader[Id[T, String]] { case Js.Str(s) => s.asId[T] }

  implicit def idsWriter[T] = upickle.Writer[Ids[T, String]] { case id =>
    val jsIds = id.ids.map(Js.Str)
    Js.Arr(jsIds:_*)
  }
  implicit def idsReader[T] = upickle.Reader[Ids[T, String]] { case a:Js.Arr =>
    (for { v <- a.value } yield v.value.asInstanceOf[String]).asIds[T]
  }


  case class Kinded[T](kind:String, item:T)

  implicit val questionWriter = upickle.Writer[Question] {
    case st:ShortTextQuestion => upickle.json.read(upickle.write(Kinded(st.kind, st)))
    case q:BooleanQuestion => upickle.json.read(upickle.write(Kinded(q.kind, q)))
  }
  implicit val questionReader = upickle.Reader[Question] { case o:Js.Obj => o("kind") match {
    case Js.Str(ShortTextQuestion.kind) => upickle.read[Kinded[ShortTextQuestion]](upickle.json.write(o)).item
    case Js.Str(BooleanQuestion.kind) => upickle.read[Kinded[BooleanQuestion]](upickle.json.write(o)).item
  }}

  implicit val critTargetStrategyReader = upickle.Reader[CritTargetStrategy] { case o:Js.Obj => o("kind") match {
    case Js.Str(OfMyGroupsStrategy.kind) => OfMyGroupsStrategy
  }}
  implicit val critTargetStrategyWriter = upickle.Writer[CritTargetStrategy] {
    case OfMyGroupsStrategy => upickle.json.read(upickle.write(Kinded(OfMyGroupsStrategy.kind, OfMyGroupsStrategy)))
  }


  implicit val taskBodyWriter = upickle.Writer[TaskBody] {
    case ct:CritiqueTask => upickle.json.read(upickle.write(Kinded(ct.kind, ct)))
    case EmptyTaskBody => upickle.json.read(upickle.write(Kinded(EmptyTaskBody.kind, EmptyTaskBody)))
  }
  implicit val taskBodyReader = upickle.Reader[TaskBody] {
    case o:Js.Obj =>
      o("kind") match {
        case Js.Str(CritiqueTask.kind) => upickle.read[Kinded[CritiqueTask]](upickle.json.write(o)).item
        case Js.Str(EmptyTaskBody.kind) => EmptyTaskBody
      }
  }






}
