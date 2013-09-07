define(["./UserService"], () ->

  Assessory.services.GroupCritService = Assessory.angularApp.service('GroupCritService', ['$http', '$cacheFactory', 'CourseService', ($http, $cacheFactory, CourseService) ->
      
    
    {
    
      myAllocations: (taskId) -> 
        $http.get("/groupcrit/#{taskId}/myAllocations").then((res) -> res.data)

      allAllocations: (taskId) -> 
        $http.get("/groupcrit/#{taskId}/allocations").then((res) -> res.data)

    }
  ])

)