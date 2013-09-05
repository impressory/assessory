define(["./base"], (l) -> 


  Assessory.controllers.components.MyCourses = ['$scope', 'CourseService', ($scope, CourseService) ->
  
    $scope.refreshMyCourses = () ->
      $scope.courses = CourseService.my()
      
    $scope.refreshMyCourses()

  ]

)