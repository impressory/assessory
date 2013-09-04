package com.assessory.api

case class ActiveSession(
    key:String, 
    
    ip:String,
    
    since:Long = System.currentTimeMillis
)