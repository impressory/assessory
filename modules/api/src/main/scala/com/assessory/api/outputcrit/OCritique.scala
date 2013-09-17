package com.assessory.api.outputcrit

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}
import com.assessory.api._
import com.assessory.api.course.Course
import com.assessory.api.group.Group
import com.assessory.api.question.Answer

case class OCritique  (
    
    forOutput: Ref[TaskOutput] = RefNone,
    
    answers: Seq[Answer] = Seq.empty

) extends TaskOutputBody {
  
 val kind = OCritique.kind
  
}

object OCritique {
  val kind = OutputCritTask.kind
}

