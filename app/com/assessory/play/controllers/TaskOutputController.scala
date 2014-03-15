package com.assessory.play.controllers


import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent
import com.assessory.api._
import course._
import group._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.DataAction
import com.assessory.api.groupcrit._
import play.api.libs.iteratee.Enumerator
import com.assessory.api.outputcrit._


object TaskOutputController extends Controller {

  def refOutput(id:String) = LazyId(id).of[TaskOutput]
  def refCourse(id:String) = LazyId(id).of[Course]
  def refTask(id:String) = LazyId(id).of[Task]
  
  implicit val toToJSON = TaskOutputToJson
  
  def get(id:String) = DataAction.returning.one { implicit request => 
    refOutput(id)
  }
  
  def relevantToMe(taskId:String) = DataAction.returning.many { implicit request => 
    val rTask = refTask(taskId)
    for (
      task <- rTask;
      to <- TaskOutputDAO.relevantTo(task, request.user)
    ) yield to
  }
  
  def myOutputs(taskId:String) = DataAction.returning.many { implicit request =>
    val rTask = refTask(taskId)
    for (
      task <- rTask;
      to <- TaskOutputDAO.byTaskAndUser(task.itself, request.user)
    ) yield to
  }
  
  def create(taskId:String) = DataAction.returning.one(parse.json) { implicit request =>
    val rTask = refTask(taskId)
    for (
      u <- request.user;
      to = TaskOutputDAO.unsaved.copy(byUser=u.itself, task=rTask);
      task <- rTask;
      approved <- request.approval ask Permissions.ViewCourse(task.course);
      saved <- { 
        val updated = TaskOutputToJson.update(to, request.body);
        TaskOutputDAO.saveNew(updated) 
      };
      finalised <- if ((request.body \ "finalise").asOpt[Boolean].getOrElse(false)) {
        // Finalise the task output
        TaskOutputDAO.finalise(saved)
      } else {
        // Don't finalise it; just return the saved item
        saved.itself
      }
    ) yield finalised
  }
  
  def updateBody(id:String) = DataAction.returning.json(parse.json) { implicit request => 
    for (
      output <- refOutput(id);
      approved <- request.approval ask Permissions.EditOutput(output.itself);
      updated = TaskOutputToJson.update(output, request.body);
      saved <- TaskOutputDAO.updateBody(updated);
      finalised <- if ((request.body \ "finalise").asOpt[Boolean].getOrElse(false)) {
        // Finalise the task output
        TaskOutputDAO.finalise(saved)
      } else {
        // Don't finalise it; just return the saved item
        saved.itself
      };
      j <-TaskOutputToJson.toJsonFor(finalised, new Approval(request.user))
    ) yield j
  }
  
  def asCsv(taskId:String) = DataAction.returning.result { implicit request => 
    val taskR = refTask(taskId) 
    val header = for (
      task <- taskR;
      approved <- request.approval ask Permissions.EditCourse(task.course);
      h <- task.body match {
        case Some(gct:GroupCritTask) => {
          val qs =  gct.questionnaire.questions.map { q => "\"" + q.prompt.replace("\"", "\"\"") + "\"," }
          (qs.fold("student, group, ")(_ + _) + "\n").itself
        }
        case Some(oct:OutputCritTask) => {
          val qs =  oct.questionnaire.questions.map { q => "\"" + q.prompt.replace("\"", "\"\"") + "\"," }
          (qs.fold("student, crit by, crit for group,")(_ + _) + "\n").itself
        }
        case _ => RefFailed(new IllegalStateException("Unknown task body type"))
      }
    ) yield h
    
    val body = for (
      h <- header;
      output <- TaskOutputDAO.byTask(taskR);
      line <- output.body match {
        case Some(gc:GCritique) => {
          for (
            user <- request.approval.cache(output.byUser);
            userName = user.nickname.getOrElse("Anonymous");
            group <- request.approval.cache(gc.forGroup);
            groupName = group.name.getOrElse("Unnamed group")
          ) yield {
            val as = gc.answers.map { a => "\"" + a.answerAsString.replace("\"", "\"\"") + "\"," }
            val unr = userName.replace("\"","\"\"")
            val gnr = groupName.replace("\"","\"\"")
            as.fold("\"" + unr + "\",\"" + gnr + "\",")(_ + _) + "\n"
          }
        }
        case Some(oct:OCritique) => {
          for (
            user <- request.approval.cache(output.byUser);
            userName = user.nickname.getOrElse("Anonymous");
            output <- request.approval.cache(oct.forOutput);
            otherUser <- request.approval.cache(output.byUser);
            otherUserName = otherUser.nickname.getOrElse("Anonymous");
            gc <- output.body match {
              case Some(gc:GCritique) => gc.itself
              case _ => RefFailed(new IllegalStateException("Wasn't critiquing a group crit"))
            };
            group <- request.approval.cache(gc.forGroup);
            groupName = group.name.getOrElse("Unnamed group")
          ) yield {
            val as = oct.answers.map { a => "\"" + a.answerAsString.replace("\"", "\"\"") + "\"," }
            val unr = userName.replace("\"","\"\"")
            val ounr = otherUserName.replace("\"","\"\"")
            val gnr = groupName.replace("\"","\"\"")
            as.fold("\"" + unr + "\",\"" + ounr + "\",\"" + gnr + "\",")(_ + _) + "\n"
          }
        }        
        case _ => RefFailed(new IllegalStateException("Unknown task output body type"))
      }
    ) yield line
      
    val enum = for (h <- header) yield {
      import com.wbillingsley.handyplay.RefConversions._
      import play.api.libs.concurrent.Execution.Implicits.defaultContext
      val en = Enumerator(h) andThen body.enumerate
      Ok.chunked(en)
    }
    enum
  }
  
}