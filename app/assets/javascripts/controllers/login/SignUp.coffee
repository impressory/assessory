#
# Controller for sign-up form.
#
define(["./base"], (l) ->

  Assessory.controllers.login.SignUp = ["$scope", "UserService", "$location", ($scope, UserService, $location) ->    
    $scope.user = { }
    
    $scope.errors = []    
    
    $scope.submit = (user) -> 
      $scope.errors = [ ]
      UserService.signUp(user).then(
       (data) -> $location.path("/")           
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]

)