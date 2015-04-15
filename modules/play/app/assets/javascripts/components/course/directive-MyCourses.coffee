'use strict';
define ["./course"], () ->

  controller = [ "$scope", "CourseService", ($scope, CourseService) ->

    $scope.findMyCourses = () ->
      CourseService.doPreenrolments().then((d) ->
        $scope.refreshMyCourses()
      )

    $scope.refreshMyCourses = () ->
      CourseService.my().then((courses) ->
        $scope.courses = courses
      )

    $scope.refreshMyCourses()
  ]

  angular.module('assessory.course').controller 'course.MyCourses', controller