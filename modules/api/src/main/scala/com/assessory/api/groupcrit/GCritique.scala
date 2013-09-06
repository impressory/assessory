package com.assessory.api.groupcrit

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}
import com.assessory.api.User
import com.assessory.api.course.Course
import com.assessory.api.group.Group
import com.assessory.api.question.Answer

case class GCritique (
    
    id:String,

    byUser: Ref[User] = RefNone,
    
    forGroup: Ref[Group] = RefNone,
    
    answer: Seq[Answer] = Seq.empty

) extends HasStringId