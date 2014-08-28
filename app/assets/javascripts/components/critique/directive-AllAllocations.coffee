define [ "./module" ], () ->

  controller = [ "$scope", "UserService", "GroupService", "CritiqueService", ($scope, UserService, GroupService, CritiqueService) ->

     # We keep our own locally updated cache of users on the scope, so that the table of allocations
     # cannot inadvertently cause individual requests for users to the server
     $scope.cachedUsers = {}

     # We keep our own locally updated cache of users on the scope, so that the table of allocations
     # cannot inadvertently cause individual requests for users to the server
     $scope.cachedGroups = {}

     CritiqueService.allAllocations($scope.task.id).then((allocations) ->
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
         for alloc in allocation.allocation when alloc.target.kind == 'Group'
           groups[alloc.target.ref] = 1
       groups = GroupService.findMany(Object.keys(groups)).then((groups) ->
         for group in groups
           $scope.cachedGroups[group.id] = group
       )

       $scope.allocations = allocations
     )

     $scope.allocate = () -> CritiqueService.allocateTask($scope.task.id)
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { task: '=task' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/critique/directive_allAllocations.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.critique').directive "critAllAllocations", directive


