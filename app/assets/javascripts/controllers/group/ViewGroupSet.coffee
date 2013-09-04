define(["./base"], (l) ->

  Assessory.controllers.group.ViewGroupSet = ["$scope", "CourseService", "GroupService", "groupSet", ($scope, CourseService, GroupService, groupSet) ->    

    $scope.groupSet = groupSet

  ]
  
  Assessory.controllers.group.ViewGroupSet.resolve = {
    groupSet: ['$route', 'GroupService', ($route, GroupService) -> 
      GroupService.getGroupSet($route.current.params.gsId)
    ]
  }  

)