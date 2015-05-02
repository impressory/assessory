package com.assessory.api

import com.wbillingsley.handy.Id

package object client {

  /**
   * When new objects are sent from the client, the client does not know the ID to set.
   * This generates an "invalid" ID that the server can replace
   * @tparam T
   * @return
   */
  def invalidId[T] = Id[T,String]("invalid")

}
