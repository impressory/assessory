/*
 * Require.js set of modules for various info style pages.
 * (At the moment we're just including everything!)
 */
require([
  "modules/base",  
  
  "services/UserService",
  "services/CourseService",
  "services/GroupService",
  
  "components/SiteHeader",
  "components/MyCourses",
  
  "controllers/login/LogIn",
  "controllers/login/SignUp",
  "controllers/login/Self",
    
  "controllers/course/Create",
  "controllers/course/View",
  "controllers/course/CreatePreenrol",
  "controllers/course/ViewPreenrol",
  "controllers/course/FindMyCourses",

  "controllers/group/CreateGroupSet",
  "controllers/group/CreateGPreenroll",
  "controllers/group/ViewGroupSet",

  "modules/app"
  
], function(l) {
	console.log("required scripts have loaded")
	
	angular.bootstrap(document, ['assessory'])
})