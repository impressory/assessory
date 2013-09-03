package com.assessory.reactivemongo

import org.specs2.mutable._
import com.wbillingsley.handy._
import Ref._
import scala.concurrent.ExecutionContext.Implicits.global
import com.assessory.api._
import org.specs2.specification.BeforeExample
import org.specs2.specification.Step

class UserDAOSpec extends Specification with BeforeExample {
      
  def before = {
    Step(SpecSetup.doSetup)
  }
    
  "UserDAO" should {
    
    "save a new user" in {      
      val u = UserDAO.unsaved.copy(name=Some("Algernon Moncrieff"))      
      
      val returnedName = for (
          saved <- UserDAO.saveNew(u);      
          fetched <- UserDAO.byId(u.id); 
          name <- fetched.name
      ) yield name      
      returnedName.toFuture must be_==(Some("Algernon Moncrieff")).await      
    }
    
    "push identities correctly" in {      
      val u = UserDAO.unsaved.copy(name=Some("Bertie Wooster"))      
      
      val returnedName = for (
        saved <- UserDAO.saveNew(u);      
        pushed <- UserDAO.pushIdentity(saved.itself, Identity(service="spoodle", value="spong"));
        fetched <- UserDAO.byIdentity(service="spoodle", id="spong"); name <- fetched.name
      ) yield name      
      returnedName.toFuture must be_==(Some("Bertie Wooster")).await      
    }
    
    "push sessions correctly" in {      
      val u = UserDAO.unsaved.copy(name=Some("Cecily Cardew"))      
      
      val returnedName = for (
        saved <- UserDAO.saveNew(u);      
        pushed <- UserDAO.pushSession(saved.itself, ActiveSession(key="mysession"));
        fetched <- UserDAO.bySessionKey("mysession"); name <- fetched.name
      ) yield name      
      
      returnedName.toFuture must be_==(Some("Cecily Cardew")).await      
    }
    
  }

}