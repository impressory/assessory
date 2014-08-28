define [ "./module" ], () ->


  service = [ "$http", "$cacheFactory", "AssessoryConfig", ($http, $cacheFactory, AssessoryConfig) ->
    {
      allocateTask: (taskId) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/critique/#{taskId}/allocate"
        ).then(
          (successRes) -> successRes.data
        )

      myAllocations: (taskId) ->
        $http(
          withCredentials: true
          method: 'GET'
          url: "#{AssessoryConfig.apiBase}/critique/#{taskId}/myAllocations"
        ).then(
          (successRes) -> successRes.data
        )

      allAllocations: (taskId) ->
        $http(
          withCredentials: true
          method: 'GET'
          url: "#{AssessoryConfig.apiBase}/critique/#{taskId}/allocations"
        ).then(
          (successRes) -> successRes.data
        )

      getCritique: (taskId, target) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/critique/#{taskId}/findOrCreateCrit"
          data: target
        ).then(
          (successRes) -> successRes.data
        )
    }
  ]

  angular.module('assessory.task').service 'CritiqueService', service
