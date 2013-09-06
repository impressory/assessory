package com.assessory.api.groupcrit

import com.wbillingsley.handy.{Ref, RefNone}
import com.assessory.api.{Task, TaskDetails, TaskBody}
import com.assessory.api.course.Course
import com.assessory.api.group.GroupSet
import com.assessory.api.question.Questionnaire

case class GroupCritTask (
    
    number:Int,
    
    groupToCrit: Ref[GroupSet] = RefNone,
    
    withinSet: Ref[GroupSet] = RefNone,
    
    preallocate: Boolean = true,
    
    questionnaire: Questionnaire = new Questionnaire 
    
) extends TaskBody {
  
  val kind = GroupCritTask.kind
  
}

object GroupCritTask {
  
  val kind = "Group critique by an individual"
  
}