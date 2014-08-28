define ["./module"], () ->

  controller = [ "$scope", "TaskService", "$state", "course", ($scope, TaskService, $state, course) ->

    $scope.course = course

    $scope.task = {
      body: {
        kind: "Critique"
        strategy: {
          kind: "group"
          number: 5
        }
        questionnaire: {
          questions: []
        }
      }

    }

    $scope.errors = []

    $scope.submit = () ->
      $scope.errors = [ ]
      TaskService.create(course.id, $scope.task).then(
       (t) -> $state.go("course.admin.task.view", { courseId: course.id, taskId: t.id })
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )

  ]

  angular.module('assessory.course').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin.critique__create", {
      url: '/createCritTask'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/critique/view_create.html"
      controller: controller
    }

  ]