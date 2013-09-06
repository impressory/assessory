define(["./UserService"], () ->

  Assessory.services.GroupService = Assessory.angularApp.service('GroupService', ['$http', '$cacheFactory', 'CourseService', ($http, $cacheFactory, CourseService) ->
      
    gsCache = $cacheFactory("gsCache")
    
    groupCache = $cacheFactory("groupCache")
    
    {
    
      get: (id) ->         
        groupCache.get(id) || ( 
          prom = $http.get("/group/#{id}").then(
            (successRes) -> successRes.data
          )
          groupCache.put(id, prom)
          prom
        )
    
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
        
      byGroupSet: (groupSetId) ->
        $http.post("/groupSet/#{groupSetId}/groups").then((res) -> res.data)
                
      courseGroupSets: (courseId) -> 
        $http.get("/course/#{courseId}/groupSets").then((res) -> res.data)
        
      createGroupPreenrol: (groupSetId, gpreenrol) ->
        $http.post("/groupSet/#{groupSetId}/createGPreenrol", gpreenrol).then((res) -> res.data)
        
      myGroups: (courseId) ->
        $http.get("/course/#{courseId}/group/my").then((res) -> res.data)
    }
  ])

)