/*
 * Require.js set of modules for various info style pages.
 * (At the moment we're just including everything!)
 */
require([
  "modules/base",  
  
  "services/UserService",
  "services/CourseService",
  "services/GroupService",
  "services/TaskService",
  "services/GroupCritService",
  
  "components/SiteHeader",
  "components/MyCourses",
  
  "controllers/login/LogIn",
  "controllers/login/SignUp",
  "controllers/login/Self",
  "controllers/login/UserInfo",
    
  "controllers/course/Create",
  "controllers/course/View",
  "controllers/course/Admin",
  "controllers/course/CreatePreenrol",
  "controllers/course/ViewPreenrol",
  "controllers/course/FindMyCourses",

  "controllers/group/View",
  "controllers/group/CreateGroupSet",
  "controllers/group/CreateGPreenroll",
  "controllers/group/ViewGroupSet",

  "controllers/task/TaskInfo",
  "controllers/task/View",
  "controllers/task/Admin",

  "controllers/groupcrit/CreateTask",
  "controllers/groupcrit/View",
  "controllers/groupcrit/Edit",
  
  "controllers/question/EditQuestionnaire",

  "modules/app"
  
], function(l) {
	console.log("required scripts have loaded")
	
	angular.bootstrap(document, ['assessory'])
})