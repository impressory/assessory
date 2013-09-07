define(["./UserService"], () ->

  Assessory.services.GroupCritService = Assessory.angularApp.service('GroupCritService', ['$http', '$cacheFactory', 'CourseService', ($http, $cacheFactory, CourseService) ->
      
    
    {
    
      allocateTask: (taskId) -> $http.post("/groupcrit/#{taskId}/allocate").then((res) -> res.data)
    
      myAllocations: (taskId) -> 
        $http.get("/groupcrit/#{taskId}/myAllocations").then((res) -> res.data)

      allAllocations: (taskId) -> 
        $http.get("/groupcrit/#{taskId}/allocations").then((res) -> res.data)

    }
  ])

)