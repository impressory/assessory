package com.assessory.sjsreact

import scala.annotation.meta.field
import scala.scalajs.js
import JSImports._

import scala.scalajs.js.annotation.JSExport

 @JSExport
 class ComponentSpec[T](var displayName:String )  {

   var render: () => js.Any = js.native

   var getInitialState: () => js.Any = () => j()

   var getDefaultProps: () => js.Any = () => j()

   var propTypes: js.Any = j()

   var mixins: js.Array[js.Any] = new js.Array

   var statics: js.Any = j()

   var componentWillMount: () => Unit = { () => }

   var componentWillUnmount: () => Unit = { () => }

   def replaceState(s:T) = js.native

   def state:T = js.native

 }

object JSReact {



  trait ReactSig extends js.Any {
    def createElement(name:js.Any, props:js.Any, children: js.Any*):js.Any = js.native

    def createClass(spec:js.Any):js.Any = js.native

  }

  def TypedReact = global.React.asInstanceOf[ReactSig]

  def React = global.React

  def e = React.createElement

  def divC(cn:String)(args:js.Any*) = TypedReact.createElement("div", j(className = cn), args:_*)

  def div(args:js.Any*) = TypedReact.createElement("div", j(), args:_*)

  def spanC(cn:String)(args:js.Any*) = TypedReact.createElement("span", j(className = cn), args:_*)

  def span(args:js.Any*) = TypedReact.createElement("span", j(), args:_*)

  def componentUT(displayName:String)(block: js.Any => js.Any) = block(j(displayName = displayName))

  def component[T](displayName:String)(block: ComponentSpec[T] => Unit) = {
    val spec = j(displayName = displayName)
    block(spec.asInstanceOf[ComponentSpec[T]])
    spec
  }

  def createClass[T](displayName:String)(block: ComponentSpec[T] => Unit) = React.createClass(component(displayName)(block))

/*
  def component(
    displayName:String,
    render: () => js.Any,
    getInitialState: () => js.Any = () => j(),
    getDefaultProps: () => js.Any = () => j(),
    componentWillMount: () => js.Any = () => j(),
    componentWillUnmount: () => js.Any = () => j(),
    propTypes: js.Any = j(),
    mixins: js.Array[js.Any] = new js.Array,
    statics: js.Any = j()
  ) = j(
    displayName = displayName, render = render,
    getInitialState = getInitialState, getDefaultProps = getDefaultProps,
    componentWillMount = componentWillMount, componentWillUnmount = componentWillUnmount,
    propTypes = propTypes, mixins = mixins, statics = statics
  )
*/
}
