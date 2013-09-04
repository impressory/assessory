define(["./base"], (l) ->

  Assessory.controllers.group.CreateGroupSet = ["$scope", "GroupService", "$location", "course", ($scope, GroupService, $location, course) ->    
    $scope.groupSet = { }
    
    $scope.errors = []    
    
    $scope.submit = (groupSet) -> 
      $scope.errors = [ ]
      GroupService.createGroupSet(course.id, groupSet).then(
       (gs) -> $location.path("/groupSet/#{gs.id}")           
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]
  
  Assessory.controllers.group.CreateGroupSet.resolve = {
    course: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.get($route.current.params.courseId)
    ]
  }    

)