//kontroler za obnašanje about strani
scrumkin.controller('Logout', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService) {
    
    $scope.processLogout = function(){
        $http({
            method: 'DELETE', 
            url: '/api/login'})
        .success(function(data, status, headers, config) {
            $rootScope.userLogged = false;
            $location.path( "/Login" );
        }).error(function(data, status, headers, config) {
            alert("Error logging out!" + status);
        });
    
    }; 
    
});
