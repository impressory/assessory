define [ "./base" ], () ->

  service = [ "$http", "$cacheFactory", "CourseService", "AssessoryConfig", ($http, $cacheFactory, CourseService, AssessoryConfig) ->

    toCache = $cacheFactory("taskOutputCache")

    {

      get: (id) ->
        toCache.get(id) || (
          prom = $http(
            withCredentials: true
            method: "GET"
            url: "#{AssessoryConfig.apiBase}/taskoutput/#{id}"
          ).then(
            (successRes) -> successRes.data
          )
          toCache.put(id, prom)
          prom
        )

      updateBody: (to) ->
        $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/taskoutput/#{to.id}"
          data: to
        ).then(
          (successRes) -> successRes.data
        )

      saveNew: (to) ->
        $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/task/#{to.task}/newOutput"
          data: to
        ).then((res) ->
          gs = res.data
          toCache.put(gs.id, gs)
          gs
        )


      save: (to) ->
        if to.id?
          @updateBody(to)
        else
          @saveNew(to)

      relevantToMe: (taskId) ->
        $http(
          withCredentials: true
          method: "GET"
          url: "#{AssessoryConfig.apiBase}/task/#{taskId}/relevantToMe"
        ).then((res) -> res.data)

      myOutputs: (taskId) ->
        $http(
          withCredentials: true
          method: "GET"
          url: "#{AssessoryConfig.apiBase}/task/#{taskId}/myOutputs"
        ).then((res) -> res.data)
    }

  ]

  angular.module('assessory.task').service 'TaskOutputService', service