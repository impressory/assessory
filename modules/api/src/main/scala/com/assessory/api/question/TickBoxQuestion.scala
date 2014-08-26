package com.assessory.api.question

import com.wbillingsley.handy.Id

case class TickBoxQuestion(
    
  id:Id[Question,String],
  
  prompt: String,
  
  required: Boolean = true,
  
  active: Boolean = true

) extends Question {
  
  val kind = TickBoxQuestion.kind

  def blankAnswer = TickBoxAnswer(question=Some(id), None)
  
}

object TickBoxQuestion {
  
  val kind = "Tick box"
  
}

case class TickBoxAnswer(
  
  question: Option[Id[Question,String]],
  
  answer: Option[Boolean]

) extends Answer {
  
  val kind = TickBoxQuestion.kind
  
  def answerAsString = answer.map(_.toString).getOrElse("")
  
}