/*
 * Require.js set of modules for various info style pages.
 * (At the moment we're just including everything!)
 */
require([
  "modules/base",
  
  "components/SiteHeader",
  
  "modules/app"
  
], function(l) {
	console.log("required scripts have loaded")
	
	angular.bootstrap(document, ['assessory'])
})