define(["./base"], (l) -> 


  Assessory.controllers.components.MyCourses = ['$scope', 'CourseService', ($scope, CourseService) ->
  
    $scope.courses = CourseService.my()

  ]

)