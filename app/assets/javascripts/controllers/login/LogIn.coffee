#
# Controller for log-in form.
#
define(["./base"], (l) ->

  Assessory.controllers.login.LogIn = ["$scope", "UserService", "$location", ($scope, UserService, $location) ->    
    $scope.user = { }
    
    $scope.errors = []
        
    $scope.submit = (user) -> 
      $scope.errors = [ ]
      UserService.logIn(user).then(
       (data) -> $location.path("/")           
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]
  
)