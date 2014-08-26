package com.assessory.api.question

import com.wbillingsley.handy.Id

trait Answer {

  val question: Option[Id[Question,String]]
  
  val kind:String
  
  def answerAsString: String
  
}