package com.assessory.sjsreact

import com.assessory.api.Task
import com.assessory.api.client.WithPerms
import com.assessory.api.critique.{CritiqueTask, Critique}
import com.assessory.sjsreact.services.{CourseService, TaskService, GroupService}
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{Group, Course}
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object TaskViews {

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
        <.p((task.details.description.getOrElse(""):String)),
        <.p(
          "Published:", task.details.published.toString
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
        editOutputForTask(wp.item)
      )
    )
  }

  val viewOutputForTask = ReactComponentB[Task]("viewOutputForTask")
    .render(task =>
    task.body match {
      case c:Critique => <.div("This task can no longer be completed.")
    }
  )


  val editOutputForTask = ReactComponentB[Task]("taskOutputForTask")
    .render(task =>
      task.body match {
        case c:CritiqueTask => CritiqueViews.front(task)
      }
    )
    .build
}

