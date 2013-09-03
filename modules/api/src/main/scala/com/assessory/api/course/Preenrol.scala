package com.assessory.api.course

import com.wbillingsley.handy.{Ref, RefNone, HasStringId}

case class Preenrol (
        
    id:String,
    
    course: Ref[Course] = RefNone,
    
    identities: Seq[PreenrolPair] = Seq.empty
    
) extends HasStringId

case class PreenrolPair(service:String, value:String)