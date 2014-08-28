define ["./course"], () ->

  controller = [ "$scope", "CourseService", "GroupService", "TaskService", "course", ($scope, CourseService, GroupService, TaskService, course) ->

    $scope.course = course

    GroupService.myGroups(course.id).then (groups) ->
      $scope.groups = groups

    TaskService.courseTasks(course.id).then (tasks) ->
      $scope.tasks = tasks

  ]


  resolveCourse = {
    course: [ "CourseService", "$stateParams", (CourseService, $stateParams) ->
      CourseService.get($stateParams.courseId)
    ]
  }

  angular.module('assessory.course').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course", {
      abstract: true
      url: '/course/{courseId:[0-9a-fA-F]+}'
      template: "<ui-view></ui-view>"
      resolve: resolveCourse
    }

    $stateProvider.state "course.view", {
      url: ''
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/course/view.html"
      controller: controller
    }

  ]

