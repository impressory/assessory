package com.assessory.sjsreact

import com.assessory.api.{Due, Task}
import com.assessory.api.client.WithPerms
import com.assessory.api.critique.{PreallocateGroupStrategy, CritiqueTask, Critique}
import com.assessory.sjsreact.services.{CourseService, TaskService, GroupService}
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.Ids._
import com.wbillingsley.handy.appbase.{Group, Course}
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js.Date

object TaskViews {

  val optDateL = CommonComponent.latchedRender[Option[Long]]("date") {
    case Some(l) => <.span(new Date(l).toLocaleString())
    case _ => <.span("No date")
  }

  val due = ReactComponentB[Due]("Due")
    .initialStateP(due => Latched.lazily(
      for {
        groups <- GroupService.myGroups.request
      } yield {
        val ids = groups.map(_.item.id.id).asIds[Group]
        due.due(ids)
      }
    ))
    .render( (a, b, c) => optDateL(c))
    .build


  val taskAdmin = ReactComponentB[WithPerms[Task]]("TaskInfo")
    .render { wp =>
      if (wp.perms("edit")) {
        wp.item.body match {
          case CritiqueTask(_, p:PreallocateGroupStrategy) =>
            <.div(
              <.a(^.href:=s"/api/critique/${wp.item.id.id}/allocations.csv", " allocations.csv "),
              <.a(^.href:=s"/api/task/${wp.item.id.id}/outputs.csv", " outputs.csv ")
            )
          case _ =>
            <.div(
              <.a(^.href:=s"/api/task/${wp.item.id.id}/outputs.csv", "outputs.csv")
            )
        }
      } else <.div()
    }
    .build

  /**
   * Summary information for a task
   */
  val taskInfo = ReactComponentB[WithPerms[Task]]("TaskInfo")
    .render({ wp =>
      val task = wp.item
      val name = task.details.name.getOrElse("Untitled task")

      <.div(
        <.h3(
          <.a(^.href := MainRouter.taskHome(wp.item.id), name)
        ),
        taskAdmin(wp),
        <.p(task.details.description.getOrElse(""):String),
        if (wp.perms("edit")) <.div() else <.div(
          <.div(^.className:="text-info", "opens: ", due(task.details.open)),
          <.div(^.className:="text-danger", "closes: ", due(task.details.closed)),
          <.p()
        )
      )
    })
    .build


  /**
   * Summary info for each task in a list
   */
  val taskInfoList = CommonComponent.latchedRender[Seq[WithPerms[Task]]]("TaskInfoList") { groups =>
    <.div(
      for { g <- groups } yield taskInfo(g)
    )
  }

  /**
   * Summary info for each task in a course
   */
  val courseTasks = ReactComponentB[Id[Course, String]]("CourseTasks")
    .initialStateP(s => TaskService.courseTasks(s))
    .render { (a, b, c) => taskInfoList(c) }
    .build

  /**
   * View for a particular task (allowing the user to do it)
   */
  val taskView = CommonComponent.latchedRender[WithPerms[Task]]("TaskView") { wp =>
    <.div(
      Front.siteHeader(""),

      <.div(^.className := "container",
        CourseViews.courseInfoL(CourseService.latch(wp.item.course)),
        taskInfo(wp),
        if (wp.perms("complete")) {
          editOutputForTask(wp.item)
        } else {
          viewOutputForTask(wp.item)
        }
      )
    )
  }

  val viewOutputForTask = ReactComponentB[Task]("viewOutputForTask")
    .render(task =>
      task.body match {
        case c:CritiqueTask => <.div("Sorry, this task doesn't appear to be open. (If you think it should be open, try refreshing the page -- maybe it's opened since I cached it.)")
      }
    ).build


  val editOutputForTask = ReactComponentB[Task]("taskOutputForTask")
    .render(task =>
      task.body match {
        case c:CritiqueTask => CritiqueViews.front(task)
      }
    )
    .build
}

