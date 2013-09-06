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

  
  /**
   * Searches for course pre-enrolments, and performs them
   *
  def doPreallocation(rTask:Ref[Task], gct:GroupCritTask, rDistribute:Ref[GroupSet], rAmong:Ref[GroupSet]):RefMany[Group]= {
    
    for (
      task <- rTask; 
      distributeSet <- rDistribute;
      distributePR <- distributeSet.preenrol;
      amongSet <- rAmong;
      amongPE <- amongSet.preenrol
    ) {
      
      val groups = (for (row <- distributePR.groupData; id <- row.group.getId) yield id).toSet.toSeq      
      if (groups.length == 0) { 
        throw new IllegalStateException("Can't allocate critiques when there are no groups to critique")
      }
      
      val rows = amongPE.groupData
      
      var cursor = 0
      
      
      
      
    }
  }  */
  
}