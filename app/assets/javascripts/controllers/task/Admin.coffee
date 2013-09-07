define(["./base"], (l) ->

  Assessory.controllers.task.Admin = ["$scope", "CourseService", "GroupService", "TaskService", "task", ($scope, CourseService, GroupService, TaskService, task) ->    

    $scope.task = task
    
    $scope.course = CourseService.get(task.course)
    
  ]
  
  Assessory.controllers.task.Admin.resolve = {
    task: ['$route', 'TaskService', ($route, TaskService) -> 
      TaskService.get($route.current.params.taskId)
    ]
  }  
  
)