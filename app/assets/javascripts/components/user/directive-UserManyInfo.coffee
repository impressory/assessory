define ["./user"], () ->

  controller = [ "$scope", "UserService", "$location", ($scope, UserService, $location) ->
    UserService.findMany($scope.userIds).then (users) ->
      $scope.users = users
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { userIds: '=userIds' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/user/directive_userManyInfo.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.user').directive "userManyInfo", directive
