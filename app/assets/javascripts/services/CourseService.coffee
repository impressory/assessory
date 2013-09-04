define(["./UserService"], () ->

  Assessory.services.CourseService = Assessory.angularApp.service('CourseService', ['$http', '$cacheFactory', 'UserService', ($http, $cacheFactory, UserService) ->
      
    cache = $cacheFactory("courseCache")
    preenrolCache = $cacheFactory("preenrolCache")
    
    {
    
      create: (course) -> $http.post("/course/create", course).then((res) -> 
        course = res.data
        cache.put(course.id, course)
        course
      )
    
      get: (id) ->         
        cache.get(id) || ( 
          prom = $http.get("/course/#{id}").then(
            (successRes) -> successRes.data
          )
          cache.put(id, prom)
          prom
        )
        
      findMany: (ids) -> 
        $http.post("/course/findMany", { ids: ids }).then(
            (successRes) -> successRes.data
          )
        
      my: () -> 
        UserService.self().then((user) =>
          if user.registrations?.length > 0
            ids = (reg.course for reg in user.registrations)
            @findMany(ids)
          else
            []
        )
        
      createPreenrol: (courseId, preenrol) -> $http.post("/course/#{courseId}/createPreenrol", preenrol).then((res) -> 
        d = res.data
        preenrolCache.put(d.id, d)
        d
      )

      getPreenrol: (id) ->         
        preenrolCache.get(id) || ( 
          prom = $http.get("/preenrol/#{id}").then(
            (successRes) -> successRes.data
          )
          preenrolCache.put(id, prom)
          prom
        )

      coursePreenrols: (courseId) -> 
        $http.get("/course/#{courseId}/preenrols").then((res) -> res.data)
    }
  ])

)