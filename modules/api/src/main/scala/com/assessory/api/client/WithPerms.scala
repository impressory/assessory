package com.assessory.api.client


case class WithPerms[T] (perms:Map[String,Boolean], item:T)
