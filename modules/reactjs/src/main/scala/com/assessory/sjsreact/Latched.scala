package com.assessory.sjsreact

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.mutable
import scala.util.{Try, Failure, Success}

/**
 * A lazy, clearable Future.
 */
class Latched[T](op: => Future[T])(implicit ec:ExecutionContext) {

  val listeners:mutable.Set[Latched.Listener[T]] = mutable.Set.empty

  var cached:Option[Future[T]] = None

  def clear() = {
    cached = None
    listeners.foreach { _(None) }
    WebApp.rerender()
  }

  def fill(v:T) = {
    cached = Some(Future.successful(v))
    listeners.foreach { _(Some(Success(v))) }
    WebApp.rerender()
  }

  def fail(t:Throwable) = {
    cached = Some(Future.failed(t))
    listeners.foreach { _(Some(Failure(t))) }
    WebApp.rerender()
  }

  def request:Future[T] = {
    cached match {
      case Some(x) => x
      case _ => {
        val v = op
        cached = Some(v)
        v.onComplete(_ => WebApp.rerender())(scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow)
        v
      }
    }
  }

}

object Latched {

  type Listener[T] = (Option[Try[T]] => Unit)

  def immediate[T](op: => T)(implicit ec:ExecutionContext):Latched[T] = new Latched(Future.successful(op))

  def future[T](op: => Future[T])(implicit ec:ExecutionContext):Latched[T] = new Latched(op)
}
