define(["./base"], (l) ->

  Assessory.controllers.group.ViewGroupSet = ["$scope", "CourseService", "GroupService", "groupSet", ($scope, CourseService, GroupService, groupSet) ->    

    $scope.groupSet = groupSet
    
    $scope.course = CourseService.get(groupSet.course)
    
    $scope.refreshGroups = () -> 
      $scope.groups = GroupService.byGroupSet(groupSet.id)

    $scope.refreshGroups()
  ]
  
  Assessory.controllers.group.ViewGroupSet.resolve = {
    groupSet: ['$route', 'GroupService', ($route, GroupService) -> 
      GroupService.getGroupSet($route.current.params.gsId)
    ]
  }  

)