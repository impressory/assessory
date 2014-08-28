angular.module('assessory.task').controller 'taskoutput.Edit', ($scope, $location, CourseService, TaskOutputService, TaskService, taskoutput) ->

  $scope.orginalOutput = taskoutput

  $scope.taskoutput = angular.copy(taskoutput)

  $scope.task = TaskService.get(taskoutput.task).then((t) ->
    $scope.course = CourseService.get(t.course)
    t
  )

  $scope.save = (finalise) ->
    $scope.errors = []
    $scope.taskoutput.finalise = finalise
    $scope.promise = TaskOutputService.updateBody($scope.taskoutput).then(
      (to) -> $location.path("/taskoutput/#{$scope.taskoutput.id}")
      (res) -> res.data
    )
