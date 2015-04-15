define [ "./module" ], () ->

  controller = [ "$scope", "GroupService", ($scope, GroupService) ->

    GroupService.courseGroupSets($scope.course.id).then (sets) ->
      $scope.groupSets = sets

  ]

  directive = () -> {
    controller: controller
    template: """
      <div class="form-group">
        <label>Choose groups from </label>
        <select ng-model="strategy.set">
          <option ng-repeat="set in groupSets" ng-value="set.id">{{ set.name || 'Untitled' }}</option>
        </select>
      </div>
      <div class="form-group">
        <label>Number of groups to critique</label>
        <input ng-model="strategy.number" />
      </div>
    """
    restrict: 'E'
    scope: { course: "=", strategy: "=" }
  }

  angular.module("assessory.critique").directive "strategyGroupEdit", directive

