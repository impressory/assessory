define(["./UserService"], () ->

  Assessory.services.GroupCritService = Assessory.angularApp.service('GroupCritService', ['$http', '$cacheFactory', '$location', ($http, $cacheFactory, $location) ->
      
    
    {
    
      allocateTask: (taskId) -> $http.post("/groupcrit/#{taskId}/allocate").then((res) -> res.data)
    
      myAllocations: (taskId) -> 
        $http.get("/groupcrit/#{taskId}/myAllocations").then((res) -> res.data)

      allAllocations: (taskId) -> 
        $http.get("/groupcrit/#{taskId}/allocations").then((res) -> res.data)
        
      createCritique: (allocId, groupId) ->
        $http.post("/groupcritalloc/#{allocId}/createCritFor/#{groupId}").then((res) -> 
          crit = res.data
          crit
        )

    }
  ])

)