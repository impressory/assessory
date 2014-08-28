define [ "./module" ], () ->

  controllerFront = [ "$scope", "user", ($scope, user) ->
    $scope.user = user

  ]

  controllerFrontResolve = {
    user: ['UserService', (UserService) ->
          UserService.self().then(
            (s) -> s
            (err) -> null
          )
        ]
  }

  angular.module('assessory.front').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "front", {
      url: '/'
      templateUrl: "#{AssessoryConfig.assetBase}/views/main.html"
      controller: controllerFront
      resolve: controllerFrontResolve
    }

  ]
