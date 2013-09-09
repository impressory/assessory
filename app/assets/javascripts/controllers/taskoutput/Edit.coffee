define(["./base"], (l) ->

  Assessory.controllers.taskoutput.Edit = ["$scope", "$location", "CourseService", "TaskOutputService", "TaskService", "taskoutput", ($scope, $location, CourseService, TaskOutputService, TaskService, taskoutput) ->    

    $scope.orginalOutput = taskoutput

    $scope.taskoutput = angular.copy(taskoutput)
    
    $scope.task = TaskService.get(taskoutput.task).then((t) ->
      $scope.course = CourseService.get(t.course)
      t
    )
    
    $scope.save = (finalise) -> 
      $scope.errors = []
      $scope.taskoutput.finalise = finalise
      TaskOutputService.updateBody($scope.taskoutput).then(
        (to) -> $location.path("/taskoutput/#{$scope.taskoutput.id}")
        (res) -> $scope.errors = [ res.data?.error || 'Unexpected error' ]
      )
    
  ]
  
  Assessory.controllers.taskoutput.Edit.resolve = {
    taskoutput: ['$route', 'TaskOutputService', ($route, TaskOutputService) -> 
      TaskOutputService.get($route.current.params.taskoutputId)
    ]
  }  
  
)