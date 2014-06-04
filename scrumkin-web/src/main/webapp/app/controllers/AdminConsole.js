//kontroler za obnašanje about strani
scrumkin.controller('AdminConsole', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService) {
    $scope.activeTab = 'newUser';
    
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
        
        //if not authorised show warning and redirect after
        if($rootScope.isAdmin === false){
            alert('User not authorised to watch this section!');
            $location.path( "/ProjWall" );
        }
        else{
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
    }
    
    /*-----------------------------------------------------------*/
    
    /*za prikazovanje/skrivanje elementov */
    $scope.showConfirmed = false;
    
    /* ------------  KODA ZA TAB newUSER ------------------------------------ */
    $scope.newUserData = {};
    $scope.saveNewUser = function(){
        //ali so vsi podatki vnešeni
        var dataLen = Object.keys($scope.newUserData).length;
        
        $scope.usernameMissing = isStringEmpty($scope.newUserData['username']);
        $scope.nameMissing = isStringEmpty($scope.newUserData['name']);
        //ali je email ustrezen format
        var emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,15}$/;
        $scope.emailOK = validateEntry($scope.newUserData['email'], emailRegex);
        //ali sta passworda enaka
        if($scope.newUserData['password'] === $scope.newUserData['passwordRe']){
            $scope.passMatch = true;
        }
        else{
            $scope.passMatch = false;
        } 
        //ali je izbran usertype
        if(typeof $scope.newUserData['usertype'] === 'undefined' || $scope.newUserData['usertype'] === null || $scope.newUserData['usertype'] === ''){
            $scope.userTypeOK = false;
        }
        else{
            $scope.userTypeOK = true;
        }
        
        if(dataLen === 6 && !$scope.usernameMissing && !$scope.nameMissing && $scope.emailOK && $scope.passMatch && $scope.userTypeOK){
            //vse vneseno
            $scope.missingData = false;
            
            //vse ok - REST za shranjevanje uporabnika
            $scope.userSavedSuccess = false;        //prikaz ino o uspesnem vnosu uporabnika - == true
            $scope.t = [];
            $scope.t.push(parseInt($scope.newUserData['usertype']));
            $scope.newUser = { 
                "username" : $scope.newUserData['username'],
                "password" : $scope.newUserData['password'],
                "email" : $scope.newUserData['email'],
                "name" : $scope.newUserData['name'],
                "groups" :  $scope.t
            };

            $scope.newUserLoader = true;
            $http({
                method: 'POST',
                url: '/api/users/add',
                data: $scope.newUser,
                headers: {'Content-Type': 'application/json'}})
                    .success(function(data, status, headers, config) {
                        $scope.newUserErr = false;
                        $scope.userSavedSuccess = true; 
                        $scope.newUserLoader = false;
                        $scope.emailAlreadyExists = false;
                        
                        //pocistimo opozorila
                        $scope.usernameMissing = false;
                        $scope.nameMissing = false;
                        $scope.emailOK = true;
                        $scope.passMatch = true;
                        $scope.userTypeOK = true;
                        $scope.missingData = false;
                        $scope.newUserData = {};
                    }).error(function(data, status, headers, config) {
                        if(data.trim() === 'Username already taken'){
                            $scope.newUserErr = true;
                        }
                        if(data.trim() === 'User already exists'){
                            $scope.emailAlreadyExists = true;
                        }
                        $scope.newUserLoader = false;
                        $scope.userSavedSuccess = false; 
            });
        }
        else{
            //prikazemo opozorilo, da so vsi podatki potrebni
            $scope.missingData = true;
            $scope.userSavedSuccess = false; 
        }
        
    };
    
    $scope.removeNewUserMsg = function(){
        $scope.usernameMissing = false;
        $scope.nameMissing = false;
        $scope.emailOK = true;
        $scope.passMatch = true;
        $scope.userTypeOK = true;
        $scope.missingData = false;
        $scope.userSavedSuccess = false;
        $scope.newUserErr = false;
        $scope.emailAlreadyExists = false;
    };
    
    /* ------------  KODA ZA TAB newProject ------------------------------------ */
     
    $scope.removeNewProjectMsg = function(){
        $scope.projectSavedSuccess = false;
        $scope.newProjErr = false;
        $scope.projErr = false;
        $scope.userNumErr = false;
        $scope.spOK = true;
        $scope.poOK = true;
    };
     
    /*REST - iz baze poberemo userje in jih prikazemo v levem stolpcu */
    $scope.usersFromDB = {};
    $scope.getAllUsers = function(){
        $http({ 
            method: 'GET', 
            url: '/api/users'})
        .success(function(data, status, headers, config) {
            $scope.usersFromDB = data;
        }).error(function(data, status, headers, config) {
            //err
        });
    };
    
    
    /*---- koda za premikanje uporabnika iz levega v desni stolpec in za odstranjevanje */
    $scope.usersChosen = [];    
    $scope.usersChosenNames = [];
    /* Funkcija, za dodajanje izbranih uporabnikov */
    $scope.chooseUser = function(id, user){
        //dodamo uporabnika na seznam na desni, ce ga se ni
        var index = $scope.usersChosen.indexOf(id);
        if(index === -1){
            $scope.usersChosen.push(id);  
            $scope.usersChosenNames.push(user);  
        }
    };
    
    /* Funkcija, ki pobrise celoten seznam izbranih uporabnikov na desni */
    $scope.clearUsersList = function(){
        $scope.usersChosen = [];
        $scope.usersChosenNames = [];
    };
    
    /* Funkcija, ki odstrani izbranega uporabnika iz desnega seznama */
    $scope.removeUser = function(user2remove){
        var index = $scope.usersChosenNames.indexOf(user2remove);
        $scope.usersChosen.splice(index, 1);
        $scope.usersChosenNames.splice(index, 1);
    };     
    
    
    /* --- Potrditev podatkov za nov projekt in prikaz summary-ja */
    $scope.newProjectData = {};
    $scope.confirmSummary = function(){

        //ali je izpolnjeno ime projekta
        if($scope.newProjectData['imeProjekta'] === null || $scope.newProjectData['imeProjekta'] === ''
                || typeof $scope.newProjectData['imeProjekta'] === 'undefined'){
            $scope.projErr = true;
        }
        else{
            $scope.projErr = false;
        }
        
        //ali je na projektu vsaj en uporabnik
        if($scope.usersChosen.length > 0){
            $scope.userNumErr = false;
        }
        else{
            $scope.userNumErr = true;
        }
        
        //ce je na projektu vsaj en uporabnik in je ime ok - ali so vloge ok?
        if(!$scope.projErr && !$scope.userNumErr){
            $scope.developers = [];
            $scope.scrumMaster = -1;
            $scope.productOwner = -1;
            
            $scope.noOfProductOwners = 0;//counter za produktno vodjo
            $scope.noOfScrumMasters = 0;//counter za SM
            
            var els = document.getElementsByClassName('tipUporabnika');            
            for(var i=0; i<els.length; i++){
                if(els[i].value === "1"){
                    //team member
                    $scope.developers.push($scope.usersChosen[i]);
                }
                else if(els[i].value === "2"){ 
                    //scrum master in NE team member
                    $scope.scrumMaster = parseInt($scope.usersChosen[i]);
                    $scope.noOfScrumMasters++;
                }
                else if(els[i].value === "3"){ 
                    //product owner in NE team member
                    $scope.productOwner = parseInt($scope.usersChosen[i]);
                    $scope.noOfProductOwners++;
                }
                else if(els[i].value === "5"){ 
                    //scrum master IN team member
                    $scope.scrumMaster = parseInt($scope.usersChosen[i]);
                    $scope.developers.push($scope.usersChosen[i]);  //damo ga se med developerje
                    $scope.noOfScrumMasters++;
                }
                else{
                    //product owner IN team member
                    $scope.productOwner = parseInt($scope.usersChosen[i]);
                    $scope.developers.push($scope.usersChosen[i]);  //damo ga se med developerje
                    $scope.noOfProductOwners++;
                }
            }    
            
            //ali ima projekt samo enega scrum masterja in samo enega product ownerja?
            $scope.spOK = true;
            $scope.spErr = '';
            if($scope.noOfScrumMasters === 0){
                //ni scrum masterja
                $scope.spOK = false;
                $scope.spErr = 'You have to select a Scrum Master!';
                $scope.showConfirmed = false;
            }
            else if($scope.noOfScrumMasters > 1){
                //več scrum masterjev
                $scope.spOK = false;
                $scope.spErr = 'Only one Scrum Master can be assigned to project!';
                $scope.showConfirmed = false;
            }
            
            $scope.poOK = true;
            $scope.poErr = '';
            if($scope.noOfProductOwners === 0){
                //ni product ownerja
                $scope.poOK = false;
                $scope.poErr = 'You have to select a Product Owner!';
                $scope.showConfirmed = false;
            }
            else if($scope.noOfProductOwners > 1){
                //več PO-jev
                $scope.poOK = false;
                $scope.poErr = 'Only one Product Owner can be assigned to project!';
                $scope.showConfirmed = false;
            }
            
            //vloge OK
            if($scope.spOK && $scope.poOK){
                $scope.showConfirmed = true;
                //klicemo create project
                $scope.createProject();
            }
        }
        else{
            $scope.showConfirmed = false;
        }
    };      
    
    $scope.createProject = function(){
        //REST ustvarjanje projekta v bazi   
        $scope.projectSavedSuccess = false;        //prikaz ino o uspesnem vnosu uporabnika - == true
        $scope.newProjErr = false; 
        $scope.imeProjekta = $scope.newProjectData['imeProjekta'].toString();
        
        $scope.newProject = { 
            "name" : $scope.imeProjekta.toLowerCase(),
            "productOwner" : parseInt($scope.productOwner),
            "scrumMaster" : parseInt($scope.scrumMaster),
            "developers" : $scope.developers
        };
        
        $scope.newProjLoader = true;
        $http({
            method: 'POST',
            url: '/api/project',
            data: $scope.newProject,
            headers: {'Content-Type': 'application/json'}})
            .success(function(data, status, headers, config) {
                $scope.projectSavedSuccess = true;
                $scope.newProjLoader = false;
            }).error(function(data, status, headers, config) {
                $scope.newProjErr = true;
                $scope.newProjLoader = false;
        });
    };
    
});

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

function isStringEmpty(str){
    var isEmpty = (!str || 0 === str.length);
    var isBlank = (!str || /^\s*$/.test(str));
    return isEmpty && isBlank;
}