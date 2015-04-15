define [ "./module" ], () ->

  directive = () -> {
      restrict: "E"
      scope: { task: "=" }
      template: """
        <div ng-switch="task.body.strategy.kind">
            <div ng-switch-when="group">
              <crit-all-allocations task="task"></crit-all-allocations>
            </div>
            <div ng-switch-when="critiques of my groups">

            </div>
        </div>
      """

    }

  angular.module("assessory.critique").directive "critiqueAdmin", directive