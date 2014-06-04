
//Define an angular module for our app
var scrumkin = angular.module('scrumkin', []);



//Define Routing for app
scrumkin.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/ProjWall', {
                templateUrl: 'app/views/ProjectWall.html',
                controller: 'ProjWall'
            }).
            when('/Login', {
                templateUrl: 'app/views/Login.html',
                controller: 'Login'
            }).
            when('/AdminConsole', {
                templateUrl: 'app/views/AdminConsole.html',
                controller: 'AdminConsole'
            }).
            when('/ScrumMasterConsole', {
                templateUrl: 'app/views/ScrumMasterConsole.html',
                controller: 'ScrumMasterConsole'
            }).
            when('/404', {
                templateUrl: 'app/views/404.html',
                controller: ''
            }).
            otherwise({
                redirectTo: '404'
            });
    }]);


    //resource za pridobivanje user token-a --> ali je prijavljen
    scrumkin.factory('User', ['$resource',
    function($resource) {
        return $resource('/api/user/:userId', {}, {
            getFromLoginToken: {method: 'GET', url: '/api/login'}
        });
    }]);