
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

          when('/course/:courseId/createGroupCritTask', { templateUrl: '/partials/groupcrit/createTask.html', controller: Assessory.controllers.groupcrit.CreateTask, resolve: Assessory.controllers.groupcrit.CreateTask.resolve }).

          when('/course/:courseId/createGroupSet', { templateUrl: '/partials/group/createGroupSet.html', controller: Assessory.controllers.group.CreateGroupSet, resolve: Assessory.controllers.group.CreateGroupSet.resolve }).
          when('/course/:courseId/createPreenrol', { templateUrl: '/partials/course/createPreenrol.html', controller: Assessory.controllers.course.CreatePreenrol, resolve: Assessory.controllers.course.CreatePreenrol.resolve }).
          when('/course/:courseId/admin', { templateUrl: '/partials/course/admin.html', controller: Assessory.controllers.course.Admin, resolve: Assessory.controllers.course.Admin.resolve }).
          when('/course/:courseId', { templateUrl: '/partials/course/view.html', controller: Assessory.controllers.course.View, resolve: Assessory.controllers.course.View.resolve }).
          when('/group/:groupId', { templateUrl: '/partials/group/view.html', controller: Assessory.controllers.group.View, resolve: Assessory.controllers.group.View.resolve }).
          when('/groupSet/:gsId', { templateUrl: '/partials/group/viewGroupSet.html', controller: Assessory.controllers.group.ViewGroupSet, resolve: Assessory.controllers.group.ViewGroupSet.resolve }).
          when('/preenrol/:preenrolId', { templateUrl: '/partials/course/viewPreenrol.html', controller: Assessory.controllers.course.ViewPreenrol, resolve: Assessory.controllers.course.ViewPreenrol.resolve }).
          when('/task/:taskId/admin', { templateUrl: '/partials/task/admin.html', controller: Assessory.controllers.task.Admin, resolve: Assessory.controllers.task.Admin.resolve }).
          when('/task/:taskId', { templateUrl: '/partials/task/view.html', controller: Assessory.controllers.task.View, resolve: Assessory.controllers.task.View.resolve }).

          when('/taskoutput/:taskoutputId/edit', { templateUrl: '/partials/taskoutput/edit.html', controller: Assessory.controllers.taskoutput.Edit, resolve: Assessory.controllers.taskoutput.Edit.resolve }).
          when('/taskoutput/:taskoutputId', { templateUrl: '/partials/taskoutput/view.html', controller: Assessory.controllers.taskoutput.View, resolve: Assessory.controllers.taskoutput.View.resolve }).

          otherwise({ redirectTo: '/' })
      ])

    console.log("Angular app defined")

)