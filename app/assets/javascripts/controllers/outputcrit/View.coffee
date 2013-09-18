#
#  BEWARE: UGLY HACK
#
# As in the short term, there's no prospect of this being reused, the "view" form is coded to show the questionnaire to complete --
# which is rigged to be for an "Output critique".  This would need unpicking and generalising before re-using this (but if we were 
# to reuse it, I'd suggest redesigning the way Tasks work anyway, so there doesn't need to be a separate set of classes for each
# new kind of review task.  (It's just a questionnaire with a different selector and allocation -- we shouldn't need an end-to-end set
# of new classes just because a task selects something different to fill a questionnaire in for.)

define(["./base"], (l) ->

  Assessory.controllers.outputcrit.AllocationsInfo = ["$scope", "CourseService", "TaskService", "TaskOutputService", ($scope, CourseService, TaskService, TaskOutputService) ->    

    $scope.relomap = {}
    myomap = {}

    $scope.relevantOutputs = TaskOutputService.relevantToMe($scope.task.body.taskToCrit).then((output) -> 
      for o in output
        $scope.relomap[o.id] = o
      output
    )
    
    TaskService.get($scope.task.body.taskToCrit).then((t) -> $scope.forTask = t)

    $scope.myOutputs = TaskOutputService.myOutputs($scope.task.id).then((output) -> 
      for o in output
        myomap[o.body.forOutput] = o
    )
    
    $scope.getMyResponseFor = (o) -> 
      m = myomap[o.id]
      if !(m?)
        myomap[o.id] = { task: $scope.task.id, body: { forOutput: o.id, answers: [], kind: "Task output critique" }, permissions: { edit: true } }
        m = myomap[o.id]
      m
    
    
    $scope.selectOutput = (output) ->
      console.log(output.id) 
      $scope.selectedOutput = output
    
    $scope.questionFilter = (q) -> q.kind == "Short text"
    
    $scope.save = (output, finalise) -> 
      $scope.errors = []
      output.finalise = finalise
      TaskOutputService.save(output).then(
        (to) -> angular.copy(to, output)
        (res) -> $scope.errors = [ res.data?.error || 'Unexpected error' ]
      )    

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