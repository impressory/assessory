define [ "./group" ], () ->

  controller = [ "$scope", "GroupService", ($scope, GroupService) ->
    $scope.$watch("groupId", (nv) ->
      GroupService.get($scope.groupId).then (g) ->
        $scope.group = g
    )

  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { groupId: '=groupId' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/group/directive_groupLabel.html"
      restrict: 'E'
    }
  ]

  angular.module("assessory.group").directive "groupLabel", directive