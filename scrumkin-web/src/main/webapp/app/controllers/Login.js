//kontroler za obnašanje about strani
scrumkin.controller('Login', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService) {
    $rootScope.currentProjectName = 'No project selected';
    
    $rootScope.activeSprint = {};   //aktiven sprint tega projekta
    $rootScope.activeSprintExists = false;
    $rootScope.activeSprintID = -1;
    $rootScope.activeSprintStart = '';
    $rootScope.activeSprintEnd = '';
    
    $rootScope.isProductOwner = false;
    $rootScope.isScrumMaster = false;
    $rootScope.currentProjectRole = '/';
    $rootScope.usertypeDesc = 'User';
    
    //preverimo, ce je uporabnik logiran    
    $rootScope.userLogged = false;
    $scope.user = LoggedUserService.getUserToken();
    if( typeof $scope.user === 'undefined'){
        $rootScope.userLogged = false;
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
        $location.path( "/ChooseProject" );
    }
    
    /*-----------------------------------------------------------*/
    
    
    $scope.loginFormData = {};      
    $scope.loginLoader = false;
    //naredimo metodo, ki se kliče ko je login form submitan (na form smo določil ng-submit)
    $scope.processLoginForm = function(){
        $scope.loginLoader = true;
        //validacija na strani klienta
        var username = $scope.loginFormData["uname"];
        var password = $scope.loginFormData["pass"];
        var klientOK = validateOnClient(username, password);
        $scope.userOK = klientOK[0];
        $scope.passOK = klientOK[1];
        
        if($scope.userOK && $scope.passOK){
            $scope.loginError = false;  //skrijemo opozorilo
                            
            $http({
                method: 'POST',
                url: '/api/login',
                data: $.param({username: username, password: password}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
                    .success(function(data, status, headers, config) {
                        //šlo skos, mamo cookie whatevs
                        $scope.loginError = null;
                        $scope.userLogged = true;
                        
                        $scope.loginLoader = false;    
                        
                        $location.path( "/ChooseProject" );
                        
                    }).error(function(data, status, headers, config) {
                        $scope.loginLoader = false;
    
                        $scope.loginError = "Your login credentials are incorrect! (" + status+")";
                        document.getElementById('uname').focus();
            });
        }
        else{
            $scope.loginError = "Your login credentials are incorrect!";
            document.getElementById('uname').focus();
            $scope.loginLoader = false;
        }
    };
    
});

function validateOnClient(uname, pass){
    
    var usernameRegex = /^[a-zA-Z0-9]{4,20}$/; //dovoli vse crke in stevilke, dolzine 4 - 20
    var passRegex = /^[a-zA-Z0-9#$%&!?@]{4,256}$/;
    
    var unameOK = validateEntry(uname, usernameRegex);
    var passOK = validateEntry(pass, passRegex);
    
    var arr = [unameOK, passOK];
    return arr;
}

function validateEntry(stringEntry, regex){
    if(typeof stringEntry !== "undefined"){
        if(stringEntry.match(regex)){
            return true;
        }
        else{ return false; }
    }
    else{
        return false;
    }
}