define(["./base"], (l) ->

  Assessory.controllers.course.View = ["$scope", "CourseService", "GroupService", "course", ($scope, CourseService, GroupService, course) ->    

    $scope.course = course
    
    $scope.groupSets = GroupService.courseGroupSets(course.id)
    
    $scope.preenrols = CourseService.coursePreenrols(course.id)

  ]
  
  Assessory.controllers.course.View.resolve = {
    course: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.get($route.current.params.courseId)
    ]
  }  
  
  Assessory.angularApp.directive("courseInfo", [ () -> 
    {
      scope: { course: '=course' }
      templateUrl: "directive_courseInfo.html"
      restrict: 'E'
    }
  )]
  

)