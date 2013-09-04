define(["./base"], (l) ->

  Assessory.controllers.course.Create = ["$scope", "CourseService", "$location", ($scope, CourseService, $location) ->    
    $scope.course = { }
    
    $scope.errors = []    
    
    $scope.submit = (course) -> 
      $scope.errors = [ ]
      CourseService.create(course).then(
       (course) -> $location.path("/course/#{course.id}")           
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]

)