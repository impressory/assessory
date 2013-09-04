#
# Controller for sign-up form.
#
define(["./base"], (l) ->

  Assessory.controllers.login.Self = ["$scope", "UserService",  ($scope, UserService) ->    
    
    $scope.user = UserService.self()
    
  ]

)