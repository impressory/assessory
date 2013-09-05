define(["./base"], (l) ->

  Assessory.controllers.course.FindMyCourses = ["$scope", "CourseService", "UserService", "$route", ($scope, CourseService, UserService, $route) ->    

    $scope.findMyCourses = () -> 
      CourseService.doPreenrolments().then((d) ->
        $scope.refreshMyCourses()
      )

  ]

)