define ["./course"], () ->

  controller = [ "$scope", "CourseService", "$state", ($scope, CourseService, $state) ->
    $scope.course = { }

    $scope.errors = []

    $scope.submit = (course) ->
      $scope.errors = [ ]
      CourseService.create(course).then(
       (course) -> $state.go("course.view", { courseId: course.id })
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]

  angular.module('assessory.course').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course__create", {
      url: '/course/create'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/course/create.html"
      controller: controller
    }

  ]