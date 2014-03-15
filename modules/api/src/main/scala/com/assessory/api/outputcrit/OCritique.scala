package com.assessory.api.outputcrit

import com.wbillingsley.handy._
import com.assessory.api._
import com.assessory.api.question.Answer

case class OCritique  (
    
    forOutput: RefWithId[TaskOutput] = RefNone,
    
    answers: Seq[Answer] = Seq.empty

) extends TaskOutputBody {
  
 val kind = OCritique.kind
  
}

object OCritique {
  val kind = OutputCritTask.kind
}

