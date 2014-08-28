define ["./group"], () ->

  controller = [ "$scope", "CourseService", "GroupService", "course", "groupSet", ($scope, CourseService, GroupService, course, groupSet) ->

    $scope.groupSet = groupSet

    $scope.course = course

    $scope.refreshGroups = () ->
      GroupService.byGroupSet(groupSet.id).then (groups) ->
        $scope.groups = groups

    $scope.refreshGroups()

  ]

  resolveGroupSet = {
    groupSet: [ "GroupService", "$stateParams", (GroupService, $stateParams) ->
      GroupService.getGroupSet($stateParams.groupSetId)
    ]
  }

  angular.module('assessory.group').config [ "$stateProvider", "AssessoryConfig", ($stateProvider, AssessoryConfig) ->

    $stateProvider.state "course.admin.groupSet", {
      abstract: true
      url: '/groupSet/{groupSetId:[0-9a-fA-F]+}'
      template: "<ui-view></ui-view>"
      resolve: resolveGroupSet
    }

    $stateProvider.state "course.admin.groupSet.view", {
      url: ''
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/group/viewGroupSet.html"
      controller: controller
    }

  ]
