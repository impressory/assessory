define [ "./module" ], () ->

  controller = [ "$scope", ($scope) ->

    $scope.questions = $scope.questionnaire.questions

    $scope.answerMap = {}

    for answer in ($scope.answers || [])
      $scope.answerMap[answer.question] = answer

    for question in $scope.questions
      ans = $scope.answerMap[question.id]
      if not (ans?)
        ans =  {
          kind: question.kind
          question: question.id
          answer: null
        }
        $scope.answers.push(ans)
        $scope.answerMap[question.id] = ans

  ]

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { questionnaire: '=questionnaire', answers: "=answers" }
      controller: controller
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/question/directive_questionnaireFill.html"
      restrict: 'E'
    }

  ]

  angular.module('assessory.question').directive "questionnaireFill", directive