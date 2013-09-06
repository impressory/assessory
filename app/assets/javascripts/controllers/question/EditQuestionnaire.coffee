define(["./base"], (l) ->

  Assessory.controllers.question.EditQuestionnaire = ["$scope", ($scope) ->    
    
    $scope.addQuestion = (kind) ->
      $scope.questionnaire.questions.push({ kind: kind })
  ]
  
  Assessory.angularApp.directive("questionnaireEdit", [ () -> 
    {
      scope: { questionnaire: '=questionnaire' }
      controller: Assessory.controllers.question.EditQuestionnaire
      templateUrl: "directive_questionnaireEdit.html"
      restrict: 'E'
    }
  )]  

)