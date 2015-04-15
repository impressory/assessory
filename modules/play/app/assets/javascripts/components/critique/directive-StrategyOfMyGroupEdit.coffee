define [ "./module" ], () ->

  controller = [ "$scope", "TaskService", ($scope, TaskService) ->

    TaskService.courseTasks($scope.course.id).then (tasks) ->
      $scope.tasks = tasks

  ]

  directive = () -> {
    controller: controller
    template: """
      <div class="form-group">
        <label>Review critiques from </label>
        <select ng-model="strategy.task">
          <option ng-repeat="task in tasks" ng-value="task.id">{{ task.details.name || 'Untitled' }}</option>
        </select>
      </div>
    """
    restrict: 'E'
    scope: { course: "=", strategy: "=" }
  }

  angular.module("assessory.critique").directive "strategyOfMyGroupEdit", directive

