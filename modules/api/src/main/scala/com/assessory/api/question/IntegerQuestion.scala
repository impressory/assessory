package com.assessory.api.question

case class IntegerQuestion(
  
    id: String,
    
    prompt: String,
    
    max: Option[Int] = Some(5),
    
    min: Option[Int] = Some(0),
    
    required: Boolean = true,
    
    active: Boolean = true

) extends Question {
  
  val kind = IntegerQuestion.kind

  def blankAnswer = IntegerAnswer(question=Some(id), None)
  
}

object IntegerQuestion {
  
  val kind = "Integer"
  
}

case class IntegerAnswer(
  
  question: Option[String],
  
  answer: Option[Int]

) extends Answer {
  val kind = IntegerQuestion.kind
  
  def answerAsString = answer.map(_.toString).getOrElse("")
}