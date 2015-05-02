package com.assessory.asyncmongo

import com.assessory.asyncmongo.converters.BsonHelpers._
import com.assessory.asyncmongo.converters.PreenrolmentB
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase._
import com.wbillingsley.handy.mongodbasync.DAO


class PreenrolmentDAO[W,T, R, UT](collName:String)(implicit p:PreenrolmentB[W,T,R,UT])
  extends DAO(DB, classOf[Preenrolment[W,T,R,UT]], collName) {

  private def filterByWithin(id:Id[W, String]) = "within" $eq id

  private def filterByIdentity(service:String, value:Option[String], username:Option[String]) = {
    val s = Seq(
      for (v <- value) yield {
        ("rows.identity.service" $eq service) and ("rows.identity.value" $eq value) and ("rows.identity.used" $eq $null)
      },
      for (u <- username) yield {
        ("rows.identity.service" $eq service) and ("rows.identity.username" $eq username) and ("rows.identity.used" $eq $null)
      }
    )
    or((for (opt <- s; v <- opt) yield v):_*)
  }

  def within(id:Id[W, String]) = findMany(filterByWithin(id))

  def byIdentity(service:String, value:Option[String], username:Option[String]) =  {
    findMany { filterByIdentity(service, value, username) }
  }

  def withinByIdentity(within:Id[W, String], service:String, value:Option[String], username:Option[String]) = {
    findMany { and(
      filterByWithin(within),
      filterByIdentity(service, value, username)
    )}
  }


  def useRow(id:Id[Preenrolment[W,T,R,UT],String], row:Int, ut:Id[UT,String]) = {
    for (
      p <- byId(id.id);
      updated <- {
        updateAndFetch(
          query="_id" $eq id,
          update=bsonDoc("$set" -> bsonDoc(s"identities.$row.used" -> ut))
        )
      }
    ) yield updated
  }


  def useRow(id:Id[Preenrolment[W,T,R,UT],String], service:String, value:Option[String], username:Option[String], ut:Id[UT,String]) = {
    for (
      p <- byId(id.id);
      updated <- {
        val row = p.rows.indexWhere { row =>
          row.identity.service == service && row.used.isEmpty && (
            (username.isDefined && (row.identity.username == username)) ||
              (value.isDefined && (row.identity.value == value))
            )
        }
        updateAndFetch(
          query="_id" $eq id,
          update=bsonDoc("$set" -> bsonDoc(s"identities.$row.used" -> ut))
        )
      }
    ) yield updated
  }

}

object PreenrolmentDAO {
  val course = new PreenrolmentDAO[Course, Course, CourseRole, Course.Reg]("coursePreenrolment")

  val group = new PreenrolmentDAO[GroupSet, Group, GroupRole, Group.Reg]("groupPreenrolment")
}

