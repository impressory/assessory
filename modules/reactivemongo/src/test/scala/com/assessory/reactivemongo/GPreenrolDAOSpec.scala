package com.assessory.reactivemongo

import org.specs2.mutable._
import com.wbillingsley.handy._
import Ref._
import scala.concurrent.ExecutionContext.Implicits.global
import com.assessory.api._
import course._
import group._
import org.specs2.specification.BeforeExample
import org.specs2.specification.Step

class GPreenrolDAOSpec extends DatabaseSpec {
      
  "GPreenrolDAO" should {
    
    "Find a pre-enrolment" in {
      
      val c = CourseDAO.unsaved.copy(title=Some("Preenrolment test course 1"))
      val gs = GroupSetDAO.unsaved.copy(name=Some("group set 1"))
      val group = GroupDAO.unsaved.copy(course=c.itself, set=gs.itself, name=Some("one"))
      val gpe = GPreenrolDAO.unsaved.copy(course=c.itself, set=gs.itself, groupData=Seq(
        GPreenrol.GroupData(group=group.itself, lookups=Seq(IdentityLookup(service="github", value=None, username=Some("gpe t1"))))
      ))
          
      val returned = for (
        cs <- CourseDAO.saveNew(c);
        gss <- GroupSetDAO.saveNew(gs);
        groupsaved <- GroupDAO.saveNew(group);
        gpesaved <- GPreenrolDAO.saveNew(gpe);
        found <- GPreenrolDAO.byIdentity(course=c.itself, service="github", value=None, username=Some("gpe t1"));
        a <- Ref(found.course.getId)
      ) yield a
      
      returned.toRefOne.map(_.toList).toFuture must be_==(Some(List(c.id))).await
    }
    
    
    "Mark a pre-enrolment used if it uses it" in {
      
      val c = CourseDAO.unsaved.copy(title=Some("Preenrolment test course 2"))
      val gs = GroupSetDAO.unsaved.copy(name=Some("group set 2"))
      val group = GroupDAO.unsaved.copy(course=c.itself, set=gs.itself, name=Some("two"))
      val gpe = GPreenrolDAO.unsaved.copy(course=c.itself, set=gs.itself, groupData=Seq(
        GPreenrol.GroupData(group=group.itself, lookups=Seq(
            IdentityLookup(service="github", value=None, username=Some("gpe t2")),
            IdentityLookup(service="github", value=None, username=Some("notme"))
          ))
      ))
          
      val returned = for (
        cs <- CourseDAO.saveNew(c);
        gss <- GroupSetDAO.saveNew(gs);
        groupsaved <- GroupDAO.saveNew(group);
        gpesaved <- GPreenrolDAO.saveNew(gpe);
        found <- GPreenrolDAO.useRow(course=c.itself, service="github", value=None, username=Some("gpe t2"));
        reloaded <- GPreenrolDAO.byId(gpesaved.id);
        row = reloaded.groupData.head.lookups.head
      ) yield row.used
      
      returned.toRefOne.map(_.toList).toFuture must be_==(Some(List(true))).await
    }    
    
    
  }

}