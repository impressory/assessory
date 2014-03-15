package com.assessory.reactivemongo

import _root_.reactivemongo.bson._
import com.assessory.api._
import question._
import outputcrit._
import group._
import com.wbillingsley.handy._

object OutputCritToBSON extends BSONDocumentReader[OutputCritTask] {
  
  implicit val qhandler = QuestionHandler
  implicit val qqhandler = QuestionnaireHandler

  def read(doc:BSONDocument) = {
    OutputCritTask(
      taskToCrit=doc.getAs[RefWithId[Task]]("taskToCrit").getOrElse(RefNone),
      questionnaire=doc.getAs[Questionnaire]("questionnaire").getOrElse(new Questionnaire)
    )
  }  
  
  def newBSON(g:OutputCritTask) = BSONDocument(
        "taskToCrit" -> g.taskToCrit,
        "questionnaire" -> g.questionnaire
      )
      
  def updateBSON(o:OutputCritTask) = BSONDocument(
        "body.taskToCrit" -> o.taskToCrit,
        "body.questionnaire" -> o.questionnaire
      )
      

}


object OCritiqueToBSON {
  
  import GroupCritToBSON._
  
  implicit val ah = AnswerHandler
  
  def newBSON(gc:OCritique) = BSONDocument("forOutput" -> gc.forOutput, "answers" -> gc.answers, "kind" -> OCritique.kind)
  
  def updateBSON(gc:OCritique) = BSONDocument("body.forOutput" -> gc.forOutput, "body.answers" -> gc.answers)
  
  implicit object gcReader extends BSONDocumentReader[OCritique] {
    def read(doc:BSONDocument) = {
      OCritique(
        forOutput = doc.getAs[RefWithId[TaskOutput]]("forOutput").getOrElse(RefNone),
        answers = doc.getAs[Seq[Answer]]("answers").getOrElse(Seq.empty)
      )
    }
  }  
  
}