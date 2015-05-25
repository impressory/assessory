package com.assessory.sjsreact

import org.scalajs.dom
import org.scalajs.dom.ext.AjaxException

import scala.concurrent.{ExecutionContext, Future}

package object services {

  val AJAX_HEADERS =  Map("Accept" -> "application/json", "Content-Type" -> "text/plain; charset=utf-8")

  implicit class FXHROps(val fxhr:Future[dom.XMLHttpRequest]) extends AnyVal {
    def responseText(implicit ec:ExecutionContext) = fxhr.map(_.responseText)
  }

  implicit class FutOps[T](val f:Future[T]) extends AnyVal {
    def optional404(implicit ec:ExecutionContext) = f.map(Some(_)) recover { case AjaxException(req) if req.status == 404 => None }
  }


}
