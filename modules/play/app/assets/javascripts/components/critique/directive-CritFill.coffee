define [ "./module" ], () ->

  # Filling out critiques
  controller = [ "$scope", "TaskOutputService", "CritiqueService", ($scope, TaskOutputService, CritiqueService) ->

    $scope.saveMsg = []

    $scope.$watch("target", (nv) ->
      $scope.taskOutput = null
      $scope.promise = null
      $scope.error = null
      $scope.success = null

      CritiqueService.getCritique($scope.task.id, $scope.target).then (taskOutput) ->
        $scope.taskOutput = taskOutput
    )

    $scope.save = (finalise) ->
      if $scope.taskOutput?
        copied = angular.copy($scope.taskOutput)
        copied.finalise = finalise
        $scope.promise = TaskOutputService.updateBody(copied)
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { task: '=task', target: '=target' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/critique/directive_critFill.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.task').directive "critiqueFill", directive

