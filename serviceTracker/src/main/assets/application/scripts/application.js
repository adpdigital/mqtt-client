angular.module('application', ['ngResource'])
.config(['$provide', function($provide){
    $provide.provider('$httpBackend',
        function JSONHttpProvider() {
            this.$get = ['$browser', function($browser) {
                return function(method, url, post, callback, headers, timeout, withCredentials) {
                    callback(200, window.WebApi.get(url));
                }
            }]
        });
}])
.run(['$rootScope', '$log', '$resource',// '$httpProvider',
    function($rootScope, console, $resource) {

        console.log('here');
        var List = $resource('webapi/list/:name', {name:'branches'});
        $rootScope.list = List.get();

    }]);



function loadJSON(){
    var data = window.javascriptInterface.getJSON();
    var json = JSON.parse(data);
    var str = "";
    for(var k in json) {
        str += k + " - " + json[k] + "\n";
    }
    alert(str);
}