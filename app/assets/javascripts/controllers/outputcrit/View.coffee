define(["./base"], (l) ->

  Assessory.controllers.outputcrit.AllocationsInfo = ["$scope", "CourseService", "TaskService", "TaskOutputService", ($scope, CourseService, TaskService, TaskOutputService) ->    

    $scope.relomap = {}
    $scope.myomap = {}

    $scope.relevantOutputs = TaskOutputService.relevantToMe($scope.task.body.taskToCrit).then((output) -> 
      for o in output
        $scope.relomap[o.id] = o
        $scope.myomap[o.id] = {}
      output
    )
    
    TaskService.get($scope.task.body.taskToCrit).then((t) -> $scope.forTask = t)

    $scope.myOutputs = TaskOutputService.myOutputs($scope.task.id).then((output) -> 
      for o in output
        $scope.myomap[o.forOutput] = o
    )
    
    $scope.selectOutput = (output) ->
      console.log(output.id) 
      $scope.selectedOutput = output
    
    $scope.questionFilter = (q) -> q.kind == "Short text"

  ]

  
  Assessory.angularApp.directive("taskOutputCritiqueAllocations", [ () -> 
    {
      scope: { task: '=task' }
      controller: Assessory.controllers.outputcrit.AllocationsInfo      
      templateUrl: "directive_outputCritAllocations.html"
      restrict: 'E'
    }
  ])  
    
)