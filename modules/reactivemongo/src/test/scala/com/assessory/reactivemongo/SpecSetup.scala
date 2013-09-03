package com.assessory.reactivemongo

import scala.concurrent.ExecutionContext.Implicits.global

object SpecSetup {

  lazy val doSetup = {
    DBConnector.dbName = "testAssessory"
    UserDAO.coll.drop
    
    true
  }
  
}