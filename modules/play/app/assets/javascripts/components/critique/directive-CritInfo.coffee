define [ "./module" ], () ->

  controller = [ "$scope", "CourseService", "CritiqueService", ($scope, CourseService, CritiqueService) ->

    $scope.setSelected = (target) ->
      $scope.selectedTarget = target

    CritiqueService.myAllocations($scope.task.id).then (alloc) ->
      $scope.myAllocations = alloc

  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { task: '=task' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/critique/directive_critInfo.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.critique').directive "critiqueInfo", directive