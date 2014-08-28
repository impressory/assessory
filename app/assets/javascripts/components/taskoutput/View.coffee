angular.module('assessory.task').controller 'taskoutput.View', ($scope, CourseService, GroupService, TaskService, taskoutput) ->

  $scope.taskoutput = taskoutput

  $scope.task = TaskService.get(taskoutput.task).then((t) ->
    $scope.course = CourseService.get(t.course)
    t
  )
    
angular.module('assessory.task').controller 'taskoutput.Info', ($scope, CourseService, GroupService, TaskService, TaskOutputService) ->

  $scope.$watch('taskOutputId', (nv) ->
    $scope.taskOutput = null
    $scope.task = null
    $scope.qfilter = null

    TaskOutputService.get($scope.taskOutputId).then (to) ->
      $scope.taskOutput = to

      $scope.qfilter = (q) -> q.kind == "Short text"

      TaskService.get(to.task).then (t) ->
        $scope.task = t
  )



angular.module('assessory.task').directive "taskOutputInfo", () ->
  {
    scope: { taskOutputId: '=' }
    controller: "taskoutput.Info"
    templateUrl: "/views/components/taskoutput/directive_taskoutputInfo.html"
    restrict: 'E'
  }