define(["./base"], (l) ->

  Assessory.controllers.course.Admin = ["$scope", "CourseService", "GroupService", "course", ($scope, CourseService, GroupService, course) ->    

    $scope.course = course
    
    $scope.groupSets = GroupService.courseGroupSets(course.id)
    
    $scope.preenrols = CourseService.coursePreenrols(course.id)

  ]
  
    
  Assessory.controllers.course.Admin.resolve = {
    course: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.get($route.current.params.courseId)
    ]
  }  
  
)