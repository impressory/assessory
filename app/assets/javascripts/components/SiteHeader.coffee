define(["./base"], (l) -> 


  Assessory.controllers.components.SiteHeader = ['$scope', 'UserService', '$location', "$route", ($scope, UserService, $location, $route) ->
  
    $scope.user = UserService.self()
    
    $scope.logOut = () -> 
      UserService.logOut().then(
        (res) -> 
          $location.path("/")
          $route.reload()       
        (res) -> 
          $location.path("/")
          $route.reload()
      )           
  ]

  Assessory.angularApp.directive("siteHeader", ["UserService", (UserService) -> 
    {
      controller: Assessory.controllers.components.SiteHeader
      restrict: 'E'
      templateUrl: "directive_siteHeader.html"
    }
  )]

)