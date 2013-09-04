define(["modules/base"], () ->

  Assessory.services.UserService = Assessory.angularApp.service('UserService', ['$http', '$cacheFactory', ($http, $cacheFactory) ->
      
    cache = $cacheFactory("userCache")
    
    {
    
      # Fetches a JSON representation of the user themselves
      self: () ->
        cache.get("self") || ( 
          prom = $http.get("/self").then(
            (successRes) -> successRes.data
          )
          cache.put("self", prom)
          prom
        )
        
      signUp: (json) -> $http.post("/signUp", json).then((res) -> 
        user = res.data
        cache.put("self", user)
        user
      )
      
      logIn: (json) -> $http.post("/logIn", json).then((res) -> 
        user = res.data
        cache.put("self", user)
        user
      ) 
    
      logOut: () -> 
        cache.remove("self")
        $http.post("/logOut").then(
          (res) -> null
          (res) -> null
        )
    }
  ])

)