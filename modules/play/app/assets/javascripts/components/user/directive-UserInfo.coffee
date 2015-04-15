define ["./user"], () ->

  directive = [ "AssessoryConfig", (AssessoryConfig) ->
    {
      scope: { user: '=user' }
      templateUrl: "#{AssessoryConfig.assetBase}/views/components/user/directive_userInfo.html"
      restrict: 'E'
    }

  ]

  angular.module('assessory.user').directive "userInfo", directive
