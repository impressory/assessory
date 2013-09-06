package com.assessory.reactivemongo

import org.specs2.mutable._
import com.wbillingsley.handy._
import Ref._
import scala.concurrent.ExecutionContext.Implicits.global
import com.assessory.api._
import course._
import org.specs2.specification.BeforeExample
import org.specs2.specification.Step

class PreenrolDAOSpec extends DatabaseSpec {
      
  "PreenrolDAO" should {
    
    "Find a pre-enrolment" in {
      
      val c = CourseDAO.unsaved.copy(title=Some("Preenrolment test course 1"))
      val pe = PreenrolDAO.unsaved.copy(course=c.itself, identities=Seq(IdentityLookup(service="github", value=None, username=Some("pe t1"))))
          
      val returned = for (
        cs <- CourseDAO.saveNew(c);
        ps <- PreenrolDAO.saveNew(pe);
        found <- PreenrolDAO.useRow(service="github", value=None, username=Some("pe t1"));
        a <- Ref(found.course.getId)
      ) yield a
      
      returned.toRefOne.map(_.toList).toFuture must be_==(Some(List(c.id))).await
    }
    
    "Mark a pre-enrolment used if it uses it" in {
      
      val c = CourseDAO.unsaved.copy(title=Some("Preenrolment test course 2"))
      val pe = PreenrolDAO.unsaved.copy(course=c.itself, identities=Seq(IdentityLookup(service="github", value=None, username=Some("pe t2"))))
          
      val returned = for (
        cs <- CourseDAO.saveNew(c);
        ps <- PreenrolDAO.saveNew(pe);
        found <- PreenrolDAO.useRow(service="github", value=None, username=Some("pe t2"));
        row <- Ref(found.identities.headOption)
      ) yield row.used 
      
      returned.toRefOne.map(_.toList).toFuture must be_==(Some(List(true))).await

    } 
    
  }

}