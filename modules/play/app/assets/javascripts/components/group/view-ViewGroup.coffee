define ["./group"], () ->

  controller = [ "$scope", "CourseService", "GroupService", "course", "group", ($scope, CourseService, GroupService, course, group) ->

    $scope.course = course

    $scope.group = group

    GroupService.getGroupSet($scope.group.set).then (gs) ->
      $scope.groupSet = gs

  ]

  resolveGroup = {
    group: [ "GroupService", "$stateParams", (GroupService, $stateParams) ->
      GroupService.get($stateParams.groupId)
    ]
  }

  angular.module('assessory.group').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.group", {
      abstract: true
      url: '/group/{groupId:[0-9a-fA-F]+}'
      template: "<ui-view></ui-view>"
      resolve: resolveGroup
    }

    $stateProvider.state "course.group.view", {
      url: ''
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/group/viewGroup.html"
      controller: controller
    }

  ]
