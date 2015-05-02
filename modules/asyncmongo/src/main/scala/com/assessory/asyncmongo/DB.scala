package com.assessory.asyncmongo

import scala.concurrent.ExecutionContext

object DB extends com.wbillingsley.handy.mongodbasync.DB {

  // Change the default name
  dbName = "assessory"

  var executionContext:ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

}
