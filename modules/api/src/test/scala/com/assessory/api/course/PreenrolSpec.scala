package com.assessory.api.course

import org.specs2.mutable._
import com.wbillingsley.handy._
import Ref._
import scala.concurrent.ExecutionContext.Implicits.global
import com.assessory.api._
import org.specs2.specification.BeforeExample
import org.specs2.specification.Step

class PreenrolSpec extends Specification  {
      
  "Preenrol" should {
    
    "create from CSV" in {      
      
      val rp = Preenrol.fromCsv("1", RefNone, """
github, ,fred
github, ,joe
      """.trim())
      
      rp.toFuture must be_==(Some(
        Preenrol("1", RefNone, Seq(
            PreenrolPair("github", " ", "fred"),
            PreenrolPair("github", " ", "joe")
        ))
      )).await
      
    }
  }

}