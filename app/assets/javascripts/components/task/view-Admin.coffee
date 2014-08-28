define ["./task"], () ->

  controller = [ "$scope", "CourseService", "GroupService", "TaskService", "course", "task", ($scope, CourseService, GroupService, TaskService, course, task) ->

    $scope.task = task

    $scope.course = course

  ]

  resolveTask = {
    task: [ "TaskService", "$stateParams", (TaskService, $stateParams) ->
      TaskService.get($stateParams.taskId)
    ]
  }

  angular.module('assessory.task').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin.task", {
      abstract: true
      url: '/task/{taskId:[0-9a-fA-F]+}'
      template: "<ui-view></ui-view>"
      resolve: resolveTask
    }

    $stateProvider.state "course.admin.task.view", {
      url: '/'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/task/view_admin.html"
      controller: controller
    }

  ]
