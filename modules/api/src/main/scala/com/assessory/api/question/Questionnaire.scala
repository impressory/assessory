package com.assessory.api.question

import com.wbillingsley.handy.HasStringId

case class Questionnaire(
    
    name: Option[String] = None,
    
    questions: Seq[Question] = Seq.empty,
    
    created: Long = System.currentTimeMillis
    
)
