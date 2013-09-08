package com.assessory.play.controllers

import play.api.mvc.{Action, Controller}
import com.assessory.reactivemongo._
import com.assessory.play.json._
import play.api.mvc.AnyContent
import com.assessory.api._
import course._
import group._
import groupcrit._
import question._
import com.wbillingsley.handy._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy.appbase.DataAction
import com.assessory.reactivemongo.GroupCritAllocationDAO

object GroupCritController extends Controller {

  def refTask(id:String) = new LazyId(classOf[Task], id)
  def refAlloc(id:String) = new LazyId(classOf[GroupCritAllocation], id)
  def refGroup(id:String) = new LazyId(classOf[Group], id)
  
  implicit val gcaToJson = GroupCritAllocationToJson
  implicit val toToJSON = TaskOutputToJson
  
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
  
  def doPreallocation(t:Ref[Task], user:Ref[User]) = {
    for (
      u <- user;
      i <- u.identities.toRefMany;
      r <- GroupCritAllocationDAO.useRow(t, u.itself, service=i.service, value=Some(i.value), username=i.username)
    ) yield r
  }
  
  def myAllocation(taskId:String) = DataAction.returning.many { implicit request =>
    val task = refTask(taskId)
    for (
      done <- optionally(doPreallocation(task, request.user).toRefOne);
      t <- GroupCritAllocationDAO.byUserAndTask(request.user, task)
    ) yield t
  }
  
  def allocations(taskId:String) = DataAction.returning.many { implicit request =>
    val task = refTask(taskId)
    for (t <- GroupCritAllocationDAO.byTask(task)) yield t
  }
  
  def createCritFor(allocId:String, groupId:String) = DataAction.returning.one { implicit request => 
    val alloc = refAlloc(allocId)
    val group = refGroup(groupId)
    
    for (
      allocation <- alloc;
      approved <- request.approval ask Permissions.WriteCritique(allocation.itself);
      g <- group;
      task <- allocation.task;
      taskBody <- task.body match {
        case Some(gc:GroupCritTask) => gc.itself
        case _ => RefFailed(new IllegalStateException("This was not a group critique task"))
      };
      unsaved = TaskOutputDAO.unsaved.copy(
        byUser = request.user,
        attnGroups = new RefManyById(classOf[Group], Seq(g.id)),
        attnUsers = g.members,
        body = Some(GCritique(forGroup = g.itself, answers={
          for (q <- taskBody.questionnaire.questions) yield {
            q match {
              case s:ShortTextQuestion => ShortTextAnswer(question=Some(s.id), answer=None)
              case t:TickBoxQuestion => TickBoxAnswer(question=Some(t.id), answer=None)
              case i:IntegerQuestion => IntegerAnswer(question=Some(i.id), answer=None)
            }
          }
        }))
      );
      saved <- TaskOutputDAO.saveNew(unsaved);
      updatedAlloc <- GroupCritAllocationDAO.setOutput(allocation.itself, g.itself, saved.itself)
    ) yield saved
  }
  
  /** Fetches allocations as a CSV. */
  def allocationsAsCSV(taskId:String) = DataAction.returning.result { implicit request =>
    val task = refTask(taskId)
    for (t <- task; approved <- request.approval ask Permissions.EditCourse(t.course)) yield {
      val line = for (
        alloc <- GroupCritAllocationDAO.byTask(task);
        prealloc <- Ref(alloc.preallocate);
        username <- Ref(prealloc.username)
      ) yield {
      
        val groups = for (
                       allocation <- alloc.allocation.toRefMany;
                       g <- request.approval.cache(allocation.group, classOf[Group]);
                       name <- Ref(g.name)
                     ) yield name
      
        val string = groups.fold(username)((sofar, gn) => sofar + ","+gn)
        for (s <- string) yield s + "\n"
      }
    
      import com.wbillingsley.handyplay.RefConversions._
      val en = line.flatten.enumerate
        
      Ok.stream(en)
    }
  }
}