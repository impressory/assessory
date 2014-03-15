package com.assessory.play

import com.assessory.reactivemongo._

package object controllers {

  /**
   * DataAction should retrieve user information using the UserDAO
   * from our database classes. (It includes methods for bySessionKey, etc) 
   */
  implicit val userProvider = UserDAO


  implicit val dataActionConfig = com.assessory.config.AssessoryDataActionConfig 
  
}