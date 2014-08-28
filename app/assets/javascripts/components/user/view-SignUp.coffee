

define ["./user"], () ->

  #
  # Controller for sign-up form.
  #
  controller = [ "$scope", "UserService", "$state", ($scope, UserService, $state) ->

    $scope.user = { }

    $scope.errors = []

    $scope.submit = (user) ->
      $scope.errors = [ ]
      UserService.signUp(user).then(
       (data) -> $state.go("front")
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]

  angular.module('assessory.user').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "signUp", {
      url: '/signUp'
      templateUrl: "#{AssessoryConfig.assetBase}/views/signUp.html"
      controller: controller
    }

  ]