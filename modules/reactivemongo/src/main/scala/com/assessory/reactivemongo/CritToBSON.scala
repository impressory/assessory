package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import critique._
import group._
import com.wbillingsley.handy._

import com.assessory.api.wiring.Lookups._

object CritiqueTaskToBSON {
  
  implicit val qhandler = QuestionHandler
  implicit val qqhandler = QuestionnaireHandler

  implicit object sHandler extends BSONDocumentReader[CritTargetStrategy] with BSONDocumentWriter[CritTargetStrategy] {

    override def read(bson: BSONDocument): CritTargetStrategy = {
      bson.getAs[String]("kind").get match {
        case PreallocateGroupStrategy.kind => PreallocateGroupStrategy(
          set = bson.getAs[RefWithId[GroupSet]]("set").get,
          number = bson.getAs[Int]("number").get
        )
        case OfMyGroupsStrategy.kind => OfMyGroupsStrategy(
          task = bson.getAs[RefWithId[Task]]("task").get
        )
      }
    }

    override def write(t: CritTargetStrategy): BSONDocument = {
      t match {
        case PreallocateGroupStrategy(set, number) => BSONDocument(
          "kind" -> t.kind,
          "set" -> set,
          "number" -> number
        )
        case OfMyGroupsStrategy(fromt) => BSONDocument(
          "kind" -> t.kind,
          "task" -> fromt
        )
      }
    }
  }

  def newBSON(g:CritiqueTask) = BSONDocument(
    "questionnaire" -> g.questionnaire,
    "strategy" -> g.strategy
  )

  def updateBSON(g:CritiqueTask) = BSONDocument(
    "body.strategy" -> g.strategy,
    "body.questionnaire" -> g.questionnaire
  )

  def read(doc:BSONDocument) = {
    CritiqueTask(
      questionnaire=doc.getAs[Questionnaire]("questionnaire").getOrElse(new Questionnaire),
      strategy=doc.getAs[CritTargetStrategy]("strategy").get
    )
  }

}


object CritiqueToBSON {
  
  import CritiqueTaskToBSON._
  
  implicit val ah = AnswerHandler

  implicit object CritTargetHandler extends BSONHandler[BSONDocument, CritTarget] {

    import CommonFormats._

    def read(doc:BSONDocument) = {
      val kind = doc.getAs[String]("kind").get
      kind match {
        case "TaskOutput" => CTTaskOutput(doc.getAs[Id[TaskOutput,String]]("ref").get)
        case "Group" => CTGroup(doc.getAs[Id[Group,String]]("ref").get)
      }
    }

    def write(ct:CritTarget) = ct match {
      case CTTaskOutput(t) => BSONDocument("kind" -> "TaskOutput", "ref" -> t)
      case CTGroup(g) => BSONDocument("kind" -> "Group", "ref" -> g)
    }
  }

  implicit object AllocatedCritHandler extends BSONHandler[BSONDocument, AllocatedCrit] {
    def read(doc:BSONDocument) = {
      AllocatedCrit(
        target = doc.getAs[CritTarget]("target").get,
        critique = doc.getAs[RefWithId[TaskOutput]]("critique").getOrElse(RefNone)
      )
    }

    def write(crit:AllocatedCrit) = BSONDocument(
      "target" -> crit.target,
      "critique" -> crit.critique
    )
  }
  
  def newBSON(gc:Critique) = BSONDocument("target" -> gc.target, "answers" -> gc.answers, "kind" -> Critique.kind)
  
  def updateBSON(gc:Critique) = BSONDocument("body.target" -> gc.target, "body.answers" -> gc.answers)
  
  implicit object gcReader extends BSONDocumentReader[Critique] {
    def read(doc:BSONDocument) = {
      Critique(
        target = doc.getAs[CritTarget]("target").get,
        answers = doc.getAs[Seq[Answer]]("answers").getOrElse(Seq.empty)
      )
    }
  }  
  
}