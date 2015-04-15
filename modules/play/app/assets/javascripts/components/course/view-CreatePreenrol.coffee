define ["./course"], () ->

  controller = [ "$scope", "CourseService", "$state", "course", ($scope, CourseService, $state, course) ->
    $scope.course = course

    $scope.preenrol = {}

    $scope.roleChoices = [
      { role : "student", chosen : false },
      { role : "staff", chosen : false }
    ]

    $scope.errors = []

    $scope.submit = (preenrol) ->
      $scope.errors = [ ]
      preenrol.roles = (r.role for r in $scope.roleChoices when r.chosen)

      CourseService.createPreenrol(course.id, preenrol).then(
       (p) -> $state.go("course.preenrol.view", { courseId: course.id, preenrolId: p.id })
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]

  angular.module('assessory.course').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin.preenrol__create", {
      url: '/createPreenrol'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/course/createPreenrol.html"
      controller: controller
    }

  ]
