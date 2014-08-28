define [ "./module" ], () ->

  controller = [ "$scope", ($scope) ->

    $scope.submit = () ->
      $scope.error = null
      $scope.success = null

      $scope.promise = $scope.action().then(
        (success) ->
          console.log("saved")
          $scope.promise = null
          $scope.success = $scope.successMessage || "Saved"
        (error) ->
          $scope.promise = null
          $scope.error = error.error || "Unexpected error"
      )
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      controller: controller
      restrict: 'E'
      scope: { action: '&', text: '@', successMessage: '@'}
      templateUrl: "#{AssessoryConfig.assetBase}/views/directives/directive_saveButton.html"
    }

  ]

  angular.module('assessory.config').directive "saveButton", directive