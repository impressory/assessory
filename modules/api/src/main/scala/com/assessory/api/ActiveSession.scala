package com.assessory.api

case class ActiveSession(key:String, since:Long = System.currentTimeMillis)