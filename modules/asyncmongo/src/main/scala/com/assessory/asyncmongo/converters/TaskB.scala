package com.assessory.asyncmongo.converters

import com.wbillingsley.handy.Id
import com.wbillingsley.handy.appbase.{GroupSet, Group, Course}
import com.wbillingsley.handy.mongodbasync.BsonDocumentConverter
import com.assessory.api._
import org.bson.BsonDocument

import BsonHelpers._

import scala.util.{Failure, Try}

object TaskB extends BsonDocumentConverter[Task] {
  implicit val taskDetailsB = TaskDetailsB
  implicit val taskBodyB = TaskBodyB

  override def write(i: Task) = bsonDoc(
    "_id" -> i.id,
    "course" -> i.course,
    "details" -> i.details,
    "body" -> i.body
  )

  override def read(doc: BsonDocument): Try[Task] = Try {
    new Task(
      id = doc.req[Id[Task, String]]("_id"),
      course = doc.req[Id[Course, String]]("course"),
      details = doc.req[TaskDetails]("details"),
      body = doc.req[TaskBody]("body")
    )
  }
}

object TaskDetailsB extends BsonDocumentConverter[TaskDetails] {
  implicit val dueB = DueB

  override def write(i: TaskDetails) = bsonDoc(
    "name" -> i.name,
    "description" -> i.description,
    "groupSet" -> i.groupSet,
    "individual" -> i.individual,
    "published" -> i.published,
    "open" -> i.open,
    "due" -> i.due,
    "closed" -> i.closed,
    "created" -> i.created
  )

  override def read(doc: BsonDocument): Try[TaskDetails] = Try {
    new TaskDetails(
      name = doc.opt[String]("name"),
      description = doc.opt[String]("description"),
      groupSet = doc.opt[Id[GroupSet,String]]("groupSet"),
      individual = doc.opt[Boolean]("individual").getOrElse(true),
      published = doc.req[Due]("published"),
      open = doc.req[Due]("open"),
      due = doc.req[Due]("due"),
      closed = doc.req[Due]("closed"),
      created = doc.req[Long]("created")
    )
  }
}

object DueB extends BsonDocumentConverter[Due] {
  override def write(i: Due) = i match {
    case d:DueDate => DueDateB.write(d)
    case d:DuePerGroup => DuePerGroupB.write(d)
    case NoDue => NoDueB.write(NoDue)
  }

  override def read(doc: BsonDocument): Try[Due] = {
    doc.req[String]("kind") match {
      case DueDate.kind => DueDateB.read(doc)
      case DuePerGroup.kind => DuePerGroupB.read(doc)
      case NoDue.kind => NoDueB.read(doc)
      case k => Failure(new IllegalStateException("Couldn't parse due with kind " + k))
    }
  }
}

object DueDateB extends BsonDocumentConverter[DueDate] {
  override def write(i: DueDate) = bsonDoc(
    "kind" -> i.kind,
    "time" -> i.time
  )

  override def read(doc: BsonDocument): Try[DueDate] = Try {
    new DueDate(
      time = doc.req[Long]("time")
    )
  }
}

object NoDueB extends BsonDocumentConverter[NoDue.type] {
  override def write(i: NoDue.type) = bsonDoc(
    "kind" -> i.kind
  )

  override def read(doc: BsonDocument): Try[NoDue.type] = Try {
    NoDue
  }
}

object DuePerGroupB extends BsonDocumentConverter[DuePerGroup] {
  implicit val mapB = new MapB[Id[Group,String], Long]

  override def write(i: DuePerGroup) = bsonDoc(
    "kind" -> i.kind,
    "times" -> i.times
  )

  override def read(doc: BsonDocument): Try[DuePerGroup] = Try {
    new DuePerGroup(
      times = doc.req[Map[Id[Group, String], Long]]("times")
    )
  }
}

class MapB[K,V](implicit ks: ToFromStringKey[K], vb:ToFromBson[V]) extends BsonDocumentConverter[Map[K,V]] {
  override def write(i: Map[K,V]) = {
    bsonDoc(
      (for ((k, v) <- i.toSeq) yield ks.toSK(k) -> vb.toBson(v)):_*
    )
  }

  override def read(doc: BsonDocument): Try[Map[K,V]] = Try {
    import scala.collection.JavaConverters._

    def seq:Seq[(K,V)] = for {
      e <- doc.entrySet().asScala.toSeq
    } yield ks.fromSK(e.getKey) -> vb.fromBson(e.getValue)

    Map(seq:_*)
  }
}
