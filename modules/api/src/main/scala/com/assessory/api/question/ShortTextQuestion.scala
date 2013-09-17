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
  
  question: Option[String],
  
  answer: Option[String]

) extends Answer {
  val kind = ShortTextQuestion.kind
  
  def answerAsString = answer.getOrElse("")
}
