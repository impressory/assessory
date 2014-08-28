define [ "./course" ], () ->

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { course: '=course' }
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/course/directive_courseInfo.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.course').directive "courseInfo", directive