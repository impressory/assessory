define ["./group"], () ->

  controller = [ "$scope", "GroupService", "$state", "course", "groupSet", ($scope, GroupService, $state, course, groupSet) ->

    $scope.course = course

    $scope.groupSet = groupSet

    $scope.uploadData = { }

    $scope.errors = []

    $scope.submit = () ->
      $scope.errors = [ ]
      GroupService.uploadGroups($scope.groupSet.id, $scope.uploadData).then(
       (gs) -> $state.go("course.admin.groupSet.view", { courseId: course.id, groupSetId: groupSet.id }, { reload: true })
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )

  ]


  angular.module('assessory.group').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin.groupSet.upload", {
      url: '/upload'
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/group/upload.html"
      controller: controller
    }

  ]
