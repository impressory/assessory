package com.assessory

package object asyncmongo {

  implicit def executionContext = DB.executionContext

}
