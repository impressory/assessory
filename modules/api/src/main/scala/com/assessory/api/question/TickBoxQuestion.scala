package com.assessory.api.question

case class TickBoxQuestion(
    
  id:String, 
  
  prompt: String,
  
  required: Boolean = true,
  
  active: Boolean = true

) extends Question {
  
  val kind = TickBoxQuestion.kind
  
}

object TickBoxQuestion {
  
  val kind = "Tick box"
  
}

case class TickBoxAnswer(
  
  question: Option[String],
  
  answer: Option[Boolean]

) extends Answer {
  
  val kind = TickBoxQuestion.kind
  
}