define(["./base"], (l) ->

  Assessory.controllers.taskoutput.View = ["$scope", "CourseService", "GroupService", "TaskService", "taskoutput", ($scope, CourseService, GroupService, TaskService, taskoutput) ->    

    $scope.taskoutput = taskoutput
    
    $scope.task = TaskService.get(taskoutput.task).then((t) ->
      $scope.course = CourseService.get(t.course)
      t
    )
    
  ]
  
  Assessory.controllers.taskoutput.View.resolve = {
    taskoutput: ['$route', 'TaskOutputService', ($route, TaskOutputService) -> 
      TaskOutputService.get($route.current.params.taskoutputId)
    ]
  }  
  
)