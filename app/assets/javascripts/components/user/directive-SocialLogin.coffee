'use strict';

define ["./user"], () ->

  #
  # Controller for log-in form.
  #
  controller = [ "$scope", "AssessoryConfig", "$sce", ($scope, AssessoryConfig, $sce) ->

    $scope.twitter = $sce.trustAsResourceUrl("#{AssessoryConfig.oauthBase}/twitter")

    $scope.github = $sce.trustAsResourceUrl("#{AssessoryConfig.oauthBase}/github")

  ]

  angular.module('assessory.user').directive "socialLogIn", [ "AssessoryConfig", (AssessoryConfig) -> {
      controller: controller
      restrict: 'E'
      template: """
        <div>
          <p>
            <form action="{{twitter}}" method="POST">
              <button class="btn btn-default" type="submit"><i class="icon-twitter"></i> Sign in with <b>Twitter</b></button>
            </form>
          <p>
            <form action="{{github}}" method="POST">
              <button class="btn btn-default" type="submit"><i class="icon-github"></i> Sign in with <b>GitHub</b></button>
            </form>
          </p>
        </div>
      """
    }
  ]