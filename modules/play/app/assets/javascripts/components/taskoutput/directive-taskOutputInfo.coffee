define [ "./base" ], () ->


  controller = [ "$scope", "TaskService", "TaskOutputService", ($scope, TaskService, TaskOutputService) ->

    $scope.$watch('taskOutputId', (nv) ->
      $scope.taskOutput = null
      $scope.task = null
      $scope.qfilter = null

      TaskOutputService.get($scope.taskOutputId).then (to) ->
        $scope.taskOutput = to

        $scope.qfilter = (q) -> q.kind == "Short text"

        TaskService.get(to.task).then (t) ->
          $scope.task = t
    )
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { taskOutputId: '=' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/taskoutput/directive_taskoutputInfo.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.task').directive "taskOutputInfo", directive