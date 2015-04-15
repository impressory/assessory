package com.assessory.play.json

import com.wbillingsley.handy._
import play.api.libs.json._
import com.assessory.api._
import com.assessory.api.critique._
import com.assessory.api.group._

import com.assessory.reactivemongo._
import com.assessory.api.question.Questionnaire
import com.assessory.api.TaskOutput

object CritiqueJson {

  implicit val qFormat = QuestionFormat
  implicit val qsFormat = Json.format[Questionnaire]

  implicit val aFormat = AnswerFormat

  implicit object CTSFormat extends Format[CritTargetStrategy] {
    override def reads(json: JsValue): JsResult[CritTargetStrategy] = {
      (json \ "kind").as[String] match {
        case PreallocateGroupStrategy.kind => JsSuccess(PreallocateGroupStrategy(
          set = (json \ "set").as[RefWithId[GroupSet]],
          number = (json \ "number").as[String].toInt
        ))
        case OfMyGroupsStrategy.kind => JsSuccess(OfMyGroupsStrategy(
          task = (json \ "task").as[RefWithId[Task]]
        ))
      }
    }

    override def writes(o: CritTargetStrategy): JsValue = o match {
      case OfMyGroupsStrategy(t) => Json.obj(
        "kind" -> o.kind,
        "task" -> t
      )
      case PreallocateGroupStrategy(set, number) => Json.obj(
        "kind" -> o.kind,
        "set" -> set,
        "number" -> number
      )
    }
  }


  implicit object CTFormat extends Format[CritTarget] {
    def writes(ct:CritTarget) = ct match {
      case CTGroup(g) => Json.obj("kind" -> "Group", "ref" -> g)
      case CTTaskOutput(t)  => Json.obj("kind" -> "TaskOutput", "ref" -> t)
    }

    implicit val lug = com.assessory.reactivemongo.GroupDAO.LookUp
    implicit val luto =com.assessory.reactivemongo.TaskOutputDAO.LookUp

    def reads(json:JsValue) = (json \ "kind").as[String] match {
      case "Group" => JsSuccess(CTGroup(g = (json\"ref").as[Id[Group,String]]))
      case "TaskOutput"  => JsSuccess(CTTaskOutput(t = (json\"ref").as[Id[TaskOutput,String]]))
    }
  }

  implicit val ctFormat = Json.format[CritiqueTask]
  implicit val gctFormat = Json.format[Critique]


}
