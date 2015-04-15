'use strict';

define [ "./module" ], () ->

  controller = [ "$scope", "UserService", "$location", "$state", ($scope, UserService, $location, $state) ->

    UserService.self().then (user) ->
      $scope.user = user

    $scope.logOut = () ->
      UserService.logOut().then(
        (res) ->
          $state.go("front", null, { reload: true })
        (res) ->
          $state.go("front", null, { reload: true })
      )

  ]

  angular.module('assessory.front').directive "siteHeader", [ "AssessoryConfig", (AssessoryConfig) -> {
      controller: controller
      restrict: 'E'
      scope: {}
      templateUrl: "#{AssessoryConfig.assetBase}/views/directives/directive_siteHeader.html"
    }
  ]

