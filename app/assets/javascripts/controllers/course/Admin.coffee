define(["./base"], (l) ->

  Assessory.controllers.course.Admin = ["$scope", "CourseService", "GroupService", "TaskService", "course", ($scope, CourseService, GroupService, TaskService, course) ->    

    $scope.course = course
    
    $scope.groupSets = GroupService.courseGroupSets(course.id)
    
    $scope.preenrols = CourseService.coursePreenrols(course.id)
    
    $scope.tasks = TaskService.courseTasks(course.id)

  ]
  
    
  Assessory.controllers.course.Admin.resolve = {
    course: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.get($route.current.params.courseId)
    ]
  }  
  
)