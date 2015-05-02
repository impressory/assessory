package com.assessory.asyncmongo.converters

import com.assessory.api.critique._
import com.wbillingsley.handy.Id
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api._
import org.bson.BsonDocument

import BsonHelpers._

import scala.util.{Failure, Try}

object CritAllocationB extends BsonDocumentConverter[CritAllocation] {
  implicit val taskDetailsB = TaskDetailsB
  implicit val taskBodyB = TaskBodyB
  implicit val allocCritB = AllocatedCritB

  override def write(i: CritAllocation) = bsonDoc(
    "_id" -> i.id,
    "task" -> i.task,
    "completeBy" -> i.completeBy,
    "allocation" -> i.allocation
  )

  override def read(doc: BsonDocument): Try[CritAllocation] = Try {
    new CritAllocation(
      id = doc.req[Id[CritAllocation, String]]("_id"),
      task = doc.req[Id[Task, String]]("task"),
      completeBy = doc.req[Target]("target"),
      allocation = doc.req[Seq[AllocatedCrit]]("allocation")
    )
  }
}

object AllocatedCritB extends BsonDocumentConverter[AllocatedCrit] {
  implicit val taskDetailsB = TaskDetailsB
  implicit val taskBodyB = TaskBodyB

  override def write(i: AllocatedCrit) = bsonDoc(
    "target" -> i.target,
    "critique" -> i.critique
  )

  override def read(doc: BsonDocument): Try[AllocatedCrit] = Try {
    new AllocatedCrit(
      target = doc.req[Target]("target"),
      critique = doc.opt[Id[TaskOutput, String]]("critique")
    )
  }
}
