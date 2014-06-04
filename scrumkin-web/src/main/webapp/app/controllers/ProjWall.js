//kontroler za obnašanje about strani
scrumkin.controller('ProjWall', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService) {
    $scope.activeTab = 'dailyScrum';
    
    //preverimo, ce je uporabnik logiran    
    $rootScope.userLogged = false;
    $scope.user = LoggedUserService.getUserToken();
    $rootScope.usertypeDesc = 'User';
    
    if( typeof $scope.user === 'undefined'){
        $rootScope.userLogged = false;
        $location.path( "/Login" );
    }
    else{
        $rootScope.userLogged = true;
        $rootScope.userUname = $scope.user['username'];  
        
        //pogledamo njegove pravice
        $rootScope.groups = $scope.user['groups'];
        $rootScope.usertype = $scope.groups[0]; //1: admin, 2:super, 3:normal

        $rootScope.isAdmin = false;
        if($rootScope.usertype === 1){
            $rootScope.isAdmin = true;
            $rootScope.usertypeDesc = 'Admin';
        } 

        $rootScope.isSuper = false;
        if($rootScope.usertype === 2){
            $rootScope.isSuper = true;
            $rootScope.usertypeDesc = 'Super User';
        }
    }
    
    //ALI je izbran projekt za delo? - ce ne, redirect
    if(typeof $rootScope.currentProjectID === 'undefined' || $rootScope.currentProjectID === '' 
            || $rootScope.currentProjectID === null){
        $location.path("/ChooseProject");
    }
    
    /*-----------------------------------------------------------*/ 
      
    
});