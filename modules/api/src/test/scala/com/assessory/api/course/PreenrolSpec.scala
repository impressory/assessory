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
      
      val rp = Preenrol.fromCsv("1", Some("1"), Set(CourseRole.student), RefNone, """
github, ,fred
github, ,joe
      """.trim())
      
      val rcsv = for (p <- rp) yield p.identities
      
      rcsv.toFuture must be_==(Some(
        Seq(
            PreenrolPair("github", " ", "fred"),
            PreenrolPair("github", " ", "joe")
        )
      )).await
      
    }
  }

}