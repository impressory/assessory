package com.assessory.api.course

import com.wbillingsley.handy._
import Ref._
import com.assessory.api._

case class Preenrol (
        
    id:String,
    
    name: Option[String] = None,
    
    roles: Set[CourseRole.T] = Set(CourseRole.student),
    
    course: RefWithId[Course] = RefNone,
    
    identities: Seq[IdentityLookup] = Seq.empty,
    
    created: Long = System.currentTimeMillis
    
) extends HasStringId


object Preenrol {
  
  def fromCsv(id:String, name:Option[String], roles:Set[CourseRole.T], course: RefWithId[Course], csv:String):Ref[Preenrol] = {
    import au.com.bytecode.opencsv.CSVReader
    import java.io.StringReader
    
    import scala.collection.JavaConverters._
    
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq
    reader.close()
    
    val rSeq = try {
      val seq = for (line <- lines) yield IdentityLookup(service=line(0), value=Option(line(1)).filter(_.trim.nonEmpty), username=Option(line(2)).filter(_.trim.nonEmpty))
      seq.itself
    } catch {
      case ex:Throwable => RefFailed(ex)
    }
    
    for (seq <- rSeq) yield Preenrol(id, name, roles, course, seq)
  }
  
}