package com.assessory.model

import com.assessory.api.critique.{OfMyGroupsStrategy, CritiqueTask}
import com.assessory.api.wiring.Lookups
import com.assessory.api.{TaskDetails, Task}
import com.assessory.api.client.invalidId
import com.assessory.asyncmongo.DB
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{ActiveSession, Course}
import com.wbillingsley.handy.mongodbasync.FuturifySRC
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach
import Lookups._

class TaskModelSpec extends Specification with BeforeEach {

  def before = {
    //DB.executionContext = scala.concurrent.ExecutionContext.global
    DB.dbName = "testAssessory_taskModel"
    scala.concurrent.blocking {
      FuturifySRC.void(DB.db.drop)
    }
  }

  sequential

  "TaskModel" should {

    "Allow users to create tasks in courses" in  {
      DB.dbName = "testAssessory_courseModel"
      DoWiring.doWiring

      val saved = for {
        u <- UserModel.signUp(Some("eg@example.com"), Some("password"), ActiveSession("1234", "127.0.0.1"))
        courseWithPerms <- CourseModel.create(Approval(u.itself), Course(id=invalidId, addedBy=invalidId, title=Some("TestCourse")))

        task <- TaskModel.create(Approval(u.itself), Task(
          id=invalidId, course=courseWithPerms.item.id,
          details=TaskDetails(name=Some("Foo")),
          body=CritiqueTask(
            questionnaire = Seq.empty,
            strategy = OfMyGroupsStrategy
          )
        ))
        retrieved <- task.id.lazily
      } yield retrieved.details.name

      saved.toFuture must beEqualTo(Some("Foo")).await
    }

    "Return the tasks in a course" in  {
      DB.dbName = "testAssessory_courseModel"
      DoWiring.doWiring

      val saved = for {
        u <- UserModel.signUp(Some("eg@example.com"), Some("password"), ActiveSession("1234", "127.0.0.1"))
        courseWithPerms <- CourseModel.create(Approval(u.itself), Course(id=invalidId, addedBy=invalidId, title=Some("TestCourse")))

        task1 <- TaskModel.create(Approval(u.itself), Task(
          id=invalidId, course=courseWithPerms.item.id,
          details=TaskDetails(name=Some("Foo")),
          body=CritiqueTask(
            questionnaire = Seq.empty,
            strategy = OfMyGroupsStrategy
          )
        ))
        task2 <- TaskModel.create(Approval(u.itself), Task(
          id=invalidId, course=courseWithPerms.item.id,
          details=TaskDetails(name=Some("Bar")),
          body=CritiqueTask(
            questionnaire = Seq.empty,
            strategy = OfMyGroupsStrategy
          )
        ))
        retrieved <- TaskModel.courseTasks(Approval(u.itself), courseWithPerms.item.itself)
      } yield retrieved.details.name

      saved.collect.toFuture must beEqualTo(Seq(Some("Foo"), Some("Bar"))).await
    }
  }

}
