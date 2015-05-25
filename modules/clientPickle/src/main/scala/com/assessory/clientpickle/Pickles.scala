package com.assessory.clientpickle

import com.assessory.api.critique._
import com.assessory.api._
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

  implicit val answerWriter = upickle.Writer[Answer[_]] {
    case st:ShortTextAnswer => upickle.json.read(upickle.write(Kinded(st.kind, st)))
    case q:BooleanAnswer => upickle.json.read(upickle.write(Kinded(q.kind, q)))
  }
  implicit val answerReader = upickle.Reader[Answer[_]] { case o:Js.Obj => o("kind") match {
    case Js.Str(ShortTextQuestion.kind) => upickle.read[Kinded[ShortTextAnswer]](upickle.json.write(o)).item
    case Js.Str(BooleanQuestion.kind) => upickle.read[Kinded[BooleanAnswer]](upickle.json.write(o)).item
  }}

  implicit val targetWriter = upickle.Writer[Target] {
    case t:TargetUser => upickle.json.read(upickle.write(Kinded(t.kind, t)))
    case t:TargetGroup => upickle.json.read(upickle.write(Kinded(t.kind, t)))
    case t:TargetCourseReg => upickle.json.read(upickle.write(Kinded(t.kind, t)))
    case t:TargetTaskOutput => upickle.json.read(upickle.write(Kinded(t.kind, t)))
  }
  implicit val targetReader = upickle.Reader[Target] { case o:Js.Obj => o("kind") match {
    case Js.Str(TargetUser.kind) => upickle.read[Kinded[TargetUser]](upickle.json.write(o)).item
    case Js.Str(TargetGroup.kind) => upickle.read[Kinded[TargetGroup]](upickle.json.write(o)).item
    case Js.Str(TargetCourseReg.kind) => upickle.read[Kinded[TargetCourseReg]](upickle.json.write(o)).item
    case Js.Str(TargetTaskOutput.kind) => upickle.read[Kinded[TargetTaskOutput]](upickle.json.write(o)).item
  }}


  implicit val critTargetStrategyReader = upickle.Reader[CritTargetStrategy] { case o:Js.Obj => o("kind") match {
    case Js.Str(OfMyGroupsStrategy.kind) => upickle.read[Kinded[OfMyGroupsStrategy]](upickle.json.write(o)).item
    case Js.Str(PreallocateGroupStrategy.kind) => upickle.read[Kinded[PreallocateGroupStrategy]](upickle.json.write(o)).item
  }}
  implicit val critTargetStrategyWriter = upickle.Writer[CritTargetStrategy] {
    case p:OfMyGroupsStrategy => upickle.json.read(upickle.write(Kinded(p.kind, p)))
    case p:PreallocateGroupStrategy => upickle.json.read(upickle.write(Kinded(p.kind, p)))
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

  implicit val taskOutputBodyWriter = upickle.Writer[TaskOutputBody] {
    case ct:Critique => upickle.json.read(upickle.write(Kinded(ct.kind, ct)))
    case EmptyTaskOutputBody => upickle.json.read(upickle.write(Kinded(EmptyTaskOutputBody.kind, EmptyTaskBody)))
  }
  implicit val taskOutputBodyReader = upickle.Reader[TaskOutputBody] {
    case o:Js.Obj =>
      o("kind") match {
        case Js.Str(CritiqueTask.kind) => upickle.read[Kinded[Critique]](upickle.json.write(o)).item
        case Js.Str(EmptyTaskOutputBody.kind) => EmptyTaskOutputBody
      }
  }

  implicit val coursePreenrolRowReader = upickle.Reader[Course.PreenrolRow] { case o:Js.Obj =>
    new Course.PreenrolRow(
      target = upickle.read[Id[Course,String]](upickle.json.write(o("target"))),
      roles = upickle.read[Set[CourseRole]](upickle.json.write(o("roles"))),
      identity = upickle.read[IdentityLookup](upickle.json.write(o("identity"))),
      used = upickle.read[Option[Used[Course.Reg]]](upickle.json.write(o("used")))
    )
  }
  implicit val coursePreenrolRowWriter = upickle.Writer[Course.PreenrolRow] { case row =>
    Js.Obj(
      "target" -> upickle.json.read(upickle.write(row.target)),
      "roles" -> upickle.json.read(upickle.write(row.roles)),
      "identity" -> upickle.json.read(upickle.write(row.identity)),
      "used" -> upickle.json.read(upickle.write(row.used))
    )
  }

  implicit val coursePreenrolReader = upickle.Reader[Course.Preenrol] { case o:Js.Obj =>
    new Course.Preenrol(
      id = upickle.read[Id[Course.Preenrol,String]](upickle.json.write(o("id"))),
      name = upickle.read[Option[String]](upickle.json.write(o("name"))),
      within = upickle.read[Option[Id[Course,String]]](upickle.json.write(o("within"))),
      rows = upickle.read[Seq[Course.PreenrolRow]](upickle.json.write(o("rows"))),
      created = upickle.read[Long](upickle.json.write(o("created"))),
      modified = upickle.read[Long](upickle.json.write(o("modified")))
    )
  }
  implicit val coursePreenrolWriter = upickle.Writer[Course.Preenrol] { case p =>
    Js.Obj(
      "id" -> upickle.json.read(upickle.write(p.id)),
      "name" -> upickle.json.read(upickle.write(p.name)),
      "within" -> upickle.json.read(upickle.write(p.within)),
      "rows" -> upickle.json.read(upickle.write(p.rows)),
      "created" -> upickle.json.read(upickle.write(p.created)),
      "modified" -> upickle.json.read(upickle.write(p.modified))
    )
  }


  implicit val allocatedCritReader = upickle.Reader[AllocatedCrit] { case o:Js.Obj =>
    new AllocatedCrit(
      target = upickle.read[Target](upickle.json.write(o("target"))),
      critique = upickle.read[Option[Id[TaskOutput,String]]](upickle.json.write(o("critique")))
    )
  }
  implicit val allocatedCritWriter = upickle.Writer[AllocatedCrit] { case row =>
    Js.Obj(
      "target" -> upickle.json.read(upickle.write(row.target)),
      "critique" -> upickle.json.read(upickle.write(row.critique))
    )
  }



}
