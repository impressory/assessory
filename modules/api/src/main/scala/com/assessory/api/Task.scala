package com.assessory.api

import com.wbillingsley.handy._
import com.assessory.api.course.Course

case class Task(
    
   id: Id[Task,String],
   
   course: RefWithId[Course] = RefNone,
    
   details: TaskDetails = new TaskDetails,

   body: Option[TaskBody] = None
   
) extends HasStringId[Task] {
  
  def kind = body.map(_.kind)
  
}

case class TaskDetails (
        
    name:Option[String] = None,
    
    description:Option[String] = None,
    
    created: Long = System.currentTimeMillis,
    
    published: Option[Long] = Some(System.currentTimeMillis),
    
    due: Option[Long] = None
)

trait TaskBody {
  
  val kind:String
  
}
