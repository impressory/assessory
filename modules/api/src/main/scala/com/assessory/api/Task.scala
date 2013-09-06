package com.assessory.api

import com.wbillingsley.handy.{Ref, HasStringId}
import com.assessory.api.course.Course

trait Task extends HasStringId {
  
  val kind:String
  
  val course: Ref[Course]
  
  val details: TaskDetails

}

case class TaskDetails (
        
    name:Option[String] = None,
    
    created: Long = System.currentTimeMillis,
    
    published: Option[Long] = None,
    
    due: Option[Long] = None
)
