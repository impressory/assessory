package com.assessory.api

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}
import com.assessory.api.course.Course

case class Task(
    
   id: String,
   
   course: Ref[Course] = RefNone,
    
   details: TaskDetails = new TaskDetails,

   body: Option[TaskBody] = None
   
) extends HasStringId {
  
  def kind = body.map(_.kind)
  
}

case class TaskDetails (
        
    name:Option[String] = None,
    
    created: Long = System.currentTimeMillis,
    
    published: Option[Long] = None,
    
    due: Option[Long] = None
)

trait TaskBody {
  
  val kind:String
  
}
