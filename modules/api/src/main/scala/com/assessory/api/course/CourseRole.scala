package com.assessory.api.course

object CourseRole {
  
  type T = String
  
  val staff:T = "staff"
    
  val student:T = "student"
    
  val roles:Set[T] = Set(staff, student)
  
}