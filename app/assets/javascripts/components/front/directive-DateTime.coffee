define [ "./module" ], () ->

  controller = [ "$scope", ($scope) ->

    $scope.$watch("epoch", () ->
      if $scope.epoch?
        $scope.date = new Date($scope.epoch)
      else
        $scope.date = null
    )

    $scope.$watch("date", () ->
      if $scope.date?
        $scope.epoch = $scope.date.getTime()
      else
        $scope.epoch = null
    )


  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      controller: controller
      restrict: 'E'
      scope: { epoch: '=' }
      template: """

      """
    }

  ]

  angular.module('assessory.config').directive "dateTimePicker", directive