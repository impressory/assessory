package com.assessory.sjsreact

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.mutable
import scala.util.{Failure, Success}

/**
 * A clearable future
 */
class Latched[T](op: => Future[T])(implicit ec:ExecutionContext) {


  val listeners:mutable.Set[Latched.Listener[T]] = mutable.Set.empty

  var content:Option[T] = None

  var error:Option[Throwable] = None

  var requested:Boolean = false

  def clear() = {
    content = None
    error = None
    requested = false
    listeners.foreach { l => l.onChange(content); l.onClear() }
    _onChange(content)
    _onClear()
    WebApp.rerender()
  }

  def fill(v:T) = {
    content = Some(v)
    error = None
    requested = false
    listeners.foreach { l => l.onChange(content); l.onSet(v) }
    _onChange(content)
    _onSet(v)
    WebApp.rerender()
  }

  def fail(t:Throwable) = {
    content = None
    error = Some(t)
    listeners.foreach { l => l.onChange(content); l.onClear() }
    _onChange(content)
    _onClear()
    WebApp.rerender()
  }

  def request:Option[T] = {
    content match {
      case Some(c) => Some(c)
      case None =>
        if (!requested && error.isEmpty) {
          requested = true
          // TODO: cache op in a var (can't query op as it's call by name, so need to cache the resulting Future)
          op.andThen {
            case Success(x) => fill(x)
            case Failure(t) => fail(t)
          }
        }
        None
    }
  }

  private var _onSet: T => Unit = { _ => }

  private var _onClear: () => Unit = { () => }

  private var _onChange: Option[T] => Unit = { _ => }

  def onSet(f: T => Unit) = {
    _onSet = f
    this
  }

  def onClear(f: () => Unit) = {
    _onClear = f
    this
  }

  def onChange(f: Option[T] => Unit) = {
    _onChange = f
    this
  }

}

object Latched {

  case class Listener[T](onSet: T => Unit = { x:T => }, onClear: () => Unit = { () => }, onChange: Option[T] => Unit = { x:Option[T] => })

  def immediate[T](op: => T)(implicit ec:ExecutionContext):Latched[T] = new Latched(Future.successful(op))

  def future[T](op: => Future[T])(implicit ec:ExecutionContext):Latched[T] = new Latched(op)
}
