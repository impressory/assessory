define [ "./module" ], () ->

  directive = () -> {
      restrict: "E"
      scope: { task: "=" }
      template: """
        <crit-all-allocations task="task"></crit-all-allocations>
      """

    }

  angular.module("assessory.critique").directive "critiqueAdmin", directive