package com.assessory.api.group

import com.wbillingsley.handy.{Ref, RefNone, RefFailed, HasStringId}
import Ref._
import com.assessory.api.course.Course

case class GPreenrol (

    id: String, 
    
    course: Ref[Course] = None,
    
    set: Ref[GroupSet] = None,
    
    groupData: Seq[GPreenrolPair] = Seq.empty,
    
    created: Long = System.currentTimeMillis

) extends HasStringId

case class GPreenrolPair(groupName:String, service:String, value:String, username:String, used:Boolean = false)

object GPreenrol {
  
  def fromCsv(id:String, course: Ref[Course], set:Ref[GroupSet], csv:String, created:Long):Ref[GPreenrol] = {
    import au.com.bytecode.opencsv.CSVReader
    import java.io.StringReader
    
    import scala.collection.JavaConverters._
    
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq
    reader.close()
    
    val rSeq = try {
      val seq = for (line <- lines) yield GPreenrolPair(groupName=line(0), service=line(1), value=line(2), username=line(3))
      seq.itself
    } catch {
      case ex:Throwable => RefFailed(ex)
    }
    
    for (seq <- rSeq) yield GPreenrol(id, course, set, seq, created)
  }
  
}
