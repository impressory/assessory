define(["./base"], (l) ->

  Assessory.controllers.outputcrit.CreateTask = ["$scope", "CourseService", "GroupService", "TaskService", "$location", "course", ($scope, CourseService, GroupService, TaskService, $location, course) ->    

    $scope.course = course
    
    $scope.tasks = TaskService.courseTasks(course.id)
    
    $scope.errors = []    
    
    $scope.task = {
    
      details: {}
    
      body: {
        kind: "Task output critique"
        questionnaire: {
          questions: []
        }
      
      }
    }
    
    $scope.submit = (task) -> 
      $scope.errors = [ ]
      TaskService.create(course.id, task).then(
       (task) -> $location.path("/task/#{task.id}")           
       (fail) -> $scope.errors = [ fail.data?.error || "Unexpected error" ]
      )
  ]

  Assessory.controllers.outputcrit.CreateTask.resolve = {
    course: ['$route', 'CourseService', ($route, CourseService) -> 
      CourseService.get($route.current.params.courseId)
    ]
  }
)