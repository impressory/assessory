package com.assessory.api.groupcrit

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}
import com.assessory.api._
import com.assessory.api.course.Course
import com.assessory.api.group.Group
import com.assessory.api.question.Answer

case class GCritique  (
    
    forGroup: Ref[Group] = RefNone,
    
    answers: Seq[Answer] = Seq.empty

) extends TaskOutputBody {
  
 val kind = GCritique.kind
  
}

object GCritique {
  val kind = GroupCritTask.kind
}

