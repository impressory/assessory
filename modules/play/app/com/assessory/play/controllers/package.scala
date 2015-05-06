package com.assessory.play

import com.assessory.asyncmongo._
import com.wbillingsley.handyplay.HeaderInfo
import com.wbillingsley.handy._
import Ref._
import play.api.Play
import play.api.libs.iteratee.Enumerator

import scala.concurrent.ExecutionContext

package object controllers {

  /**
   * DataAction should retrieve user information using the UserDAO
   * from our database classes. (It includes methods for bySessionKey, etc)
   */
  implicit val userProvider = UserDAO


  implicit val dataActionConfig = com.assessory.config.AssessoryDataActionConfig

  import play.api.Play.current
  val uiBaseUrl = Play.configuration.getString("ui.baseurl").getOrElse("*")

  /**
   * Support CORS. At the moment, we support it from anywhere.
   * TODO: Limit allowed origins
   */
  val headerInfo = HeaderInfo(headers=Seq(
    "Access-Control-Allow-Origin" -> uiBaseUrl,
    "Access-Control-Allow-Credentials" -> "true"
  )).itself

  implicit class StringifyJson(val en: Enumerator[String]) extends AnyVal {
    def stringifyJsArr = Enumerator("[").andThen({
      var sep = ""
      for (j <- en) yield {
        val s = sep + j
        sep = ","
        s
      }
    }).andThen(Enumerator("]"))
  }
}
