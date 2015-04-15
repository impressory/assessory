define ["./task"], () ->

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { task: '=task' }
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/task/directive_taskInfo.html"
      restrict: 'E'
    }

  ]

  angular.module('assessory.task').directive "taskInfo", directive
