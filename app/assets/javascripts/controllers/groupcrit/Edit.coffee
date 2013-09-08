define(["./base"], (l) ->

  Assessory.controllers.groupcrit.EditTaskBody = ["$scope", "CourseService", "GroupService", "TaskService", "$location", ($scope, CourseService, GroupService, TaskService, $location) ->    

    $scope.groupSets = GroupService.courseGroupSets($scope.task.course)
    
    $scope.errors = []    
    
    origTask = $scope.task
    
    $scope.reset = () -> 
      $scope.task = angular.copy(origTask)
    
    $scope.save = (task) -> 
      $scope.errors = [ ]
      TaskService.updateBody(task).then(
       (task) -> $location.path("/task/#{task.id}")           
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
      
    $scope.reset()
  ]
)