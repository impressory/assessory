package com.assessory.api.outputcrit

import com.wbillingsley.handy._
import com.assessory.api._
import com.assessory.api.question.Questionnaire

case class OutputCritTask (
    
    taskToCrit: RefWithId[Task] = RefNone,
    
    questionnaire: Questionnaire = new Questionnaire
    
) extends TaskBody {
  
  val kind = OutputCritTask.kind
  
}

object OutputCritTask {
  
  val kind = "Task output critique"
  
}