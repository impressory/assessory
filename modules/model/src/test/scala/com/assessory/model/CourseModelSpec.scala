package com.assessory.model

import com.assessory.api.client.invalidId
import com.assessory.asyncmongo.DB
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.Course
import com.wbillingsley.handy.mongodbasync.FuturifySRC
import com.wbillingsley.handy.user.ActiveSession
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach


class CourseModelSpec extends Specification with BeforeEach {

  def before = {
    //DB.executionContext = scala.concurrent.ExecutionContext.global
    DB.dbName = "testAssessory_courseModel"
    scala.concurrent.blocking {
      FuturifySRC.void(DB.db.drop)
    }
  }

  sequential

  "CourseModel" should {

    "Allow users to create courses" in  {
      DoWiring.doWiring

      val myCoursesAfterSignup = for {
        u <- UserModel.signUp(Some("eg@example.com"), Some("password"), ActiveSession("1234", "127.0.0.1"))
        courseWithPerms <- CourseModel.create(Approval(u.itself), Course(id=invalidId, addedBy=invalidId, title=Some("TestCourse")))
        course <- CourseModel.myCourses(u.itself)
      } yield course.title

      myCoursesAfterSignup.collect.toFuture must beEqualTo(Seq(Some("TestCourse"))).await
    }


  }

}
