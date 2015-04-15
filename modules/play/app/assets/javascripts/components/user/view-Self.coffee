'use strict';

define ["./user"], () ->

  #
  # Controller for sign-up form.
  #
  controller = [ "$scope", "user", ($scope, user) ->
    $scope.user = user
  ]

  controller.resolve = {
    user: [ "UserService", (UserService) -> UserService.self() ]
  }

  angular.module('assessory.user').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "self", {
      url: '/self'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/user/self.html"
      controller: controller
      resolve: controller.resolve
    }

  ]