package com.assessory.play

import com.wbillingsley.handy.Id._
import com.wbillingsley.handy._
import play.api.mvc.PathBindable

object PathBinders {

  implicit def bindableId[T](implicit lu:LookUp[T,String], g:GetsId[T,String]):PathBindable[Id[T,String]] = new PathBindable[Id[T,String]] {
    def bind(key:String, value:String) = {
      Right(value.asId[T])
    }

    def unbind(key:String, value:Id[T,String]):String = value.id
  }

}
