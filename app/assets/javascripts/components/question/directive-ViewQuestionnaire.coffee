define [ "./module" ], () ->

  controller = [ "$scope", ($scope) ->

    $scope.questions = $scope.questionnaire.questions

    $scope.answerMap = {}

    for answer in $scope.answers
      $scope.answerMap[answer.question] = answer

    $scope.showQuestion = (q) -> (!$scope.qfilter?) || $scope.qfilter(q)

  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { questionnaire: '=questionnaire', answers: "=answers", qfilter: "=qfilter" }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/question/directive_questionnaireView.html"
      restrict: 'E'
    }

  ]

  angular.module('assessory.question').directive "questionnaireView", directive