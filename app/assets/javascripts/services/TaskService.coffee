define(["./UserService"], () ->

  Assessory.services.TaskService = Assessory.angularApp.service('TaskService', ['$http', '$cacheFactory', 'CourseService', ($http, $cacheFactory, CourseService) ->
      
    taskCache = $cacheFactory("taskCache")
    
    {
    
      get: (id) ->         
        taskCache.get(id) || ( 
          prom = $http.get("/task/#{id}").then(
            (successRes) -> successRes.data
          )
          taskCache.put(id, prom)
          prom
        )
      
      create: (courseId, task) -> $http.post("/course/#{courseId}/task/create", task).then((res) -> 
        gs = res.data
        taskCache.put(gs.id, gs)
        gs
      )
      
      updateBody: (task) -> $http.post("/task/#{task.id}/body", task).then((res) -> 
        gs = res.data
        taskCache.put(gs.id, gs)
        gs      
      )
      
      courseTasks: (courseId) ->  $http.get("/course/#{courseId}/tasks").then((res) -> res.data)
      
    }
  ])

)