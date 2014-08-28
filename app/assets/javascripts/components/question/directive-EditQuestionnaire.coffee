define [ "./module" ], () ->

  controller = [ "$scope", ($scope) ->
    $scope.addQuestion = (kind) ->
      $scope.questionnaire.questions.push({ kind: kind })
  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { questionnaire: '=questionnaire' }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/question/directive_questionnaireEdit.html"
      restrict: 'E'
    }
  ]

  angular.module('assessory.question').directive "questionnaireEdit", directive
