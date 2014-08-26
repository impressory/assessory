package com.assessory.api.question

import com.wbillingsley.handy.Id

case class ShortTextQuestion(
    
  id:Id[Question,String],
  
  prompt: String,
  
  maxLength: Option[Int] = None,
  
  required: Boolean = true,
  
  active: Boolean = true

) extends Question {
  
  val kind = ShortTextQuestion.kind

  def blankAnswer = ShortTextAnswer(Some(id), None)
  
}

object ShortTextQuestion {
  
  val kind = "Short text"
  
}


case class ShortTextAnswer(

  question: Option[Id[Question,String]],

  answer: Option[String]

) extends Answer {
  val kind = ShortTextQuestion.kind
  
  def answerAsString = answer.getOrElse("")
}
