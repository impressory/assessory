define ["./course"], () ->

  controller = [ "$scope", "CourseService", "GroupService", "TaskService", "course", ($scope, CourseService, GroupService, TaskService, course) ->

    $scope.course = course

    GroupService.courseGroupSets(course.id).then((groupSets) ->
      $scope.groupSets = groupSets
    )

    CourseService.coursePreenrols(course.id).then((preenrols) ->
      $scope.preenrols = preenrols
    )

    TaskService.courseTasks(course.id).then((tasks) ->
      $scope.tasks = tasks
    )
  ]

  angular.module('assessory.course').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin", {
      url: '/admin'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/course/admin.html"
      controller: controller
    }

  ]

