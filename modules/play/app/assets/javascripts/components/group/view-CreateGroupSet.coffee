define ["./group"], () ->

  controller = [ "$scope", "GroupService", "$state", "course", ($scope, GroupService, $state, course) ->
    $scope.course = course

    $scope.groupSet = { }

    $scope.errors = []

    GroupService.courseGroupSets(course.id).then((data) ->
      $scope.groupSets = data
    )

    $scope.submit = (groupSet) ->
      $scope.errors = [ ]
      GroupService.createGroupSet(course.id, groupSet).then(
       (gs) -> $state.go("course.admin.groupSet.view", { courseId: course.id, groupSetId: gs.id }, { reload: true })
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )

  ]

  angular.module('assessory.group').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin.groupSet__create", {
      url: '/createGroupSet'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/group/createGroupSet.html"
      controller: controller
    }

  ]
