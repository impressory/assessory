package com.assessory.api.groupcrit

import com.wbillingsley.handy._
import com.assessory.api._
import com.assessory.api.group.Group
import com.assessory.api.question.Answer

case class GCritique  (
    
    forGroup: RefWithId[Group] = RefNone,
    
    answers: Seq[Answer] = Seq.empty

) extends TaskOutputBody {
  
 val kind = GCritique.kind
  
}

object GCritique {
  val kind = GroupCritTask.kind
}

