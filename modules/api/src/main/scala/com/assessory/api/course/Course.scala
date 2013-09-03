package com.assessory.api.course

import com.wbillingsley.handy.{Ref, HasStringId}

case class Course (

    id:String,
    
    name:Option[String]
    
) extends HasStringId