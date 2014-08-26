package com.assessory.play

import com.assessory.api._
import com.assessory.api.wiring.Lookups
import course._
import play.api.mvc.PathBindable
import com.wbillingsley.handy._
import Id._

object PathBinders {

  import Lookups._

  implicit def bindableRWI[T <: HasStringId[T]](implicit lu:LookUp[T,String], g:GetsId[T,String]) = new PathBindable[Ref[T]] {
    def bind(key:String, value:String) = {
      Right(value.asId[T].lazily(lu))
    }

    /*
     * TODO: This is hacky. Routes can't take a union type such as RefWithId, so we've defined bindable on Ref. But that means unbind could involve a fetch
     */
    def unbind(key:String, value:Ref[T]):String = {
      value match {
        case i:IdImmediate[T] => i.getId(g).get.id
        case _ => {
          value.fetch.getId(g).get.id
        }
      }
    }
  }

  implicit val bindCourse = bindableRWI[Course]
  implicit val bindUser = bindableRWI[User]

}
