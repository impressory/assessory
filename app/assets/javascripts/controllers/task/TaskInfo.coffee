#
# Controller for sign-up form.
#
define(["./base"], (l) ->


  Assessory.controllers.task.TaskInfo = ["$scope", "TaskService",  ($scope, TaskService) ->    
    
    
    
  ]

  Assessory.angularApp.directive("taskInfo", [ () -> 
    {
      scope: { task: '=task' }
      controller: Assessory.controllers.task.TaskInfo
      templateUrl: "directive_taskInfo.html"
      restrict: 'E'
    }
  ])
)