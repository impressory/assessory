'use strict';

define ["./user"], () ->

  #
  # Controller for log-in form.
  #
  controller = [ "$scope", "UserService", "AssessoryConfig", "$location", "$sce", ($scope, UserService, AssessoryConfig, $location, $sce) ->

    $scope.twitter = $sce.trustAsResourceUrl("#{AssessoryConfig.apiBase}/oauth/twitter")

    $scope.github = $sce.trustAsResourceUrl("#{AssessoryConfig.apiBase}/oauth/github")

    $scope.user = { }

    $scope.errors = []

    $scope.submit = (user) ->
      $scope.errors = [ ]
      UserService.logIn(user).then(
       (data) -> $location.path("/")
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )

  ]

  angular.module('assessory.user').controller "user.LogIn", controller

  angular.module('assessory.user').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "login", {
      url: '/logIn'
      templateUrl: "#{AssessoryConfig.assetBase}/views/logIn.html"
      controller: controller
    }

  ]