package com.assessory

import com.wbillingsley.handy.Id._

package object sjsreact {

  implicit val ec = scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

  def invalidId[T] = "invalid".asId[T]

}
