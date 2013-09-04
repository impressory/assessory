package com.assessory.api.course

import com.wbillingsley.handy.Ref

case class Registration(
  course: Ref[Course],
  
  roles: Seq[CourseRole.T]
)
