'use strict';
define ["./course"], () ->

  service = [ "$http", "$cacheFactory", "AssessoryConfig", "UserService", "$q", ($http, $cacheFactory, AssessoryConfig, UserService, $q) ->

    cache = $cacheFactory("courseCache")
    preenrolCache = $cacheFactory("preenrolCache")

    {

      create: (course) ->
        prom = $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/course/create"
          data: course
        ).then((res) =>
          course = res.data
          @cache(course)
        )

      get: (id) ->
        cache.get(id) || (
          prom = $http(
            withCredentials: true
            method: "GET"
            url: "#{AssessoryConfig.apiBase}/course/#{id}"
          ).then(
            (successRes) -> successRes.data
          )
          cache.put(id, prom)
          prom
        )

      cache: (course) ->
        if course.id?
          defer = $q.defer()
          cache.put(course.id, defer.promise)
          defer.resolve(course)
          course

      findMany: (ids) ->
        $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/course/findMany"
          data: { ids: ids }
        ).then(
          (successRes) -> successRes.data
        )

      my: () ->
        $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/course/my"
        ).then(
          (successRes) -> successRes.data
        )

      createPreenrol: (courseId, preenrol) ->
        $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/course/#{courseId}/createPreenrol"
        ).then((res) ->
          d = res.data
          preenrolCache.put(d.id, d)
          d
        )

      getPreenrol: (id) ->
        preenrolCache.get(id) || (
          prom = $http(
            withCredentials: true
            method: "GET"
            url: "#{AssessoryConfig.apiBase}/preenrol/#{id}"
          ).then(
            (successRes) -> successRes.data
          )
          preenrolCache.put(id, prom)
          prom
        )

      coursePreenrols: (courseId) ->
        $http(
          withCredentials: true
          method: "GET"
          url: "#{AssessoryConfig.apiBase}/course/#{courseId}/preenrols"
        ).then((res) -> res.data)

    }
  ]

  angular.module('assessory.course').service 'CourseService', service

