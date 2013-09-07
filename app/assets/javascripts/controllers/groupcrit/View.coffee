define(["./base"], (l) ->

  Assessory.controllers.groupcrit.GroupCritInfo = ["$scope", "CourseService", "GroupCritService", ($scope, CourseService, GroupCritService) ->    

    $scope.myAllocations = GroupCritService.myAllocations($scope.task.id)

  ]
  
  Assessory.controllers.groupcrit.GCAlloactionInfo = ["$scope", "CourseService", "GroupService", ($scope, CourseService, GroupService) ->    

    $scope.group = GroupService.get($scope.allocation.allocation.group)

  ]  
  
  Assessory.controllers.groupcrit.GCAllAllocations = ["$scope", "CourseService", "GroupService", "GroupCritService", ($scope, CourseService, GroupService, GroupCritService) ->    

     $scope.myAllocations = GroupCritService.allAllocations($scope.task.id)
     
     $scope.allocate = () -> GroupCritService.allocateTask($scope.task.id)

  ]   
  
  
  Assessory.angularApp.directive("groupCritInfo", [ () -> 
    {
      scope: { task: '=task' }
      controller: Assessory.controllers.groupcrit.GroupCritInfo
      templateUrl: "directive_groupCritInfo.html"
      restrict: 'E'
    }
  ])  
    
  Assessory.angularApp.directive("gcAllocationInfo", [ () -> 
    {
      scope: { allocation: '=allocation' }
      controller: Assessory.controllers.groupcrit.GCAllocationInfo
      templateUrl: "directive_gcAllocationInfo.html"
      restrict: 'E'
    }
  ])  
  
  Assessory.angularApp.directive("gcAllAllocations", [ () -> 
    {
      scope: { task: '=task' }
      controller: Assessory.controllers.groupcrit.GCAllAllocations
      templateUrl: "directive_gcAllAllocations.html"
      restrict: 'E'
    }
  ])    
)