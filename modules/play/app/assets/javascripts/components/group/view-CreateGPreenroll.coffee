define ["./group"], () ->

  controller = [ "$scope", "GroupService", "$state", "course", "groupSet", ($scope, GroupService, $state, course, groupSet) ->

    $scope.course = course

    $scope.groupSet = groupSet

    $scope.gpreenroll = { }

    $scope.errors = []

    $scope.submit = (gpreenrol) ->
      $scope.errors = [ ]
      GroupService.createGroupPreenrol($scope.groupSet.id, gpreenrol).then(
       (gs) -> $state.go("course.admin.groupSet.view", { courseId: course.id, groupSetId: groupSet.id }, { reload: true })
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )

  ]


  angular.module('assessory.group').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin.groupSet.gpreenrol__create", {
      url: '/createGPreenrol'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/group/createGPreenrol.html"
      controller: controller
    }

  ]
