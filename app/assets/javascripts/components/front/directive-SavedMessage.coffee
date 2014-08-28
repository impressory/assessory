define [ "./module" ], () ->

  controller = [ "$scope", ($scope) ->

    $scope.$watch("promise", (nv) ->
      $scope.error = null
      $scope.success = null

      if $scope.promise?
        nv.then(
          (success) -> $scope.success = $scope.successMessage,
          (error) -> $scope.error = error.error || "Unexpected error"
        )
    )

  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      controller: controller
      restrict: 'E'
      scope: { promise: '=', successMessage: '@'}
      templateUrl: "#{AssessoryConfig.assetBase}/views/directives/directive_savedMessage.html"
    }
  ]

  angular.module('assessory.front').directive "savedMessage", directive