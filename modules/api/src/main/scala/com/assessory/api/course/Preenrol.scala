package com.assessory.api.course

import com.wbillingsley.handy._
import Ref._

case class Preenrol (
        
    id:String,
    
    name: Option[String] = None,
    
    course: Ref[Course] = RefNone,
    
    identities: Seq[PreenrolPair] = Seq.empty,
    
    created: Long = System.currentTimeMillis
    
) extends HasStringId

case class PreenrolPair(service:String, id:String, username:String)

object Preenrol {
  
  def fromCsv(id:String, name:Option[String], course: Ref[Course], csv:String):Ref[Preenrol] = {
    import au.com.bytecode.opencsv.CSVReader
    import java.io.StringReader
    
    import scala.collection.JavaConverters._
    
    val reader = new CSVReader(new StringReader(csv.trim()))
    val lines = reader.readAll().asScala.toSeq
    reader.close()
    
    val rSeq = try {
      val seq = for (line <- lines) yield PreenrolPair(service=line(0), id=line(1), username=line(2))
      seq.itself
    } catch {
      case ex:Throwable => RefFailed(ex)
    }
    
    for (seq <- rSeq) yield Preenrol(id, name, course, seq)
  }
  
}