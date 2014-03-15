package com.assessory.api.groupcrit

import com.wbillingsley.handy._
import com.assessory.api.{Task, TaskDetails, TaskBody}
import com.assessory.api.course.Course
import com.assessory.api.group.GroupSet
import com.assessory.api.question.Questionnaire

case class GroupCritTask (
    
    number:Int,
    
    groupToCrit: RefWithId[GroupSet] = RefNone,

    withinSet: RefWithId[GroupSet] = RefNone,

    preallocate: Boolean = true,
    
    questionnaire: Questionnaire = new Questionnaire, 
    
    allocated: Boolean = false
    
) extends TaskBody {
  
  val kind = GroupCritTask.kind
  
}

object GroupCritTask {
  
  val kind = "Group critique by an individual"
  
}