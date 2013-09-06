package com.assessory.api.question

case class ShortTextQuestion(
    
  id:String, 
  
  prompt: String,
  
  maxLength: Option[Int] = None,
  
  required: Boolean = true,
  
  active: Boolean = true

) extends Question {
  
  val kind = ShortTextQuestion.kind
  
}

object ShortTextQuestion {
  
  val kind = "Short text"
  
}


case class ShortTextAnswer(
  
  question: String,
  
  answer: String

) extends Answer
