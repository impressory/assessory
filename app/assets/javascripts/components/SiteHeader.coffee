define(["modules/base"], (l) -> 

  Assessory.angularApp.directive("siteHeader", () -> 
    {
      restrict: 'E'
      templateUrl: "directive_siteHeader.html"
    }
  )

)