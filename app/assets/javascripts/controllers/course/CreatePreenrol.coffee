define(["./base"], (l) ->

  Assessory.controllers.course.CreatePreenrol = ["$scope", "CourseService", "$location", "course", ($scope, CourseService, $location, course) ->    
    $scope.course = course
    
    $scope.preenrol = {}
    
    $scope.roleChoices = [
      { role : "student", chosen : false },
      { role : "staff", chosen : false }
    ]
    
    $scope.errors = []    
    
    $scope.submit = (preenrol) -> 
      $scope.errors = [ ]
      preenrol.roles = (r.role for r in $scope.roleChoices when r.chosen)
      
      CourseService.createPreenrol(course.id, preenrol).then(
       (gs) -> $location.path("/preenrol/#{gs.id}")           
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]
  
  Assessory.controllers.course.CreatePreenrol.resolve = {
    course: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.get($route.current.params.courseId)
    ]
  }    

)