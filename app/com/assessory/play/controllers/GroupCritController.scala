package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent

import com.assessory.api._
import course._
import group._
import groupcrit._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.DataAction

object GroupCritController extends Controller {

  def refTask(id:String) = new LazyId(classOf[Task], id)
  
  implicit val gcaToJson = GroupCritAllocationToJson
  
  /**
   * Searches for course pre-enrolments, and performs them
   */
  def doPreallocation(rTask:Ref[Task]):RefMany[GroupCritAllocation]= {
    
    val rr = for (
      task <- rTask;
      body <- {
        task.body match {
          case Some(gct:GroupCritTask) => gct.itself;
          case _ => RefFailed(new IllegalStateException("Trying to allocate group crits on a non-group-crit task"))
        }
      };
      distributeSet <- body.groupToCrit;
      distributePR <- distributeSet.preenrol;
      amongSet <- body.withinSet;
      amongPE <- amongSet.preenrol
    ) yield {
      
      val groupDatas = distributePR.groupData      
      if (groupDatas.length == 0) { 
        throw new IllegalStateException("Can't allocate critiques when there are no groups to critique")
      }
      
      val reverseMap = Map((for (gd <- amongPE.groupData; row <- gd.lookups) yield row -> gd):_*)
      
      var cursor = 0
      def pick(row:IdentityLookup):GPreenrol.GroupData = {
        val gd = groupDatas(cursor)
        cursor = cursor + 1
        if (cursor >= groupDatas.length) {
          cursor = 0
        }
        if (
            // Group doesn't contain this person
            (!gd.lookups.contains(row))  &&
            
            (gd.lookups.length > 0) &&
            
            // First member is in the same 'among' set
            reverseMap(gd.lookups.head) == reverseMap(row)
        ) {
          println("returning " + gd.group)
          gd
        } else {
          println("moving on")
          pick(row)
        }
      }
      
      val rows = amongPE.groupData.flatMap(_.lookups)
      
      println(rows)
      
      for (
        marked <- GroupCritAllocationDAO.markTaskAllocated(rTask);
        row <- rows.toRefMany;
        gca = GroupCritAllocation(
          id=GroupCritAllocationDAO.allocateId, task=task.itself, user=RefNone, preallocate=Some(row),
          allocation = for (iter <- 0 until body.number) yield GCAllocatedCrit(group=pick(row).group)
        );
        saved <- GroupCritAllocationDAO.saveNew(gca)
      ) yield saved
    }
    rr.flatten
  }
  
  def allocateTask(taskId:String) = DataAction.returning.many { implicit request =>
    val task = refTask(taskId)
    val allocations = for (t <- task; approved <- request.approval ask Permissions.EditCourse(t.course);
         a <- doPreallocation(t.itself)
    ) yield a
    allocations
  }
  
  def myAllocation(taskId:String) = DataAction.returning.many { implicit request =>
    val task = refTask(taskId)
    for (t <- GroupCritAllocationDAO.byUserAndTask(request.user, task)) yield t
  }
  
  def allocations(taskId:String) = DataAction.returning.many { implicit request =>
    val task = refTask(taskId)
    for (t <- GroupCritAllocationDAO.byTask(task)) yield t
  }
  
}