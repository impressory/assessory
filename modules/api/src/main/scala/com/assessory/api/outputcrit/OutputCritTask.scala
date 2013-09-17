package com.assessory.api.outputcrit

import com.wbillingsley.handy.{Ref, RefNone}
import com.assessory.api._
import com.assessory.api.course.Course
import com.assessory.api.group.GroupSet
import com.assessory.api.question.Questionnaire

case class OutputCritTask (
    
    taskToCrit: Ref[Task] = RefNone,
    
    questionnaire: Questionnaire = new Questionnaire
    
) extends TaskBody {
  
  val kind = OutputCritTask.kind
  
}

object OutputCritTask {
  
  val kind = "Task output critique"
  
}