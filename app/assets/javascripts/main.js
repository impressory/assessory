/*
 * Require.js set of modules for various info style pages.
 * (At the moment we're just including everything!)
 */
require([
  "app"
], function(l) {
	console.log("library has loaded")
	
	angular.bootstrap(document, ['assessory'])
}, function(err) {
  console.error("Failed loading required scripts")
  console.error(err)
})