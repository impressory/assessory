define(["./base"], (l) ->

  Assessory.controllers.course.View = ["$scope", "CourseService", "course", ($scope, CourseService, course) ->    

    $scope.course = course

  ]
  
  Assessory.controllers.course.View.resolve = {
    course: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.get($route.current.params.courseId)
    ]
  }  

)