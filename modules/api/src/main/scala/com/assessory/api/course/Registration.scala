package com.assessory.api.course

import com.wbillingsley.handy._

case class Registration(
  course: RefWithId[Course],
  
  roles: Seq[CourseRole.T]
)
