'use strict'
define [
  "components/configuration/require",
  "components/user/require",
  "components/course/require",
  "components/group/require",
  "components/task/require",
  "components/taskoutput/require",

  "components/question/require",
  "components/critique/require",

  "components/front/require"

], () ->


  # Helper functions
  merge = (options, overrides) ->
    extend (extend {}, options), overrides


  #
  # Declare the app
  #
  angular.module('assessory', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ui.router',

    'assessory.config',
    'assessory.user',
    'assessory.course',
    'assessory.group',
    'assessory.task',
    'assessory.question',
    'assessory.critique',
    'assessory.front'
  ])
    .config [ "$locationProvider", ($locationProvider) ->
      $locationProvider.html5Mode(true)
    ]

  #
  # Handles route change errors so that the user doesn't just see a blank page
  #
  angular.module('assessory').controller 'ErrorController', [ "$scope", ($scope) ->
    $scope.$on("$routeChangeError", (event, current, previous, rejection) ->
      $scope.error = rejection
    )
    $scope.$on("$routeChangeSuccess", () ->
      $scope.error = null
    )
  ]


  console.log("Angular app defined")




