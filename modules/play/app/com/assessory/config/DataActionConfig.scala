package com.assessory.config

import com.wbillingsley.handyplay.DataActionConfig
import com.assessory.reactivemongo._
import com.assessory.api.UserError

object AssessoryDataActionConfig extends DataActionConfig {

  val redirectHtmlRequests = false
    
  def homeAction = com.assessory.play.controllers.Application.index
  
  def errorCodeMap = Map(classOf[UserError] -> 400)
}
