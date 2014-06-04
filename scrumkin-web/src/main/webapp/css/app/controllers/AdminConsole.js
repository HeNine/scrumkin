//kontroler za obnašanje about strani
scrumkin.controller('AdminConsole', function($scope, $route, $http, $rootScope, $location, $anchorScroll) {
    $scope.activeTab = 'newUser';
    
    /*za prikazovanje/skrivanje elementov */
    $scope.showConfirmed = false;
    
    /* ------------  KODA ZA TAB newUSER ------------------------------------ */
    $scope.newUserData = {};
    $scope.saveNewUser = function(){
        //ali so vsi podatki vnešeni
        var dataLen = Object.keys($scope.newUserData).length;
        if(dataLen === 6){
            //vse vneseno
            $scope.missingData = false;
            
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
            
            if($scope.emailOK && $scope.passMatch){
                //vse ok - REST za shranjevanje uporabnika
                $scope.userSavedSuccess = false;        //prikaz ino o uspesnem vnosu uporabnika - == true
            }
        }
        else{
            //prikazemo opozorilo, da so vsi podatki potrebni
            $scope.missingData = true;
        }
        
    };
    
    /* ------------  KODA ZA TAB newProject ------------------------------------ */
     
    /*REST - iz baze poberemo userje in jih prikazemo v levem stolpcu */
    $scope.users = ['user1', 'user2', 'user3', 'user4', 'user5', 'user6', 'user7', 'user8'];
    
    /*---- koda za premikanje uporabnika iz levega v desni stolpec in za odstranjevanje */
    $scope.usersChosen = [];    
    
    /* Funkcija, za dodajanje izbranih uporabnikov */
    $scope.chooseUser = function(user){
        //dodamo uporabnika na seznam na desni, ce ga se ni
        var index = $scope.usersChosen.indexOf(user);
        if(index === -1){
            $scope.usersChosen.push(user);  
        }
    };
    
    /* Funkcija, ki pobrise celoten seznam izbranih uporabnikov na desni */
    $scope.clearUsersList = function(){
        $scope.usersChosen = [];
    };
    
    /* Funkcija, ki odstrani izbranega uporabnika iz desnega seznama */
    $scope.removeUser = function(user2remove){
        var index = $scope.usersChosen.indexOf(user2remove);
        $scope.usersChosen.splice(index, 1);
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
        
        if(!$scope.projErr && !$scope.userNumErr){
            //??? kšna elegantnejša rešitev?? - zdruzimo uporabnike in njhiove vloge
            $scope.usersFinal = {};            
            var els = document.getElementsByClassName('tipUporabnika');
            for(var i=0; i<els.length; i++){
                var user = $scope.usersChosen[i];
                $scope.usersFinal[user] = els[i].value;
            }           
            $scope.showConfirmed = true;
        }
        else{
            $scope.showConfirmed = false;
        }
    };      
    
    $scope.createProject = function(){
        //REST ustvarjanje projekta v bazi
        
        //ime projekta
        //$scope.newProjectData['imeProjekta'];
        
        //uporabniki na projektu in vloge
        //$scope.usersFinal;
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