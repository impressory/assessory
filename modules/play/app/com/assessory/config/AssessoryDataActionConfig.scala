package com.assessory.config

import com.wbillingsley.handy.appbase.UserError
import com.wbillingsley.handyplay.DataActionConfig
import play.api.mvc.{Action, Results}

object AssessoryDataActionConfig extends DataActionConfig {

  val redirectHtmlRequests = false

  override def onNotFound = Action(Results.NotFound("Not found"))

  def homeAction = com.assessory.play.controllers.Application.index

  def errorCodeMap = Map(
    classOf[UserError] -> 400,
    classOf[NoSuchElementException] -> 404
  )
}
