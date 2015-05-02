package com.assessory.model

import com.assessory.api.wiring.Lookups
import com.assessory.asyncmongo._

object DoWiring {

  def doWiring = {
    Lookups.luCourse = CourseDAO.lookUp
    Lookups.luCReg = RegistrationDAO.course.lookUp
    Lookups.luCritAlloc = CritAllocationDAO.lookUp
    Lookups.luGPreenrol = PreenrolmentDAO.group.lookUp
    Lookups.luGReg = RegistrationDAO.group.lookUp
    Lookups.luGroup = GroupDAO.lookUp
    Lookups.luGroupSet = GroupSetDAO.lookUp
    Lookups.luPreenrol = PreenrolmentDAO.course.lookUp
    Lookups.luTask = TaskDAO.lookUp
    Lookups.luTaskOutput = TaskOutputDAO.lookUp
    Lookups.luUser = UserDAO.lookUp

    Lookups.courseRegistrationProvider = RegistrationDAO.course
  }

}
