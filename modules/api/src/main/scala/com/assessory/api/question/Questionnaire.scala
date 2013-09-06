package com.assessory.api.question

import com.wbillingsley.handy.HasStringId

case class Questionnaire(
    questions: Seq[Question] = Seq.empty
)
