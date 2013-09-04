define(["./UserService"], () ->

  Assessory.services.GroupService = Assessory.angularApp.service('GroupService', ['$http', '$cacheFactory', 'CourseService', ($http, $cacheFactory, CourseService) ->
      
    gsCache = $cacheFactory("gsCache")
    
    groupCache = $cacheFactory("groupCache")
    
    {
    
      createGroupSet: (courseId, groupSet) -> $http.post("/course/#{courseId}/createGroupSet", groupSet).then((res) -> 
        gs = res.data
        gsCache.put(gs.id, gs)
        gs
      )
    
      getGroupSet: (id) ->         
        gsCache.get(id) || ( 
          prom = $http.get("/groupSet/#{id}").then(
            (successRes) -> successRes.data
          )
          gsCache.put(id, prom)
          prom
        )
                
      courseGroupSets: (courseId) -> 
        $http.get("/course/#{courseId}/groupSets").then((res) -> res.data)
    }
  ])

)