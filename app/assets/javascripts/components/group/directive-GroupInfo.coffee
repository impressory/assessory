define [ "./group" ], () ->

  controller = [ "$scope", "GroupService", ($scope, GroupService) ->
    GroupService.getGroupSet($scope.group.set).then (gs) ->
      $scope.groupSet = gs
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { group: '=group' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/group/directive_groupInfo.html"
      restrict: 'E'
    }
  ]

  angular.module("assessory.group").directive "groupInfo", directive