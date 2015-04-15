define [ "./module" ], () ->

  controller = [ "$scope", "state", "CritiqueService", "GroupService", ($scope, $state, CritiqueService, GroupService) ->

    groupIds = (alloc.group for alloc in $scope.allocation.allocation)

    $scope.cachedGroups = {}

    GroupService.findMany(groupIds).then((groups) ->
      for group in groups
        $scope.cachedGroups[group.id] = group
    )

    $scope.createCritique = (alloc) ->
       GroupCritService.createCritique($scope.allocation.id, alloc.group).then((crit) ->
         $location.path("/taskoutput/#{crit.id}/edit")
       )
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) -> {
      scope: { allocation: '=allocation' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/critique/directive_allocationInfo.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.task').directive "allocationInfo", directive