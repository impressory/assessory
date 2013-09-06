define(["./base"], (l) ->

  Assessory.controllers.group.View = ["$scope", "CourseService", "GroupService", "group", ($scope, CourseService, GroupService, group) ->    

    $scope.course = CourseService.get(group.course)

    $scope.group = group
    
    $scope.groupSet = GroupService.getGroupSet($scope.group.set)

  ]
  
  Assessory.controllers.group.GroupInfo = ["$scope", "GroupService", ($scope, GroupService) ->    

    $scope.groupSet = GroupService.getGroupSet($scope.group.set)

  ]  
  
  Assessory.controllers.group.View.resolve = {
    group: ['$route', 'GroupService', ($route, GroupService) -> 
      GroupService.get($route.current.params.groupId)
    ]
  }  
  
  Assessory.angularApp.directive("groupInfo", [ () -> 
    {
      scope: { group: '=group' }
      controller: Assessory.controllers.group.GroupInfo
      templateUrl: "directive_groupInfo.html"
      restrict: 'E'
    }
  ])  
    
)