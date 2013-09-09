define(["./base"], (l) ->

  Assessory.controllers.taskoutput.Edit = ["$scope", "CourseService", "GroupService", "TaskService", "taskoutput", ($scope, CourseService, GroupService, TaskService, taskoutput) ->    

    $scope.taskoutput = angular.copy(taskoutput)
    
    $scope.task = TaskService.get(taskoutput.task).then((t) ->
      $scope.course = CourseService.get(t.course)
      t
    )
    
  ]
  
  Assessory.controllers.taskoutput.Edit.resolve = {
    taskoutput: ['$route', 'TaskOutputService', ($route, TaskOutputService) -> 
      TaskOutputService.get($route.current.params.taskoutputId)
    ]
  }  
  
)