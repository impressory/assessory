
define ["./group"], () ->


  service = [ "$http", "$cacheFactory", "CourseService", "AssessoryConfig", ($http, $cacheFactory, CourseService, AssessoryConfig) ->

    gsCache = $cacheFactory("gsCache")

    groupCache = $cacheFactory("groupCache")

    {

      get: (id) ->
        groupCache.get(id) || (
          prom = $http(
            withCredentials: true
            method: 'GET'
            url: "#{AssessoryConfig.apiBase}/group/#{id}"
          ).then(
            (successRes) -> successRes.data
          )
          groupCache.put(id, prom)
          prom
        )

      createGroupSet: (courseId, groupSet) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/course/#{courseId}/createGroupSet"
          data: groupSet
        ).then((res) ->
          gs = res.data
          gsCache.put(gs.id, gs)
          gs
        )

      findMany: (ids) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/group/findMany"
          data: { ids: ids }
        ).then(
            (successRes) ->
              data = successRes.data
              for group in data
                groupCache.put(group.id, group)
              data
          )

      getGroupSet: (id) ->
        gsCache.get(id) || (
          prom = $http(
            withCredentials: true
            method: 'GET'
            url: "#{AssessoryConfig.apiBase}/groupSet/#{id}"
          ).then(
            (successRes) -> successRes.data
          )
          gsCache.put(id, prom)
          prom
        )

      byGroupSet: (groupSetId) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/groupSet/#{groupSetId}/groups"
        ).then((res) -> res.data)

      courseGroupSets: (courseId) ->
        $http(
          withCredentials: true
          method: 'GET'
          url: "#{AssessoryConfig.apiBase}/course/#{courseId}/groupSets"
        ).then((res) -> res.data)

      uploadGroups: (groupSetId, uploadData) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/groupSet/#{groupSetId}/uploadGroups"
          data: uploadData
        ).then((res) -> res.data)

      createGroupPreenrol: (groupSetId, gpreenrol) ->
        $http(
          withCredentials: true
          method: 'POST'
          url: "#{AssessoryConfig.apiBase}/groupSet/#{groupSetId}/createGPreenrol"
          data: greenrol
        ).then((res) -> res.data)

      myGroups: (courseId) ->
        $http(
          withCredentials: true
          method: 'GET'
          url: "#{AssessoryConfig.apiBase}/course/#{courseId}/group/my"
        ).then((res) -> res.data)
    }

  ]

  angular.module('assessory.group').service 'GroupService', service