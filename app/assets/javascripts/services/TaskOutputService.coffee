define(["./UserService"], () ->

  Assessory.services.TaskOutputService = Assessory.angularApp.service('TaskOutputService', ['$http', '$cacheFactory', 'CourseService', ($http, $cacheFactory, CourseService) ->
      
    toCache = $cacheFactory("taskOutputCache")
    
    {
    
      get: (id) ->         
        toCache.get(id) || ( 
          prom = $http.get("/taskoutput/#{id}").then(
            (successRes) -> successRes.data
          )
          toCache.put(id, prom)
          prom
        )
      
      updateBody: (to) -> $http.post("/taskoutput/#{to.id}", to).then((res) -> 
        gs = res.data
        toCache.put(gs.id, gs)
        gs      
      )
      
      relevantToMe: (taskId) -> $http.get("/task/#{taskId}/relevantToMe").then((res) -> res.data)
      
      myOutputs: (taskId) -> $http.get("/task/#{taskId}/myOutputs").then((res) -> res.data)
    }
  ])

)