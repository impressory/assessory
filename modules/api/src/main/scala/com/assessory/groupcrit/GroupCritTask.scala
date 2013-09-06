package com.assessory.groupcrit

import com.wbillingsley.handy.{Ref, RefNone}
import com.assessory.api.{Task, TaskDetails}
import com.assessory.api.course.Course
import com.assessory.api.group.GroupSet
import com.assessory.api.question.Questionnaire

case class CritTask(
    
    id:String,
    
    details: TaskDetails = new TaskDetails,
    
    course: Ref[Course] = RefNone,
    
    groupToCrit: Ref[GroupSet] = RefNone,
    
    withinSet: Ref[GroupSet] = RefNone,
    
    questionnaire: Questionnaire = new Questionnaire 
    
) extends Task {
  
  val kind = CritTask.kind
  
}

object CritTask {
  
  val kind = "Group critique by an individual"
  
}