define [ "./module" ], () ->

  directive = [ "AssessoryConfig", (AssessoryConfig) -> {
      scope: { target: '=target', index: '=' }
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/critique/directive_targetInfo.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.task').directive "targetInfo", directive
