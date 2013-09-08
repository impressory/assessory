package com.assessory.api.question

trait Answer {

  val question: Option[String]
  
  val kind:String
  
}