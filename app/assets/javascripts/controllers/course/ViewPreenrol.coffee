define(["./base"], (l) ->

  Assessory.controllers.course.ViewPreenrol = ["$scope", "CourseService", "$location", "preenrol", ($scope, CourseService, $location, preenrol) ->    
    $scope.preenrol = preenrol
    
    $scope.errors = []    
    
  ]
  
  Assessory.controllers.course.ViewPreenrol.resolve = {
    preenrol: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.getPreenrol($route.current.params.preenrolId)
    ]
  }    

)