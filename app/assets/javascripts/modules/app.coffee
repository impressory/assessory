
define(["./base" ], () ->
   
    Assessory.angularApp.config(['$locationProvider', ($locationProvider) ->
        $locationProvider.html5Mode(true)
    ])
    
    Assessory.angularApp.config(['$httpProvider', ($httpProvider) ->
        $httpProvider.defaults.headers.common['Accept']='application/json'
    ])
    
    # Handles route change errors so that the user doesn't just see a blank page
    Assessory.angularApp.controller('ErrorController', ['$scope', ($scope) ->
      $scope.$on("$routeChangeError", (event, current, previous, rejection) ->
        $scope.error = rejection
      )
      $scope.$on("$routeChangeSuccess", () ->
        $scope.error = null
      )
    ])
    
    
    Assessory.angularApp.config(['$routeProvider', ($routeProvider) ->  
        $routeProvider.
          when('/', { templateUrl: '/partials/main.html' }).
          when('/logIn', { templateUrl: '/partials/logIn.html' }).
          when('/signUp', { templateUrl: '/partials/signUp.html' }).
          when('/self', { templateUrl: '/partials/self.html', controller: Assessory.controllers.login.Self }).
          when('/course/create', { templateUrl: '/partials/course/create.html' }).
          when('/course/:courseId/createGroupSet', { templateUrl: '/partials/group/createGroupSet.html', controller: Assessory.controllers.group.CreateGroupSet, resolve: Assessory.controllers.group.CreateGroupSet.resolve }).
          when('/course/:courseId/createPreenrol', { templateUrl: '/partials/course/createPreenrol.html', controller: Assessory.controllers.course.CreatePreenrol, resolve: Assessory.controllers.course.CreatePreenrol.resolve }).
          when('/course/:courseId', { templateUrl: '/partials/course/view.html', controller: Assessory.controllers.course.View, resolve: Assessory.controllers.course.View.resolve }).
          when('/groupSet/:gsId', { templateUrl: '/partials/group/viewGroupSet.html', controller: Assessory.controllers.group.ViewGroupSet, resolve: Assessory.controllers.group.ViewGroupSet.resolve }).
          when('/preenrol/:preenrolId', { templateUrl: '/partials/course/viewPreenrol.html', controller: Assessory.controllers.course.ViewPreenrol, resolve: Assessory.controllers.course.ViewPreenrol.resolve }).
          otherwise({ redirectTo: '/' })
      ])

    console.log("Angular app defined")

)