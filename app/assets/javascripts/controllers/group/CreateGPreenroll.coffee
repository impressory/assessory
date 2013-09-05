define(["./base"], (l) ->

  Assessory.controllers.group.CreateGPreenrol = ["$scope", "GroupService", ($scope, GroupService) ->    
    $scope.gpreenroll = { }
    
    $scope.errors = []    
    
    $scope.submit = (gpreenroll) -> 
      $scope.errors = [ ]
      GroupService.createGroupPreenroll(groupSet.id, gpreenroll).then(
       (gs) -> $scope.refreshGroups()
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]

)