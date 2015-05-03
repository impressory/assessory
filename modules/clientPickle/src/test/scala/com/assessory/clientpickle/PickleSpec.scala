package com.assessory.model

import com.assessory.api.critique.{OfMyGroupsStrategy, CritiqueTask}
import com.assessory.api.{DueDate, TaskDetails, Task}
import com.assessory.clientpickle.Pickles
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.Id._
import com.wbillingsley.handy.Ids._
import com.wbillingsley.handy.appbase._
import org.specs2.mutable.Specification

import Pickles._
import com.assessory.api.client.invalidId

class PickleSpec extends Specification {

  "Pickles" should {
    "Pickle and unpickle Id" in  {
      val id = "myTest".asId[User]
      val pickled = upickle.write(id)
      val unpickled = upickle.read[Id[User,String]](pickled)

      unpickled must beEqualTo(id)
    }

    "Pickle and unpickle Question" in  {
      val made:Seq[Question] = Seq(
        ShortTextQuestion(id=invalidId, prompt="Hello world"),
        BooleanQuestion(id=invalidId, prompt="Hello world")
      )
      val pickled = upickle.write(made)
      val unpickled = upickle.read[Seq[Question]](pickled)

      unpickled must beEqualTo(made)
    }

    "Pickle and unpickle Course" in  {
      val made = Course(
        id = invalidId,
        addedBy = invalidId,
        title = Some("test course")
      )
      val pickled = upickle.write(made)
      val unpickled = upickle.read[Course](pickled)

      unpickled must beEqualTo(made)
    }

    "Pickle and unpickle GroupSet" in  {
      val made = GroupSet(
        id = invalidId,
        course = invalidId,
        name = Some("test group")
      )
      val pickled = upickle.write(made)
      val unpickled = upickle.read[GroupSet](pickled)
      unpickled must beEqualTo(made)
    }

    "Pickle and unpickle Group" in  {
      val made = Group(
        id = invalidId,
        set = invalidId,
        name = Some("test group"),
        members = Seq("1", "2", "3").asIds
      )
      val pickled = upickle.write(made)
      val unpickled = upickle.read[Group](pickled)
      unpickled must beEqualTo(made)
    }

    "Pickle and unpickle Task" in  {
      val made = Task(
        id = invalidId,
        course = invalidId,
        details = TaskDetails(
          name = Some("test crit"),
          description = Some("This is to test critiques can be sent between server and client"),
          published = DueDate(System.currentTimeMillis())
        ),
        body = CritiqueTask(
          questionnaire = Seq(
            ShortTextQuestion(id=invalidId, prompt="Hello world"),
            BooleanQuestion(id=invalidId, prompt="Hello world")
          ),
          strategy = OfMyGroupsStrategy
        )
      )
      val pickled = upickle.write(made)
      val unpickled = upickle.read[Task](pickled)

      unpickled must beEqualTo(made)
    }
  }

}
