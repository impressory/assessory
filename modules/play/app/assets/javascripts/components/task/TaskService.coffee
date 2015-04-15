define ["./task"], () ->

  service = [ "$http", "$cacheFactory", "CourseService", "AssessoryConfig", ($http, $cacheFactory, CourseService, AssessoryConfig) ->

    taskCache = $cacheFactory("taskCache")

    {

      get: (id) ->
        taskCache.get(id) || (
          prom = $http(
            withCredentials: true
            method: 'GET'
            url: "#{AssessoryConfig.apiBase}/task/#{id}"
          ).then(
            (successRes) -> successRes.data
          )
          taskCache.put(id, prom)
          prom
        )

      create: (courseId, task) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/course/#{courseId}/task/create"
          data: task
        ).then((res) ->
          gs = res.data
          taskCache.put(gs.id, gs)
          gs
        )

      updateBody: (task) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/task/#{task.id}/body"
          data: task
        ).then((res) ->
          gs = res.data
          taskCache.put(gs.id, gs)
          gs
        )

      courseTasks: (courseId) ->
        $http(
          withCredentials: true
          method: 'GET'
          url: "#{AssessoryConfig.apiBase}/course/#{courseId}/tasks"
        ).then((res) -> res.data)

    }

  ]

  angular.module('assessory.task').service 'TaskService', service