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
  
  val kind = "Tickbox"
  
}

case class TickBoxAnswer(
  
  question: String,
  
  answer: Boolean

) extends Answer