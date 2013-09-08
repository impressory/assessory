package com.assessory.reactivemongo

import com.assessory.api._
import question._
import reactivemongo.bson._

object QuestionHandler extends BSONHandler[BSONDocument, Question] {
  
  val shortTextWriter = Macros.writer[ShortTextQuestion]
  val tickBoxWriter = Macros.writer[TickBoxQuestion]
  val integerWriter = Macros.writer[IntegerQuestion]
  
  object shortTextReader extends BSONDocumentReader[ShortTextQuestion] {
    def read(doc:BSONDocument) = ShortTextQuestion(
       id=doc.getAs[String]("id").getOrElse(TaskDAO.allocateId),
       prompt=doc.getAs[String]("prompt").getOrElse(""),
       maxLength=doc.getAs[Int]("maxLength")
    )
  }
  
  object tickBoxReader extends BSONDocumentReader[TickBoxQuestion] {
    def read(doc:BSONDocument) = TickBoxQuestion(
       id=doc.getAs[String]("id").getOrElse(TaskDAO.allocateId),
       prompt=doc.getAs[String]("prompt").getOrElse("")
    )
  }

  object integerReader extends BSONDocumentReader[IntegerQuestion] {
    def read(doc:BSONDocument) = IntegerQuestion(
       id=doc.getAs[String]("id").getOrElse(TaskDAO.allocateId),
       prompt=doc.getAs[String]("prompt").getOrElse(""),
       max=doc.getAs[Int]("max"),
       min=doc.getAs[Int]("min")
    )
  }

  def read(doc:BSONDocument):Question = {
    
    val kind = doc.getAs[String]("kind").get
    
    kind match {
      case ShortTextQuestion.kind => shortTextReader.read(doc)
      case TickBoxQuestion.kind => tickBoxReader.read(doc)
      case IntegerQuestion.kind => integerReader.read(doc)
    }
  }
  
  def write(q:Question):BSONDocument = {
    val base:BSONDocument = q match {
      case s:ShortTextQuestion => shortTextWriter.write(s)
      case t:TickBoxQuestion => tickBoxWriter.write(t)
      case i:IntegerQuestion => integerWriter.write(i)
    }
    BSONDocument("kind" -> q.kind) ++ base
  }
  
}


object QuestionnaireHandler extends BSONDocumentReader[Questionnaire] with BSONDocumentWriter[Questionnaire] {
  implicit val qhandler = QuestionHandler
  
  def write(q:Questionnaire) = BSONDocument(
    "questions" -> q.questions
  )

  def read(doc:BSONDocument) = {
    Questionnaire(questions=doc.getAs[Seq[Question]]("questions").getOrElse(Seq.empty))
  }  
  
}


