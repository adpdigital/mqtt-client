angular.module('application').config(['$routeProvider',
    function($routeProvider){
        $routeProvider
            .when("/activities",
                {
                    templateUrl: "views/activityRecords.html"
                })
            .when("/activity/:id",
                {
                    templateUrl: "views/activityRecordEditor.html"
                })
            .otherwise(
                {
                    redirectTo: "/activities"
                });
}]);
