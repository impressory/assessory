package com.assessory.api

/** An error by the user. */
case class UserError(msg:String) extends Exception(msg)