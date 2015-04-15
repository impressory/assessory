'use strict';
define ["./user"], () ->

  UserService = [ "$http", "$cacheFactory", "$q", "AssessoryConfig", ($http, $cacheFactory, $q, AssessoryConfig) ->

    cache = $cacheFactory("userCache")

    {

      # Fetches a JSON representation of the user themselves
      self: () ->
        cache.get("self") || (
          prom = $http(
            withCredentials: true
            method: "POST"
            url: "#{AssessoryConfig.apiBase}/self"
          ).then(
            (successRes) -> successRes.data
          )
          cache.put("self", prom)
          prom
        )

      get: (id) ->
        cache.get(id) || (
          prom = $http(
            withCredentials: true
            method: "GET"
            url: "#{AssessoryConfig.apiBase}/user/#{id}"
          ).then(
            (successRes) -> successRes.data
          )
          cache.put(id, prom)
          prom
        )

      findMany: (ids) ->
        $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/user/findMany"
          data: { ids: ids }
        ).then(
            (successRes) ->
              data = successRes.data
              for user in data
                cache.put(user.id, user)
              data
          )

      forgetSelf: () -> cache.remove("self")

      signUp: (json) ->
        prom = $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/signUp"
          data: json
        ).then((res) -> res.data)
        cache.put("self", prom)
        prom


      logIn: (json) ->
        prom = $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/logIn"
          data: json
        ).then((res) -> res.data)
        cache.put("self", prom)
        prom

      logOut: () ->
        cache.remove("self")
        $http(
          withCredentials: true
          method: "POST"
          url: "#{AssessoryConfig.apiBase}/logOut"
        ).then(
          (res) -> null
          (res) -> null
        )
    }

  ]

  angular.module('assessory.user').service 'UserService', UserService