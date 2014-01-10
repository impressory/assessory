package com.assessory.config

import com.wbillingsley.handy.appbase.DataActionConfig
import com.assessory.reactivemongo._
import com.assessory.api.UserError

object AssessoryDataActionConfig extends DataActionConfig {
    
  def homeAction = com.assessory.play.controllers.Application.index
  
  def errorCodeMap = Map(classOf[UserError] -> 400)
}
