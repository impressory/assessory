package com.assessory.api.question

import com.wbillingsley.handy.HasStringId

trait Question extends HasStringId {

  val kind: String
  
  val prompt:String
  
  val required:Boolean
  
  /**
   * As questions might have already been answered, we don't delete them --
   * only mark them as inactive.
   */
  val active:Boolean

  def blankAnswer: Answer
  
} 