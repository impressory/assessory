package com.assessory.config

import com.wbillingsley.handy.appbase.UserError
import com.wbillingsley.handyplay.DataActionConfig

object AssessoryDataActionConfig extends DataActionConfig {

  val redirectHtmlRequests = false

  def homeAction = com.assessory.play.controllers.Application.index

  def errorCodeMap = Map(classOf[UserError] -> 400)
}
