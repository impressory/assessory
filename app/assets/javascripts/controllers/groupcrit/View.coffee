define(["./base"], (l) ->

  Assessory.controllers.groupcrit.GroupCritInfo = ["$scope", "CourseService", "GroupCritService", ($scope, CourseService, GroupCritService) ->    

    $scope.myAllocations = GroupCritService.myAllocations($scope.task.id)

  ]
  
  Assessory.controllers.groupcrit.GCAllocationInfo = ["$scope", "GroupCritService", "GroupService", ($scope, GroupCritService, GroupService) ->    

    groupIds = (alloc.group for alloc in $scope.allocation.allocation)

    $scope.cachedGroups = {}

    groups = GroupService.findMany(groupIds).then((groups) ->
         for group in groups
           $scope.cachedGroups[group.id] = group
    )
    
    $scope.createCritique = (alloc) ->
       GroupCritService.createCritique($scope.allocation.id, alloc.group).then((crit) ->
          $location.path("/groupcritique/#{crit.id}/edit")
       )
      

  ]  
  
  Assessory.controllers.groupcrit.GCAllAllocations = ["$scope", "UserService", "GroupService", "GroupCritService", ($scope, UserService, GroupService, GroupCritService) ->    

     # We keep our own locally updated cache of users on the scope, so that the table of allocations
     # cannot inadvertently cause individual requests for users to the server
     $scope.cachedUsers = {}

     # We keep our own locally updated cache of users on the scope, so that the table of allocations
     # cannot inadvertently cause individual requests for users to the server
     $scope.cachedGroups = {}

     $scope.allocations = GroupCritService.allAllocations($scope.task.id).then((allocations) ->
       userIds = (allocation.user for allocation in allocations when allocation.user != null)
       
       # Bulk fetch the users
       users = UserService.findMany(userIds).then((users) ->
         for user in users
           $scope.cachedUsers[user.id] = user
         users
       )
       
       # Bulk fetch the groups
       groups = {}
       for allocation in allocations
         for alloc in allocation.allocation
           groups[alloc.group] = 1
       groups = GroupService.findMany(Object.keys(groups)).then((groups) ->
         for group in groups
           $scope.cachedGroups[group.id] = group
       )
       allocations 
     )
     
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