package com.assessory.sjsreact

import com.assessory.sjsreact.JSReact._

import scala.collection.mutable


class ObservableVar[T](private var _value:T) {
  val listeners:mutable.Set[(T) => Unit] = mutable.Set.empty

  def value = _value

  def value_=(newValue:T): Unit = {
    if (value != newValue) {
      _value = newValue
      listeners.foreach(_(value))
    }
  }
}

object Navigation {

  trait NavEl

  case class NCourse(id:String) extends NavEl

  val path = new ObservableVar(Seq.empty[NavEl])


  val BreadCrumbs = createClass[Seq[NavEl]]("TypedBox"){ spec =>

    def listener(n:Seq[NavEl]) = {
      spec.replaceState(n)
      println(n)
      println(System.currentTimeMillis())
    }

    spec.render = () => {
      val spans = for { el <- spec.state } yield span(el.toString)
      div(spans:_*)
    }

    spec.componentWillMount = () => {
      Navigation.path.listeners.add(listener)
    }
    spec.componentWillUnmount = () => {
      Navigation.path.listeners.remove(listener)
    }
  }




}
