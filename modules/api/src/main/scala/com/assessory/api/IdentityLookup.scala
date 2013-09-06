package com.assessory.api

case class IdentityLookup(service:String, value:Option[String], username:Option[String], used:Boolean=false)