#
# Controller for sign-up form.
#
define(["./base"], (l) ->


  Assessory.controllers.login.UserManyInfo = ["$scope", "UserService",  ($scope, UserService) ->    
    
    $scope.users = UserService.findMany($scope.userIds)
    
  ]

  Assessory.angularApp.directive("userManyInfo", [ () -> 
    {
      scope: { userIds: '=userIds' }
      controller: Assessory.controllers.login.UserManyInfo
      templateUrl: "directive_userManyInfo.html"
      restrict: 'E'
    }
  ])

  Assessory.angularApp.directive("userInfo", [ () -> 
    {
      scope: { user: '=user' }
      templateUrl: "directive_userInfo.html"
      restrict: 'E'
    }
  ])

)