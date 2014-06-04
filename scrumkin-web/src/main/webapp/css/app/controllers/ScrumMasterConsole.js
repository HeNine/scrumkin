//kontroler za obnašanje about strani
scrumkin.controller('ScrumMasterConsole', function($scope, $route, $http, $rootScope, $location, $anchorScroll) {
    $scope.activeTab = 'newSprint';
    
    
    /* ------------------ KODA ZA NEW SPRINT TAB ----------------------------- */
    
    $scope.newSprintData = {};
    $scope.saveNewSprint = function(){
        $scope.beginDate = document.getElementById('beginDate').value;
        $scope.endDate = document.getElementById('endDate').value;
                
        //ali je string velocity number --> format xx.yy...y --> decimalke nato zaokrozim na 1
        if(typeof $scope.newSprintData['sprintVelocity'] === 'undefined'){
            $scope.decErr = true;
        }
        else{
            if ($scope.newSprintData['sprintVelocity'].indexOf(",") > -1){
                $scope.decErr = true;
            }
            else{
                $scope.decErr = false;
            }
        }
        
        //ali sta datuma ok --> begin < end in sta oba vnešena
        if($scope.beginDate === "" || $scope.beginDate === null){
            $scope.beginDateErr = true;
        }
        else{
            $scope.beginDateErr = false;
        }
        
        if($scope.endDate === "" || $scope.endDate === null){
            $scope.endDateErr = true;
        }
        else{
            $scope.endDateErr = false;
        }
        
        if(!$scope.beginDateErr && !$scope.endDateErr){
            //TODO - ali sta datuma OK?
            $scope.datesOK = false;
        }
        
        //če je vse ok
        if($scope.datesOK && !$scope.decErr && !$scope.beginDateErr && !$scope.endDateErr){
            //TODO vse OK --> REST shranjevanje
            
            $scope.sprintSaveSuccess = true;
        }
    };    
    
   
    /* ------------------ KODA ZA EDIT SPRINT TAB/ADD STORY TO SPRINT ----------------------------- */
    
    //REST - vsi sprinti iz baze
    $scope.sprints = ['sprint1','sprint2','sprint3'];
    
    //REST - vse zgodbe za dodajanje v sprint    
    $scope.stories = ['s1', 's2', 's3', 's4'];
        
    $scope.storiesChosen = [];      
    //funkcija za dodajanje story-jev na desno stran
    $scope.chooseStory = function(story){
        //dodamo story na seznam na desni, ce ga se ni
        var index = $scope.storiesChosen.indexOf(story);
        if(index === -1){
            $scope.storiesChosen.push(story);  
        }
    };
    
    /* Funkcija, ki pobrise celoten seznam izbranih story-jev na desni */
    $scope.clearStoriesList = function(){
        $scope.storiesChosen = [];
    };
    
    /* Funkcija, ki odstrani izbran story iz desnega seznama */
    $scope.removeStory = function(sc2remove){
        var index = $scope.storiesChosen.indexOf(sc2remove);
        $scope.storiesChosen.splice(index, 1);
    };
    
    /* --- Potrditev podatkov za urejanje sprinta in prikaz summary-ja */
    $scope.editSprintData = {};
    $scope.confirmStoriesSummary = function(){
        //TODO - isto kot v newProject
        
    };
    
    $scope.updateSprint = function(){
        //TODO -REST update-anje sprinta v bazi
        
    };
    
    
    
    /* ------------------ KODA ZA NEW STORY TAB ----------------------------- */
    
    /* za prikazovanje dodatnih polj za sprejemne teste*/
    $scope.acceptanceTestsNumber = [1,2];
    $scope.addTestField = function(){
        var next = $scope.acceptanceTestsNumber.length+1;
         $scope.acceptanceTestsNumber.push(next);
    };
    
    //REST - vsi obstoječi projekti iz baze
    $scope.projects = ['p1','p2','p3', 'p8'];    
    $scope.newStory = {};   //podatki o novem story-ju
    //TODO - dodanje vsebine acceptance testov v newStory{}
    
    $scope.saveNewStory = function(){
        //TODO - preveri ce so podatki ok + alerti
        
        //REST - shranjevanje story-ja
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