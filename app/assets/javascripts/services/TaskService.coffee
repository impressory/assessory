define(["./UserService"], () ->

  Assessory.services.TaskService = Assessory.angularApp.service('TaskService', ['$http', '$cacheFactory', 'CourseService', ($http, $cacheFactory, CourseService) ->
      
    gsCache = $cacheFactory("gsCache")
    
    groupCache = $cacheFactory("groupCache")
    
    {
    
      createGroupSet: (courseId, groupSet) -> $http.post("/course/#{courseId}/groupSet/create", groupSet).then((res) -> 
        gs = res.data
        cache.put(gs.id, gs)
        gs
      )
    
      getGroupSet: (id) ->         
        cache.get(id) || ( 
          prom = $http.get("/groupSet/#{id}").then(
            (successRes) -> successRes.data
          )
          cache.put(id, prom)
          prom
        )
                
      courseGroupSets: (courseId) -> 
        $http.get("/course/#{courseId}/groupSets").then((res) -> res.data)
    }
  ])

)