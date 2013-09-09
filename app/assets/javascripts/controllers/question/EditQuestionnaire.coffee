define(["./base"], (l) ->

  Assessory.controllers.question.EditQuestionnaire = ["$scope", ($scope) ->    
    
    $scope.addQuestion = (kind) ->
      $scope.questionnaire.questions.push({ kind: kind })
  ]
  
  Assessory.controllers.question.FillQuestionnaire = ["$scope", ($scope) -> 
  
    $scope.questions = $scope.questionnaire.questions
    
    $scope.answerMap = {}
    
    for answer in $scope.answers 
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
  
  Assessory.controllers.question.ViewQuestionnaire = ["$scope", ($scope) ->    
    
    $scope.questions = $scope.questionnaire.questions
    
    $scope.answerMap = {}
    
    for answer in $scope.answers 
      $scope.answerMap[answer.question] = answer

  ]
  
  Assessory.angularApp.directive("questionnaireEdit", [ () -> 
    {
      scope: { questionnaire: '=questionnaire' }
      controller: Assessory.controllers.question.EditQuestionnaire
      templateUrl: "directive_questionnaireEdit.html"
      restrict: 'E'
    }
  ]) 
  
  Assessory.angularApp.directive("questionnaireFill", [ () -> 
    {
      scope: { questionnaire: '=questionnaire', answers: "=answers" }
      controller: Assessory.controllers.question.FillQuestionnaire
      templateUrl: "directive_questionnaireFill.html"
      restrict: 'E'
    }
  ])   
  
  Assessory.angularApp.directive("questionnaireView", [ () -> 
    {
      scope: { questionnaire: '=questionnaire', answers: "=answers" }
      controller: Assessory.controllers.question.ViewQuestionnaire
      templateUrl: "directive_questionnaireView.html"
      restrict: 'E'
    }
  ])    

)