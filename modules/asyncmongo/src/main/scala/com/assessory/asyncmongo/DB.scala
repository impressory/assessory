package com.assessory.asyncmongo

import scala.concurrent.ExecutionContext

object DB extends com.wbillingsley.handy.mongodbasync.DB {

  // Change the default name
  dbName = "assessory_2015_1"

  var executionContext:ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

}
