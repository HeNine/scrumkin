//kontroler za obnašanje about strani
scrumkin.controller('Login', function($scope, $route, $http, $rootScope, $location, $anchorScroll, User) {
    $rootScope.userLogged = null;
    // create a blank object to hold our form information $scope will allow this to pass between controller and view
    $scope.loginFormData = {}; 
        
    //naredimo metodo, ki se kliče ko je login form submitan (na form smo določil ng-submit)
    $scope.processLoginForm = function(){
        
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
                        getUserToken();
                        //$rootScope.userLogged = User.getFromLoginToken();
                        
                        //$scope.userLogged = "user success";
                        //$location.path( "/AdminConsole" );
                        
                    }).error(function(data, status, headers, config) {
                        $scope.loginError = "Your login credentials are incorrect!" + status;
                        document.getElementById('uname').focus();
            });
        }
        else{
            $scope.loginError = "Your login credentials are incorrect!";
            document.getElementById('uname').focus();
        }
    };
    
    $scope.getUserToken = function(){
        alert('getting user token');
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