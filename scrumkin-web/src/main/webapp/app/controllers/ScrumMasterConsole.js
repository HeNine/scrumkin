//kontroler za obnašanje about strani
scrumkin.controller('ScrumMasterConsole', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService, 
    AcceptanceTestsService) {
        
    if($rootScope.isProductOwner){
        $scope.activeTab = 'newStory';
    }
    else{
        $scope.activeTab = 'newSprint';
    }
    
    
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
        
        //ALI je izbran projekt za delo? - ce ne, redirect
        if(typeof $rootScope.currentProjectID === 'undefined' || $rootScope.currentProjectID === '' 
                || $rootScope.currentProjectID === null){
            $location.path( "/ChooseProject" );
        }
        else {
            //pogledamo ce je scrum master --> ce ni, redirect
            if (!$rootScope.isScrumMaster && !$rootScope.isProductOwner) {
                alert('You do not have the rights for this project!');
                $location.path("/ProjWall");
            }
            else {
                //pogledamo njegove pravice
                $rootScope.groups = $scope.user['groups'];
                $rootScope.usertype = $scope.groups[0]; //1: admin, 2:super, 3:normal

                $rootScope.isAdmin = false;
                if ($rootScope.usertype === 1) {
                    $rootScope.isAdmin = true;
                    $rootScope.usertypeDesc = 'Admin';
                }

                $rootScope.isSuper = false;
                if ($rootScope.usertype === 2) {
                    $rootScope.isSuper = true;
                    $rootScope.usertypeDesc = 'Super User';
                }
            }
        }
    }
    
    /*-----------------------------------------------------------*/   
    
    /*REST - iz baze poberemo vse projekte in jih prikažemo v dropdown listu  - rabimo v vecih tabih*/
    $scope.allProjectsDB = {};
    $scope.getAllProjects = function(){
        $http({
            method: 'GET', 
            url: '/api/project'})
        .success(function(data, status, headers, config) {
            $scope.allProjectsDB = data;
        }).error(function(data, status, headers, config) {
            Console.log("Error getting all projects from DB: " + status);
        });
    }; 
    
    /* ------------------ KODA ZA NEW SPRINT TAB ----------------------------- */
    $scope.sprintSaveSuccess = false;    
    $scope.newSprintData = {};    
    $scope.saveNewSprint = function(){
        $scope.errFromBackend = false;
        $scope.beginDate = document.getElementById('beginDate').value;
        $scope.endDate = document.getElementById('endDate').value;
        $scope.newSprintData['selectedProjectNS'] = $rootScope.currentProjectID;
        
        //ali je izbran kakšen projekt
        if(typeof $scope.newSprintData['selectedProjectNS'] === 'undefined' || $scope.newSprintData['selectedProjectNS'] === null
                || $scope.newSprintData['selectedProjectNS'] === ''){
            $scope.nsProjErr = true;
        }
        else{
            $scope.nsProjErr = false;
        }
                
        //ali je string velocity positive number --> format xx.yy...y --> decimalke nato zaokrozim na 1
        if(typeof $scope.newSprintData['sprintVelocity'] === 'undefined'){
            $scope.decErr = true;
        }
        else{
            if ($scope.newSprintData['sprintVelocity'].indexOf(",") > -1
                    || $scope.newSprintData['sprintVelocity'].indexOf("-") > -1
                    || !validateEntry($scope.newSprintData['sprintVelocity'], /^\d{0,2}(\.\d{0,2}){0,1}$/)){
                $scope.decErr = true;
            }
            else{
                //zaokrozimo in to prikazemo
                var temp = parseFloat($scope.newSprintData['sprintVelocity']);
                $scope.newSprintData['sprintVelocity'] = temp.toFixed(1);
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
            var dbArray = $scope.beginDate.split('.');
            var dbD = dbArray[0];
            var dbM = dbArray[1];
            var dbY = dbArray[2];
            var begDate = new Date(dbY, dbM-1, dbD);
            
            var deArray = $scope.endDate.split('.');
            var deD = deArray[0];
            var deM = deArray[1];
            var deY = deArray[2];
            var endDate = new Date(deY, deM-1, deD);
            
            if(begDate < endDate){
                $scope.datesOK = true;
            }
            else{
                $scope.datesOK = false;
            }
        }
        
        //če je vse ok
        if(!$scope.nsProjErr && $scope.datesOK && !$scope.decErr && !$scope.beginDateErr && !$scope.endDateErr){
            $scope.newSprintLoader = true;
            
            //treba si je pripravit datum v formatu javascripta nato pa prevorit v ISO za javo            
            var beginDateF = $scope.formatDateForDB($scope.beginDate);
            var endDateF = $scope.formatDateForDB($scope.endDate);            
            
            $scope.errFromBackend = '';
            $scope.newSprint = { 
                "startDate" : beginDateF,
                "endDate" : endDateF,
                "velocity" : parseFloat($scope.newSprintData['sprintVelocity']),
                "projectId" : parseInt(JSON.parse($scope.newSprintData['selectedProjectNS']))
            };
            
            $http({
                method: 'POST',
                url: '/api/sprint',
                data: $scope.newSprint,
                headers: {'Content-Type': 'application/json'}})
                    .success(function(data, status, headers, config) {
                        //alert('Sprint for project' + $scope.newSprintData['selectedProjectNS'] + ' is created.');
                        $scope.sprintSaveSuccess = true;    
                        $scope.errFromBackend = '';
                        $scope.newSprintLoader = false;
                        $scope.newSprintData['sprintVelocity'] = '';
                        
                        //po ustvarjanju sprinta nared reload - da ti danasnji sprint aktivira
                        alert('SUCCESS! The page needs to be reloaded for the changes to take effect!');
                        location.reload();
                    }).error(function(data, status, headers, config) {
                        //alert('Error: ' + data);
                        $scope.errFromBackend = data.trim();
                        $scope.sprintSaveSuccess = false;
                        $scope.newSprintLoader = false;
                });
        }
    };    
    
    $scope.formatDateForDB = function(date2format){
        var t = date2format.toString().split('.');
        var year = parseInt(t[2]);
        var month = parseInt(t[1]);
        var day = parseInt(t[0]);
        if(month < 10){
            month = '0'+month;
        }
        if(day < 10){
            day = '0'+day;
        }
        var dateFormatString = month+'-'+day+'-'+year;
        var formattedDate = new Date(dateFormatString);
        var newFormattedDate = formattedDate.addDays(1);
        return newFormattedDate.toISOString();
    };
    
    
   
    /* ------------------ KODA ZA EDIT SPRINT TAB/ADD STORY TO SPRINT ----------------------------- */
    
    $scope.removeEditSprintWarnings = function(){
        $scope.updateSprintErr = false;
        $scope.updateSprintOK = false;
        $scope.storyNumErr = false;
        $scope.showInactiveWarning = false;
    };
    
    $scope.sprintIsChosen = false;      //enako pri sprintu
    $scope.projID = $rootScope.currentProjectID;
    $scope.editSprintStoryLoader = false;
    
    //Zgodbe dodajamo na trenutno aktiven sprint
    if($rootScope.activeSprintID !== -1 && typeof $rootScope.activeSprintID !== 'undefined'){
        $scope.activeSprintSelected = true;
    }
    else{
        $scope.activeSprintSelected = false;
    }
    
    /*REST - iz baze poberemo vse zgodbe izbranega projekta in jih prikažemo levo spodaj. 
     * Dodajamo lahko le zgodbe, ki ustrezajo kriterijem:
     * - imajo ocenjeno časovno zahtevnost: estimatedTime > 0 
     * - niso realizirane: product owner jih ni potrdiu 
     * - še niso dodeljene aktivnemu sprintu: active sprint id !== story sprint id 
     * Ko uporabnik klikne na katero od osivenih zgodb, se mu pokaze sporocilo zakaj je ne more dodat. 
     */    
    $scope.allStoriesOfProjectDB = {};
    $scope.getAllStoriesOfProject = function(){
        if(typeof $scope.projID !== 'undefined' && $scope.projID !== '' && $scope.projID !== null){
             $scope.editSprintStoryLoader = true;
            $http({ 
                method: 'GET', 
                url: '/api/project/'+$scope.projID+'/stories'})
            .success(function(data, status, headers, config) {
                $scope.allStoriesOfProjectDB = data;                
                if ($scope.activeTab === 'editStory'){      //DODANO ZA EDIT STORY TAB BY KATJA
                    $scope.sortStories1();
                    $scope.getAvailableStories();
                }
                else{
                    $scope.sortStories(); 
                }
                $scope.editSprintStoryLoader = false;
            }).error(function(data, status, headers, config) {
                //alert("Error getting all stories of selected project: " + status);
                $scope.editSprintStoryLoader = false;
            });
        }
        else{
            //undefined project id
        }
    };     
    
    $scope.sortStories1 = function(){
        var dataLen = Object.keys($scope.allStoriesOfProjectDB).length;
        for(var i=0; i<dataLen; i++){
            var currS = $scope.allStoriesOfProjectDB[i];
            
            /*
            //ali je time estimate OK
            var timeEstimateOK = false;
            if(parseFloat(currS['estimatedTime']) > 0){
                var timeEstimateOK = true;
            }*/
            
            //ali je active sprint OK
            var sprintOK = false;
            if(parseInt(currS['sprint']) === 0){
                var sprintOK = true;
                currS['onSprint'] = 'On no sprint';
            }
            else if(parseInt(currS['sprint']) !== $rootScope.activeSprintID){
                var sprintOK = true;
                //Get sprint of this story
                var sprint = $scope.getSprintOfStory(parseInt(currS['sprint']));
                currS['onSprint'] = 'FROM: ' + sprint['startDate'] + " TO: " + sprint['endDate'];
            }
            else{
                //on current sprint
                currS['onSprint'] = 'On active sprint';
            }
            
            //pretvorimo priority v tekstovno obliko
            if(currS['priority'] === 3){
                currS['priorityText'] = 'Must have';
            }
            else if(currS['priority'] === 7){
                currS['priorityText'] = 'Should have';
            }
            else if(currS['priority'] === 6){
                currS['priorityText'] = 'Could have';
            }
            else {
                currS['priorityText'] = "Won't have this time";
            }            
            
            //ali je zgodba potrjena --> NESME BIT, ce jo zelimo dodat v sprint
            currS['finished'] = AcceptanceTestsService.isStoryFinished(currS);
            
            if(currS['finished'] || !sprintOK ){
                currS['inactive'] = true;
            }
            else{
                currS['inactive'] = false;
            }
        }
    };
    
    $scope.sortStories = function(){
        var dataLen = Object.keys($scope.allStoriesOfProjectDB).length;
        for(var i=0; i<dataLen; i++){
            var currS = $scope.allStoriesOfProjectDB[i];
            
            //ali je time estimate OK
            var timeEstimateOK = false;
            if(parseFloat(currS['estimatedTime']) > 0){
                var timeEstimateOK = true;
            }
            
            //ali je active sprint OK
            var sprintOK = false;
            if(parseInt(currS['sprint']) === 0){
                var sprintOK = true;
                currS['onSprint'] = 'On no sprint';
            }
            else if(parseInt(currS['sprint']) !== $rootScope.activeSprintID){
                var sprintOK = true;
                //Get sprint of this story
                var sprint = $scope.getSprintOfStory(parseInt(currS['sprint']));
                currS['onSprint'] = 'FROM: ' + sprint['startDate'] + " TO: " + sprint['endDate'];
            }
            else{
                //on current sprint
                currS['onSprint'] = 'On active sprint';
            }
            
            //pretvorimo priority v tekstovno obliko
            if(currS['priority'] === 3){
                currS['priorityText'] = 'Must have';
            }
            else if(currS['priority'] === 7){
                currS['priorityText'] = 'Should have';
            }
            else if(currS['priority'] === 6){
                currS['priorityText'] = 'Could have';
            }
            else {
                currS['priorityText'] = "Won't have this time";
            }            
            
            //ali je zgodba potrjena --> NESME BIT, ce jo zelimo dodat v sprint
            currS['finished'] = AcceptanceTestsService.isStoryFinished(currS);
            
            if(!timeEstimateOK || currS['finished'] || !sprintOK ){
                currS['inactive'] = true;
            }
            else{
                currS['inactive'] = false;
            }
        }
    };
    
    $scope.getSprintOfStory = function(sprintID){
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/sprint/'+sprintID,
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };
    
    
    $scope.storiesChosen = [];      
    //funkcija za dodajanje story-jev na desno stran
    $scope.chooseStory = function(story){
        $scope.storyNumErr = false;
        //ce je inactive, opozorimo uporabnika
        if(story['inactive'] === true){
            $scope.showInactiveWarning = true;
        }
        else{
            $scope.showInactiveWarning = false;
            //dodamo story na seznam na desni, ce je se ni
            var index = $scope.storiesChosen.indexOf(story);
            if(index === -1){
                $scope.storiesChosen.push(story);  
            }
        }        
    };
    
    /* Funkcija, ki pobrise celoten seznam izbranih story-jev na desni */
    $scope.clearStoriesList = function(){
        $scope.storiesChosen = [];
        $scope.storiesToAssign = [];
    };
    
    /* Funkcija, ki odstrani izbran story iz desnega seznama */
    $scope.removeStory = function(sc2remove){
        var index = $scope.storiesChosen.indexOf(sc2remove);
        $scope.storiesChosen.splice(index, 1);
    };
    
    /* --- Potrditev podatkov za urejanje sprinta in prikaz summary-ja */
    $scope.storiesToAssign = [];
    $scope.confirmStoriesSummary = function(){
        $scope.storiesToAssign = [];
        //ali je dodana vsaj ena zgodba 
        if($scope.storiesChosen.length > 0){
            $scope.storyNumErr = false;
            
            //uredimo zgodbe ki jih bomo assignal
            for(var k=0; k<$scope.storiesChosen.length; k++){
                var cs = $scope.storiesChosen[k];                
                var temp = {
                    "id": cs['id'],
                    "title": cs['title'],
                    "story": cs['story'],
                    "bussinessValue": cs['bussinessValue'],
                    "estimatedTime": cs['estimatedTime'],
                    "acceptenceTests": cs['acceptenceTests'],
                    "tasks": cs['tasks'],
                    "priority": cs['priority'],
                    "project": cs['project'],
                    "sprint": cs['sprint']
                };                
                $scope.storiesToAssign.push(temp);
            }
            
            //VSE OK - pokazemo summary    
            $scope.showConfirmed = true;
            $scope.updateSprint();
        }
        else{
            $scope.storyNumErr = true;
            $scope.showConfirmed = false;
        }        
    };
    
    
    $scope.updateSprint = function(){
        $scope.editSprintLoader = true;
        
        /*REST update-anje sprinta v bazi: PUT /api/userStories/add/sprint/{idSprinta}
         * Podamo seznam JSON story-jev, ki jih assignamo na ta sprint    
         */
        //alert('Backend error: ' + JSON.stringify($scope.storiesToAssign));
        
        //JSON trenutnega aktivnega sprinta: $rootScope.activeSprint        
        if(typeof $rootScope.activeSprint !== 'undefined' && $rootScope.activeSprint !== '' && $rootScope.activeSprint !== null){
           $http({
                method: 'PUT',
                url: '/api/userStories/add/sprint/'+$rootScope.activeSprint['id'],
                data: $scope.storiesToAssign,
                headers: {'Content-Type': 'application/json'}})
                    .success(function(data, status, headers, config) {
                        $scope.updateSprintErr = '';
                        $scope.updateSprintOK = true;
                        $scope.editSprintLoader = false;
                        $scope.storiesToAssign = [];
                    }).error(function(data, status, headers, config) {
                        $scope.updateSprintErr = data;
                        $scope.updateSprintOK = false;
                        $scope.editSprintLoader = false;
                        $scope.storiesToAssign = [];
                });
        }
        else{
            //undefined active sprint
        } 
    };
    
    /* ------------------ KODA ZA NEW STORY TAB ----------------------------- */
    
    $scope.removeWarningsNewStory = function(){
        $scope.testNotUnique = false;
        $scope.storyErrFromBackend = false;
        $scope.storySavedOK = false;
        $scope.missingData = false;
        $scope.noTests = false; 
        $scope.busValErr = false;
        $scope.newStory = {};
    };
    
    $scope.storySavedOK = false;
    
    /* za prikazovanje dodatnih polj za sprejemne teste*/
    $scope.acceptanceTestsNumber = [1,2];
    $scope.addTestField = function(){
        var next = $scope.acceptanceTestsNumber.length+1;
         $scope.acceptanceTestsNumber.push(next);
    };
      
    $scope.newStory = {};   //podatki o novem story-ju    
    $scope.saveNewStory = function(){
        
        //preveri ce so podatki ok
        var len = Object.keys($scope.newStory).length;
        if(len < 4){
            $scope.missingData = true;
        }
        else{
            $scope.missingData = false;
        }
        
        //ali je bussines value stevilka
        if(typeof $scope.newStory['storyBusVal'] !== 'undefined'){
            if(validateEntry($scope.newStory['storyBusVal'], /^\d{0,2}(\.\d{0,2}){0,1}$/)){
                $scope.busValErr = false;
            }
            else{
                $scope.busValErr = true;
            }            
        }
        
        //vnešeni acceptance testi
        $scope.testNotUnique = false;
        var testArray = document.getElementsByClassName('acceptanceTests');
        var acceptanceTests = [];
        for(var i=0; i<testArray.length; i++){
            if(document.getElementsByClassName('acceptanceTests')[i].value !== "" && 
                    document.getElementsByClassName('acceptanceTests')[i].value !== " " &&
                    document.getElementsByClassName('acceptanceTests')[i].value !== null){
                
                var test = document.getElementsByClassName('acceptanceTests')[i].value.toString();
                //ce takega testa se ni v tabeli, ga dodamo, sicer opozorilo!
                if(acceptanceTests.indexOf(test.toLowerCase()) === -1){
                    acceptanceTests.push(test.toLowerCase());
                }
                else{
                    $scope.testNotUnique = true;
                    break;
                }
            }
        }
        
        
        if(acceptanceTests.length === 0){
            $scope.noTests = true;
        }
        else{
            $scope.noTests = false;
        }
        
        $scope.newStory['selectedProject'] = $rootScope.currentProjectID;
        var storyName = $scope.newStory.storyName.toString();
        if(!$scope.missingData && !$scope.busValErr && !$scope.noTests && !$scope.testNotUnique){            
            
            $scope.newStoryLoader = true;
            
            //REST - shranjevanje story-ja            
            $scope.newStory = { 
                "title" : storyName.toLowerCase(),
                "story" : $scope.newStory.storyContent,
                "bussinessValue" : parseInt($scope.newStory['storyBusVal']),
                "estimatedTime" : 0,   //0, ker ga ne definiramo pri ustvarjanju
                "acceptenceTests" : [], //prazen - shranjujemo jih posebej
                "tasks" : [], //prazen, ker pri ustvarjanju jih ne definiramo
                "priority" : parseInt($scope.newStory['storyPriority']),
                "project" : parseInt($scope.newStory['selectedProject']),
                "sprint" : 0 //0, ker ga ne definiramo pri ustvarjanju
            };
                        
            $http({
                method: 'POST',
                url: '/api/userStories/add/backlog',
                data: $scope.newStory,
                headers: {'Content-Type': 'application/json'}})
                    .success(function(data, status, headers, config) {
                        $scope.storyID = data.trim();   //vrne ID nove zgodbe
                        $scope.saveTests($scope.storyID, acceptanceTests);                         
                    }).error(function(data, status, headers, config) {
                        $scope.storyErrFromBackend = data.trim();
                        $scope.storySavedOK = false; 
                        $scope.newStoryLoader = false;
            });                     
        }        
    };    
    
    $scope.saveTests = function(storyID, testArray){
        //REST za shranjevanje acceptance testov - v tabeli acceptanceTests, vsak test posebej
        for(var j=0; j<testArray.length; j++){
            $scope.testPost = { "test" : testArray[j] };
            
            if(typeof storyID !== 'undefined' && storyID !== '' && storyID !== null){
                $http({
                    method: 'POST',
                    url: '/api/userStories/'+storyID+'/tests',
                    data: $scope.testPost,
                    headers: {'Content-Type': 'application/json'}})
                    .success(function(data, status, headers, config) {
                        $scope.storySavedOK = true;    
                        $scope.storyErrFromBackend = '';
                        $scope.newStoryLoader = false;
                    }).error(function(data, status, headers, config) {
                        alert('Error saving tests: ' + status);
                        $scope.storyErrFromBackend = data.trim();
                        $scope.storySavedOK = false; 
                        $scope.newStoryLoader = false;
                });
            }
            else{
                //undefined story id
            }
        }
    };
    
    /* ------------  KODA ZA TAB edit Stories ------------------------------------ */
    
    $scope.allAvailableStories = [];
    $scope.getAvailableStories = function() {
        $scope.allAvailableStories = []; //pobriši ta stare ki ostanejo ko zamenjaš tab
        $scope.showStoryData = false;   //skrij data
        //$scope.acceptanceTests =  [];   //pobriši accepance teste 
        for (var i = 0; i < $scope.allStoriesOfProjectDB.length; i++) {
            var currentS = $scope.allStoriesOfProjectDB[i];
            if (currentS['inactive'] === false) {
                $scope.allAvailableStories.push(currentS);
            }
        }
    };

    $scope.storyEditData = {};
    $scope.acceptanceTests = []; //JSON ACCEPTANCE TESTS with text and everything
    $scope.tasksOnStory = [];
    $scope.showStoryData = false;
    $scope.showEditStoryData = function(storyID) {
        //reseteri seznam acceptance testov, da se ne dodajajo če menjamo zgodbe
        $scope.acceptanceTests = [];
        $scope.tasksOnStory = [];
        $scope.showStoryData = true;
        $scope.getAllProjects();        //za dropdown izbiro projekta
        $scope.storyEditData = $scope.getStory(storyID);//kopija zgodbe iz baze
        //alert(JSON.stringify($scope.storyEditData));
        var arrayOfatIDs = $scope.storyEditData['acceptenceTests'];
        var arrayoftaksIDs = $scope.storyEditData['tasks'];
        //$scope.storyEditData['acceptenceTests'] = arrayOfatIDs;
        for (var j = 0; j < arrayOfatIDs.length; j++) {
            var atID = arrayOfatIDs[j];
            $.ajax({
                type: "GET",
                url: '/api/userStories/' + parseInt(storyID) + '/tests/' + parseInt(atID),
                dataType: 'json',
                async: false,
                success: function(data) {
                    $scope.acceptanceTests.push(data);
                }
            });
        }
        
        for (var j = 0; j < arrayoftaksIDs.length; j++) {
            var atID = arrayoftaksIDs[j];
            $.ajax({
                type: "GET",
                url: '/api/tasks/' + parseInt(atID),
                dataType: 'json',
                async: false,
                success: function(data) {
                    $scope.tasksOnStory.push(data);
                }
            });
        }
    };

    $scope.addFieldTest = function(storyID) {
        //povečaj acceptanceTests[] za en JSON - zaenkrat prazen
        var emptyTestJSON = {'id': 0, 'test': '', 'accepted': 'false', 'userStory': parseInt(storyID)};
        $scope.acceptanceTests.push(emptyTestJSON);
    };
    
    $scope.addFieldTask = function(storyID) {
        var emptyTaskJSON = {'id': 0, 'description': '', 'estimatedTime': 0, 'workDone': 0, 'accepted': 'false', 'userStoryID': parseInt(storyID), 'assigneeID': 0};
        $scope.tasksOnStory.push(emptyTaskJSON);
    };

    $scope.removeAT = function(accTest) {
        for (var i = 0; i < $scope.acceptanceTests.length; i++) {
            if ($scope.acceptanceTests[i]['id'] === accTest['id']) {
                $scope.acceptanceTests.splice(i, 1);
            }
        }
    };
    
    $scope.removeTASK = function(task) {
        for (var i = 0; i < $scope.tasksOnStory.length; i++) {
            if ($scope.tasksOnStory[i]['id'] === task['id']) {
                $scope.tasksOnStory.splice(i, 1);
            }
        }
    };
    
    $scope.getStory = function(storyID) {
        var storyToReturn = {};
        $.ajax({
            type: "GET",
            url: '/api/userStories/' + parseInt(storyID),
            dataType: 'json',
            async: false,
            success: function(data) {
                storyToReturn = data;
            }
        });
        return storyToReturn;
    };

    $scope.storyDeletedSuccess = false;
    $scope.deleteStory = function(storyID) {
        $.ajax({
            type: "DELETE",
            url: '/api/userStories/' + storyID,
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            async: false,
            success: function(data) {
                $scope.showStoryData = false;
                $scope.storyDeletedSuccess = true;
                //remove it from the list so it doesnt appear in dropdown anymore
                for (var i = 0; i < $scope.allAvailableStories.length; i++) {
                    if (parseInt($scope.allAvailableStories[i]['id']) === parseInt(storyID)) {
                        $scope.allAvailableStories.splice(i, 1);
                    }
                }
            },
            error: function(status) {
                alert("Error deleting story: " + status);
            }
        });
    };
    
    $scope.removeWarningsEditStory = function(){
        $scope.storyAlreadyExists = false;
        $scope.busValErr = false;
        $scope.timeEsErr = false;
        $scope.contentErr = false;
        $scope.ATErr = false;
        $scope.ATEmpty = false;
        $scope.storyNameEmpty = false;
        $scope.taksEmpty = false;
        $scope.timeEsTASKErr = false;
        $scope.storyUpdatedSuccess = false;
        $scope.storyDeletedSuccess = false;
    };
    
    
    /*sprem za validacijo*/
    $scope.storyAlreadyExists = false;
    $scope.busValErr = false;
    $scope.timeEsErr = false;
    $scope.contentErr = false;
    $scope.ATErr = false;
    $scope.ATEmpty = false;
    $scope.storyNameEmpty = false;
    $scope.taksEmpty = false;
    $scope.timeEsTASKErr = false;
    $scope.storyUpdatedSuccess = false;
    $scope.storyToUpdateJSON = {};
    
    $scope.saveStoryChanges = function(storyID) {
        $scope.busValErr = false;
        $scope.timeEsErr = false;
        $scope.contentErr = false;
        $scope.ATErr = false;
        $scope.ATEmpty = false;
        $scope.storyNameEmpty = false;
        $scope.taksEmpty = false;
        $scope.timeEsTASKErr = false;
        $scope.storyUpdatedSuccess = false;
        //alert(angular.toJson($scope.acceptanceTests));

        //preveri za podvajanje imen
        $scope.storyAlreadyExists = false;
        for (var i = 0; i < $scope.allStoriesOfProjectDB.length; i++) {
            if ($scope.allStoriesOfProjectDB[i]['title'] === $scope.storyEditData['title'].toLowerCase()) {  //POPRAVEK 1
                $scope.storyAlreadyExists = true;
            }
        }
        
        //ali je prazno ime?
        if ($scope.storyEditData['title'].toString() === '') {
            $scope.storyNameEmpty = true;
        }
        //alert($scope.storyNameEmpty);
        //ali je bussines value stevilka
        if (typeof $scope.storyEditData['bussinessValue'] !== 'undefined') {
            if (validateEntry($scope.storyEditData['bussinessValue'].toString(), /^\d{0,2}(\.\d{0,2}){0,1}$/) &&
                    $scope.storyEditData['bussinessValue'].toString() !== '') {
                $scope.busValErr = false;
            }
            else {
                $scope.busValErr = true;
            }
        }
        //ali je time estimate stevilka
        if (typeof $scope.storyEditData['estimatedTime'] !== 'undefined') {
            if (validateEntry($scope.storyEditData['estimatedTime'].toString(), /^\d{0,2}(\.\d{0,2}){0,1}$/) &&
                    $scope.storyEditData['estimatedTime'].toString() !== '') {
                $scope.timeEsErr = false;
            }
            else {
                $scope.timeEsErr = true;
            }
        }
        //ali je content OK
        if (typeof $scope.storyEditData['story'] !== 'undefined' && $scope.storyEditData['story'] !== "") {
            $scope.contentErr = false;
        } 
        else {
            $scope.contentErr = true;
        }

        //ali je vsaj en acceptance test izpolnjen
        if ($scope.acceptanceTests.length > 0) {
            for (var i = 0; i < $scope.acceptanceTests.length; i++) {
                if ($scope.acceptanceTests[i]['test'] === '' ||
                        $scope.acceptanceTests[i]['test'] === 'undefined') {
                    $scope.ATEmpty = true;
                    $scope.ATErr = false;
                }
            }
        } 
        else {
            $scope.ATEmpty = false;
            $scope.ATErr = true;
        }

        //ali je kšn task neizpolnjen do konca
        if ($scope.tasksOnStory.length > 0) {
            for (var i = 0; i < $scope.tasksOnStory.length; i++) {
                if ($scope.tasksOnStory[i]['description'] === '' ||
                        $scope.tasksOnStory[i]['estimatedTime'] === 0 ||
                        $scope.tasksOnStory[i]['assigneeID'] === 0)
                    $scope.taksEmpty = true;
                //ali je time estimate stevilka (task)
                if (typeof $scope.tasksOnStory[i]['estimatedTime'] !== 'undefined') {
                    if (validateEntry($scope.tasksOnStory[i]['estimatedTime'].toString(), /^\d{0,2}(\.\d{0,2}){0,1}$/) &&
                            $scope.tasksOnStory[i]['estimatedTime'].toString() !== '') {
                        $scope.timeEsTASKErr = false;
                    }
                    else {
                        $scope.timeEsTASKErr = true;
                    }
                }
            }
        }

        //če vse OK updejti v bazo
        if (!$scope.storyAlreadyExists && !$scope.busValErr && !$scope.contentErr
                && !$scope.timeEsErr && !$scope.ATEmpty && !$scope.ATErr
                && !$scope.storyNameEmpty && !$scope.taksEmpty && !$scope.timeEsTASKErr) {
            //cast to int
            $scope.storyEditData['title'] = $scope.storyEditData['title'].toLowerCase();
            $scope.storyEditData['project'] = parseInt($scope.storyEditData['project']);
            $scope.storyEditData['sprint'] = parseInt($scope.storyEditData['sprint']);
            //alert("Before : " + angular.toJson($scope.storyEditData));
            //first update everything but acctests and tasks
            $.ajax({
                type: "PUT",
                url: '/api/userStories/' + parseInt(storyID),
                dataType: 'json',
                data: angular.toJson($scope.storyEditData),
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function(data) {
                    $scope.storyUpdatedSuccess = true;
                },
                error: function(xhr, textStatus, errorThrown) {
                    alert("error updating story: " + errorThrown);
                }
            });

            $scope.testToUpdate = {};
            //check if any new acc tests
            //v $scope.storyEditData['acceptenceTests'] so vsi stari
            //v $scope.acceptanceTests pa že novi, oziroma tud zbrisani
            for (var i = 0; i < $scope.acceptanceTests.length; i++) {
                var oldTest = false;
                for (var j = 0; j < $scope.storyEditData['acceptenceTests'].length; j++) {
                    if (parseInt($scope.storyEditData['acceptenceTests'][j]) === $scope.acceptanceTests[i]['id']) {
                        oldTest = true;
                    }
                }
                $scope.testToUpdate = $scope.acceptanceTests[i];
                if (!oldTest) {
                    //create new one in DB
                    $.ajax({
                        type: "POST",
                        url: '/api/userStories/' + parseInt(storyID) + '/tests',
                        dataType: 'json',
                        data: angular.toJson($scope.testToUpdate),
                        contentType: "application/json; charset=utf-8",
                        async: false,
                        success: function(data) {
                            //alert("new test added");
                        },
                        error: function(xhr, textStatus, errorThrown) {
                            alert("error adding test: " + errorThrown);
                        }
                    });
                } 
                else {
                    //update old one
                    $scope.IDOftesttoupdate = $scope.acceptanceTests[i]['id'];
                    $.ajax({
                        type: "PUT",
                        url: '/api/userStories/' + parseInt(storyID) + '/tests/' + $scope.IDOftesttoupdate,
                        dataType: 'json',
                        data: angular.toJson($scope.testToUpdate),
                        contentType: "application/json; charset=utf-8",
                        async: false,
                        success: function(data) {
                            //alert("test updated");
                        },
                        error: function(xhr, textStatus, errorThrown) {
                            alert("error updating test: " + errorThrown);
                        }
                    });
                }
            }
            //alert($scope.storyEditData['acceptenceTests'].length);
            //check if there is any to delete
            for (var i = 0; i < $scope.storyEditData['acceptenceTests'].length; i++) {
                var toDelete = true;

                for (var j = 0; j < $scope.acceptanceTests.length; j++) {
                    //alert($scope.storyEditData['acceptenceTests'][i].toString() + " " + $scope.acceptanceTests[j]['id'].toString());
                    if ($scope.acceptanceTests[j]['id'].toString() === $scope.storyEditData['acceptenceTests'][i].toString()) {
                        toDelete = false;
                    }
                }
                $scope.IDOftesttoupdate = $scope.storyEditData['acceptenceTests'][i];

                //alert(JSON.stringify($scope.storyEditData['acceptenceTests'][i]));
                if (toDelete) {
                    $.ajax({
                        type: "DELETE",
                        url: '/api/userStories/' + parseInt(storyID) + '/tests/' + $scope.IDOftesttoupdate,
                        async: false,
                        success: function(data) {
                            //alert("test deleted");
                        },
                        error: function(xhr, textStatus, errorThrown) {
                            alert("error updating test: " + errorThrown);
                        }
                    });
                }
            }
            /*
             //check if there is a new task
             for (var i = 0; i<$scope.tasksOnStory.length; i++){
             var old = false;
             for (var j = 0; j<$scope.storyEditData['tasks'].length; j++){
             if ($scope.storyEditData['tasks'][j].toString() === $scope.tasksOnStory[i]['id'].toString())
             old = true;
             }
             if (old){
             $scope.updateTask($scope.tasksOnStory[i],storyID);
             }else{
             $scope.putNewTask($scope.tasksOnStory[i],storyID);
             }
             }*/
            //check if there is task to delete and update others
            for (var i = 0; i < $scope.storyEditData['tasks'].length; i++) {
                var toDelete = true;
                for (var j = 0; j < $scope.tasksOnStory.length; j++) {
                    if ($scope.storyEditData['tasks'][j].toString() === $scope.tasksOnStory[i]['id'].toString()) {
                        toDelete = false;
                    }
                }
                if (toDelete) {
                    $scope.deleteTask($scope.tasksOnStory[i]);
                } else {
                    $scope.updateTask($scope.tasksOnStory[i]);
                }
            }

            $scope.storyEditData = $scope.getStory(storyID);//kopija zgodbe iz baze
            //alert("After : " + angular.toJson($scope.storyEditData));
        }
    };

    $scope.updateTask = function(task) {
        alert(JSON.stringify(task));
        $.ajax({
            type: "PUT",
            url: '/api/tasks/' + task['id'],
            dataType: 'json',
            data: angular.toJson(task),
            contentType: "application/json; charset=utf-8",
            async: false,
            success: function(data) {
                alert("task updated");
            },
            error: function(xhr, textStatus, errorThrown) {
                alert("error updating task: " + errorThrown);
            }
        });
    };
    
    $scope.putNewTask = function(task) {
        //cant put new task!!
    };
    
    $scope.deleteTask = function(task) {
        alert(JSON.stringify(task));
        $.ajax({
            type: "DELETE",
            url: '/api/tasks/' + task['id'],
            async: false,
            success: function(data) {
                alert("task deleted");
            },
            error: function(xhr, textStatus, errorThrown) {
                alert("error deleting task: " + errorThrown);
            }
        });
    };
    
    /* ------------------   KODA ZA MANAGE SPRINT TAB       ----------------------------- */
    $scope.sprintsForEdit = [];
    $scope.allSprintsOfProject = {};
    $scope.showSprintData = false;
    $scope.sprintEditData = {};
    $scope.getSprintsForEdit = function(){        
        //vsi sprinti tega projekta
        $http({ 
            method: 'GET', 
            url: '/api/project/'+$rootScope.currentProjectID+'/sprints'})
        .success(function(data, status, headers, config) {
            $scope.allSprintsOfProject = data;  
            $scope.filterSprints();     //prikazemo samo tiste sprinte, ki ustrezajo kriterijem
        }).error(function(data, status, headers, config) {
            //alert('Error getting all sprints of project!');
        });
    };
    
    $scope.filterSprints = function(){
        //Uporabnik lahko ureja in brise sprinte, ki se se niso priceli
        for(var i=0; i<$scope.allSprintsOfProject.length; i++){
            var currSprint = $scope.allSprintsOfProject[i];
            var beginDate = new Date(currSprint['startDate']);
            var today = new Date();
            
            //ali je ta sprint ze v sprintsForEdit
            var exists = $scope.sprintExistsInArray(currSprint);
            if(!exists){
                if(!(beginDate < today || beginDate === today)){
                    //sprint se se ni zacel
                    $scope.sprintsForEdit.push(currSprint);
                }
            }
            
        };
    };
    
    $scope.sprintExistsInArray = function(sprint){
        var len = $scope.sprintsForEdit.length;
        var exists = false;
        for(var j=0; j<len; j++){
            var s = $scope.sprintsForEdit[j];
            if(parseInt(s['id']) === parseInt(sprint['id'])){
                //ta sprint obstaja
                exists = true;
                break;
            }
        }
        return exists;
    };
    
    
    $scope.sprintToSaveEdit = {};
    $scope.showEditSprintData = function(sprintID){
        var id = parseInt(sprintID);
        
        //poiscemo ta sprint v sprintsForEdit
        $scope.thisSprint = {};
        for(var i=0; i<$scope.sprintsForEdit.length; i++){
            var currSprint = $scope.sprintsForEdit[i];
            if(parseInt(currSprint['id']) === id){
                $scope.thisSprint = currSprint;
                $scope.sprintToSaveEdit = currSprint;
            }
        }
        
        $scope.editStartDate = $scope.thisSprint['startDate'];
        $scope.editEndDate = $scope.thisSprint['endDate'];
        $scope.velocity = $scope.thisSprint['velocity'];
        
        //nastavimo datumom te vrednosti
        $('#beginDate').datepicker({
            format: "dd.mm.yyyy",
            todayBtn: "linked",
            language: "sl",
            autoclose: true,
            keyboardNavigation: true,
            todayHighlight: true
        });
        $('#beginDate').datepicker('update', new Date($scope.editStartDate));

        $('#endDate').datepicker({
            format: "dd.mm.yyyy",
            todayBtn: "linked",
            language: "sl",
            autoclose: true,
            keyboardNavigation: true,
            todayHighlight: true
        });
        $('#endDate').datepicker('update', new Date($scope.editEndDate));
        
        //velocity
        $scope.sprintEditData['sprintVelocity'] = $scope.velocity.toString();
        $scope.showSprintData = true;
    };
    
    $scope.sprintEditSuccess = false;
    $scope.errFromBackendEditSprint = '';
    $scope.datesOKEditSprint = true;
    $scope.endDateErrEditSprint = false;
    $scope.beginDateErrEditSprint = false;
    $scope.velEditErr = false;
    $scope.saveSprintChanges = function(){
        $scope.errFromBackendEditSprint = '';       
        //validacija podatkov v poljih
        var beginDate = document.getElementById('beginDate').value;
        var endDate = document.getElementById('endDate').value;
        var vel = $scope.sprintEditData['sprintVelocity'];
        
        $scope.validateEditSprintData(beginDate, endDate, vel);
        
        //če je vse ok
        if($scope.datesOKEditSprint && !$scope.velEditErr && !$scope.beginDateErrEditSprint && !$scope.endDateErrEditSprint){
            $scope.manageSprintsLoader = true;
            
            //treba si je pripravit datum v formatu javascripta nato pa prevorit v ISO za javo            
            var beginDateF = $scope.formatDateForDB(beginDate);
            var endDateF = $scope.formatDateForDB(endDate);
            
            $scope.errFromBackendEditSprint = '';            
            $scope.updateSprintJSON = { 
                "id": $scope.sprintToSaveEdit['id'],
                "startDate" : beginDateF,
                "endDate" : endDateF,
                "velocity" : parseFloat($scope.sprintEditData['sprintVelocity']),
                "projectId" : parseInt($rootScope.currentProjectID), 
                "stories": $scope.sprintToSaveEdit['stories']
            };
                        
            $http({
                method: 'PUT',
                url: '/api/sprint/' + $scope.sprintToSaveEdit['id'],
                data: $scope.updateSprintJSON,
                headers: {'Content-Type': 'application/json'}})
                    .success(function(data, status, headers, config) {
                        $scope.sprintEditSuccess = true;    
                        $scope.errFromBackendEditSprint = '';
                        $scope.manageSprintsLoader = false;
                        //update model of dropdown
                        $scope.updateDropdownModelAfterSprintEdit($scope.updateSprintJSON['id'], beginDate, endDate, $scope.updateSprintJSON['velocity']);
                        //pocistimo podatke
                        $scope.sprintEditData['sprintVelocity'] = '';
                        document.getElementById('beginDate').value = '';
                        document.getElementById('endDate').value = '';
                    }).error(function(data, status, headers, config) {
                        $scope.errFromBackendEditSprint = data.trim();
                        $scope.sprintEditSuccess = false;
                        $scope.manageSprintsLoader = false;
                });
        }
    };
    
    $scope.updateDropdownModelAfterSprintEdit = function(id, bd, ed, vel){
        
        var t = bd.toString().split('.');
        var year = parseInt(t[2]);
        var month = parseInt(t[1]);
        var day = parseInt(t[0]);
        if(month < 10){
            month = '0'+month;
        }
        if(day < 10){
            day = '0'+day;
        }
        var bd1 = year+'-'+month+'-'+day;
        
        var t = ed.toString().split('.');
        var year = parseInt(t[2]);
        var month = parseInt(t[1]);
        var day = parseInt(t[0]);
        if(month < 10){
            month = '0'+month;
        }
        if(day < 10){
            day = '0'+day;
        }
        var ed1 = year+'-'+month+'-'+day;
        
        var len = $scope.sprintsForEdit.length;
        for(var i=0; i<len; i++){
            var currSE = $scope.sprintsForEdit[i];
            if(parseInt(currSE['id']) === parseInt(id)){
                //najdl smo sprint
                currSE['beginDate'] = bd1;
                currSE['endDate'] = ed1;
                currSE['velocity'] = vel;
                break;
            }
        }
        
    };
    
    $scope.velEditErr = false;
    $scope.validateEditSprintData = function(beginDate, endDate, velocity){
        //ali je string velocity positive number --> format xx.yy...y --> decimalke nato zaokrozim na 1
        if(typeof velocity === 'undefined' || velocity ==='' || velocity === null){
            $scope.velEditErr = true;
        }
        else{
            if (velocity.indexOf(",") > -1 || velocity.indexOf("-") > -1 || !validateEntry(velocity, /^\d{0,2}(\.\d{0,2}){0,1}$/)){
                $scope.velEditErr = true;
            }
            else{
                //zaokrozimo in to prikazemo
                var temp = parseFloat(velocity);
                $scope.sprintEditData['sprintVelocity'] = temp.toFixed(1);
                $scope.velEditErr = false;                
            }
        }
        
        //ali sta datuma ok --> begin < end in sta oba vnešena
        if(beginDate === "" || beginDate === null){
            $scope.beginDateErrEditSprint = true;
        }
        else{
            $scope.beginDateErrEditSprint = false;
        }
        
        if(endDate === "" || endDate === null){
            $scope.endDateErrEditSprint = true;
        }
        else{
            $scope.endDateErrEditSprint = false;
        }
        
        if(!$scope.beginDateErrEditSprint && !$scope.endDateErrEditSprint){
            var dbArray = beginDate.split('.');
            var dbD = dbArray[0];
            var dbM = dbArray[1];
            var dbY = dbArray[2];
            var begDate = new Date(dbY, dbM-1, dbD);
            
            var deArray = endDate.split('.');
            var deD = deArray[0];
            var deM = deArray[1];
            var deY = deArray[2];
            var endDate = new Date(deY, deM-1, deD);
            
            if(begDate < endDate){
                $scope.datesOKEditSprint = true;
            }
            else{
                $scope.datesOKEditSprint = false;
            }
        }
    };
    
    $scope.removeWarningsNewSprint = function(){
        $scope.endDateErr = false;
        $scope.beginDateErr = false;
        $scope.decErr = false;
        $scope.datesOK = true;
        $scope.nsProjErr = false;
        $scope.errFromBackend = false;
        $scope.sprintSaveSuccess = false;
    };
    
    $scope.removeWarningsEditSprint = function(){
        //odstranmo opozorila od editiranja
        $scope.sprintEditSuccess = false;
        $scope.errFromBackendEditSprint = false;
        $scope.datesOKEditSprint = true;
        $scope.endDateErrEditSprint = false;
        $scope.beginDateErrEditSprint = false;
        $scope.velEditErr = false;
        
        //odstranimo vsa opozorila od deleteanja
        $scope.deleteSprintSuccess = false;
        $scope.deleteSprintErr = false;
    };
    
    //Brisanje sprinta
    $scope.deleteSprintLoader = false;
    $scope.deleteSprint = function(sprintID){
        //odstranmo opozorila od editiranja
        $scope.sprintEditSuccess = false;
        $scope.errFromBackendEditSprint = false;
        $scope.datesOKEditSprint = true;
        $scope.endDateErrEditSprint = false;
        $scope.beginDateErrEditSprint = false;
        $scope.velEditErr = false;
        
        $scope.deleteSprintLoader = true;
        
        $http({
            method: 'DELETE',
            url: '/api/sprint/' + sprintID,
            data: {},
            headers: {'Content-Type': 'application/json'}
            }).success(function(data, status, headers, config) {
                $scope.deleteSprintSuccess = true;
                $scope.deleteSprintErr = false;
                $scope.deleteSprintLoader = false;                
                //spraznemo polja
                document.getElementById('beginDate').value = '';
                document.getElementById('endDate').value = '';
                $scope.sprintEditData['sprintVelocity'] = '';
                //damo select na default
                $scope.sprintID = 0;
                //izbrisemo iz seznama sprintsForEdit
                for(var i=0; i<$scope.sprintsForEdit.length; i++){
                    var currS = $scope.sprintsForEdit[i];
                    if(parseInt(currS['id']) === parseInt(sprintID)){
                        //delete from array
                        $scope.sprintsForEdit.splice(i, 1);
                        break;
                    }
                }
            }).error(function(data, status, headers, config) {
                $scope.deleteSprintErr = true;
                $scope.deleteSprintSuccess = false;
                $scope.deleteSprintLoader = false;
            });
    };
    
    /* ------------  KODA ZA TAB edit project (zelo enaka koda kot za New Project ker mamo skor iste funkcionalnosti) -------------------- */
    $scope.removeWarningsEditProject = function(){
        $scope.projNameErr = false;
        $scope.userNumErrEdit = false;
        $scope.SMok = true;
        $scope.SMerr = '';
        $scope.POok = true;
        $scope.POerr = '';
        $scope.allHaveRoles = true;
        $scope.usersNotOnProject = [];
        $scope.usersOnProject = [];
        $scope.showNameErrMsg = false;
        $scope.showUpdateProjectErrMsg = false;
        $scope.updateProjectSuccess = false;
        $scope.updateProjectData['projectName'] = $rootScope.currentProjectName;           
    };
    
    $scope.updateProjectData = {};  //za nove podatke o tem projektu    
    /*
    * Metoda, ki se pokliče ko uporabnik klikne na shrani spremembe
    */
    $scope.updateProjectDataOK = false; //default vrednost validacije
    $scope.updateProjectEdit = function(){
        $scope.validateUpdateProjectData();  
        
        if($scope.updateProjectDataOK){
            //Vse ok - podatki za shranjevanje
            $scope.updateProjectJSON = { 
                "name" : $scope.updateProjectData['projectName'].toLowerCase(),
                "productOwner" : parseInt($scope.productOwner),
                "scrumMaster" : parseInt($scope.scrumMaster),
                "developers" : $scope.developers
            };
            
            var pnOK = true;
            if($scope.updateProjectJSON['name'] !== $rootScope.currentProjectName){
                pnOK = $scope.setProjectName($scope.updateProjectJSON['name']);     //update name ce je spremenjen
            }
                        
            if(pnOK){   //ce je ime OK spremenjeno - update se ostalo
                $scope.showNameErrMsg = false;
                var devOK = $scope.setDevelopers($scope.updateProjectJSON['developers']);    
                //alert('Setting developers: ' + JSON.stringify($scope.updateProjectJSON['developers']));
                var POsmOK = $scope.setSMandPO($scope.updateProjectJSON['scrumMaster'], $scope.updateProjectJSON['productOwner']);
            }
            else{
                $scope.showNameErrMsg = true;
            }
            
            
            if(devOK && pnOK && POsmOK){
                $scope.showUpdateProjectErrMsg = false;
                $scope.updateProjectSuccess = true;
                
                //vedno po spremembi projekta nardiš refresh strani
                alert('SUCCESS! The page needs to be reloaded for the changes to take effect!');
                location.reload();
                    
                /*var isChanged = $scope.isCurrentUserChanged();
                if(isChanged){
                    //log out and refresh
                    //$scope.logout();  // ali je dovolj reload?
                    alert('You have changed your role on this project. You need to reload the page for the changes to take effect!');
                    location.reload();                
                }*/
            }
            else{
                $scope.showUpdateProjectErrMsg = true;
                $scope.updateProjectSuccess = false;
            }
        }        
    };
    
    $scope.setDevelopers = function(developersTable){
        //REST - nastavimo nove developerje
        var len = developersTable.length;
        var userJSONlist = [];
        for(var i=0; i<len; i++){
            var currDevID = developersTable[i];
            var developerJSON = $scope.getUser(currDevID);
            userJSONlist.push(developerJSON);            
        }
        
        var success = true;
        $.ajax({
            type: "PUT",
            url: '/api/project/'+$rootScope.currentProjectID+'/developers',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            async: false,
            data: JSON.stringify(userJSONlist),
            success: function (data) { 
                success = true;
            },
            error: function(){
                success = false;
            }
        });
        return success;
    };
    
    $scope.setProjectName = function(name){
        var stringName = name.replace(/\s/g, "%20");
        //REST nastavimo nov project name
        var success = true;
        $.ajax({
            type: "PUT",
            url: '/api/project/'+$rootScope.currentProjectID+'/name/'+stringName,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            async: false,
            data: JSON.stringify([]),
            success: function (data) { 
                success = success && true;
                $scope.nameErrMsg = '';
            },
            error: function(data){
                success = success && false;
                $scope.nameErrMsg = data;
            }
        });
        return success;
    };
    
    $scope.setSMandPO = function(smID, poID){
        //REST nastavimo nova PO in SM
        var poJSON = $scope.getUser(poID);
        var smJSON = $scope.getUser(smID);        
        
        var success = true;
        //Nastavimo nov PO
        $.ajax({
            type: "PUT",
            url: '/api/project/'+$rootScope.currentProjectID+'/productOwner',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            async: false,
            data: JSON.stringify(poJSON),
            success: function (data) { 
                success = success && true;
                var rez = $scope.setSM(smJSON);
                success = success && rez;
            },
            error: function(){
                success = success && false;
            }
        });
        
       
        return success;
    };
    
    $scope.setSM = function(smJSON){
        var success = true;
        $.ajax({
            type: "PUT",
            url: '/api/project/'+$rootScope.currentProjectID+'/scrumMaster',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            async: false,
            data: JSON.stringify(smJSON),
            success: function (data) { 
                success = success && true;
            },
            error: function(){
                success = success && false;
            }
        });
        return success;
    };
    
    $scope.getUser = function(userID){
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/users/'+userID,
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };
    
    $scope.logout = function(){
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
    
    $scope.isCurrentUserChanged = function(){
        var user = LoggedUserService.getUserToken();
        var userID = user['id'];
        var notChanged = true;
        if($rootScope.isScrumMaster){
            //ce je trenutno SM in ni v novih podatkih SM --> changed
            if(parseInt(userID) === parseInt($scope.updateProjectJSON['scrumMaster'])){
                //ok
                notChanged = notChanged && true;
            }
            else{
                //changed
                notChanged = notChanged && false;
            }
        }
        if($rootScope.isProductOwner){
            //ce je trenutno product owner in ni v novih podatkih PO --> changed
            if(parseInt(userID) === parseInt($scope.updateProjectJSON['productOwner'])){
                //ok
                notChanged = notChanged && true;
            }
            else{
                //changed
                notChanged = notChanged && false;
            }

        }
        if($rootScope.isTeamMember){
            //ce je trenutno TM in v novih ni TM --> changed
            if($scope.updateProjectJSON['developers'].indexOf(parseInt(userID)) > -1){
                //ok
                notChanged = notChanged && true;
            }
            else{
                //changed
                notChanged = notChanged && false;
            }
        }
        return !notChanged;
    };
    
    $scope.validateUpdateProjectData = function(){      //validiramo update podatke o tem projektu 
                   
        //ali je izpolnjeno ime projekta
        if($scope.updateProjectData['projectName'] === null || $scope.updateProjectData['projectName'] === ''
                || typeof $scope.updateProjectData['projectName'] === 'undefined'){
            $scope.projNameErr = true;
        }
        else{
            $scope.projNameErr = false;
        }        
        
        //na projektu morata biti vsaj 1 uporabnik - ce ni uporabnikov ne shranimo
        if($scope.usersOnProject.length > 0){
            $scope.userNumErrEdit = false;
        }
        else{
            $scope.userNumErrEdit = true;
        }        
        
        //ce je na projektu vsaj en uporabnik in je ime ok - ali so vloge ok?
        if(!$scope.projNameErr && !$scope.userNumErrEdit){
            //id-ji uporabnikov ki so na teh vlogah
            $scope.developers = [];
            $scope.scrumMaster = -1;
            $scope.productOwner = -1;
            
            $scope.noOfProductOwners = 0; //counter za produktno vodjo
            $scope.noOfScrumMasters = 0; //counter za SM
            $scope.noOfNoRoles = 0;     //counter za tiste brez vlog
            
            var els = document.getElementsByClassName('tipUporabnikaEdit');     //vsi dropdowni za vloge            
            for(var i=0; i<els.length; i++){
                if(els[i].value === "0"){
                    //nic ni izbrano
                    $scope.noOfNoRoles++;
                }
                else if(els[i].value === "1"){
                    //TM
                    var u = $scope.usersOnProject[i];
                    $scope.developers.push(parseInt(u['id']));
                }
                else if(els[i].value === "2"){ 
                    //SM
                    var u = $scope.usersOnProject[i];
                    $scope.scrumMaster = parseInt(u['id']);
                    $scope.noOfScrumMasters++;
                }
                else if(els[i].value === "3"){ 
                    //PO
                    var u = $scope.usersOnProject[i];
                    $scope.productOwner = parseInt(u['id']);
                    $scope.noOfProductOwners++;
                }
                else if(els[i].value === "5"){ 
                    //SM+TM
                    var u = $scope.usersOnProject[i];
                    $scope.scrumMaster = parseInt(u['id']);
                    $scope.developers.push(parseInt(u['id']));  //damo ga se med developerje
                    $scope.noOfScrumMasters++;
                }
                else if(els[i].value === "4"){
                    //PO+TM
                    var u = $scope.usersOnProject[i];
                    $scope.productOwner = parseInt(u['id']);
                    $scope.developers.push(parseInt(u['id']));  //damo ga se med developerje
                    $scope.noOfProductOwners++;
                }
            }
            
            //ali imajo vsi vloge
            $scope.allHaveRoles = true;
            if($scope.noOfNoRoles > 0){
                //eni nimajo vlog
                $scope.allHaveRoles = false;
            }
            
            //ali ima projekt samo enega scrum masterja in samo enega product ownerja?
            $scope.SMok = true;
            $scope.SMerr = '';
            if($scope.noOfScrumMasters === 0){
                //ni scrum masterja
                $scope.SMok = false;
                $scope.SMerr = 'You have to select a Scrum Master!';
                $scope.updateProjectDataOK = false;
            }
            else if($scope.noOfScrumMasters > 1){
                //več scrum masterjev
                $scope.SMok = false;
                $scope.SMerr = 'Only one Scrum Master can be assigned to project!';
                $scope.updateProjectDataOK = false;
            }
            
            $scope.POok = true;
            $scope.POerr = '';
            if($scope.noOfProductOwners === 0){
                //ni product ownerja
                $scope.POok = false;
                $scope.POerr = 'You have to select a Product Owner!';
                $scope.updateProjectDataOK = false;
            }
            else if($scope.noOfProductOwners > 1){
                //več PO-jev
                $scope.POok = false;
                $scope.POerr = 'Only one Product Owner can be assigned to project!';
                $scope.updateProjectDataOK = false;
            }
            
            //vloge OK
            if($scope.POok && $scope.SMok && $scope.allHaveRoles){
                $scope.updateProjectDataOK = true;
            }
        }
        else{
            $scope.updateProjectDataOK = false;
        }        
    };
    
    
    /*--- Metode za pridobivanje in razvrščanje uporabnikov v sezname levo in desno glede na vloge na tem projektu ---*/
    $scope.allUsersFromDB = [];     //vsi uporabniki v bazi
    $scope.usersNotOnProject = [];      //uporabniki, ki niso na trenutnem projektu
    $scope.usersOnProject = [];     //uporabniki, ki so na trenutnem projektu    
    $scope.getAllUsers = function(){
        $http({ 
            method: 'GET', 
            url: '/api/users'})
        .success(function(data, status, headers, config) {
            $scope.allUsersFromDB = data;
            //vsakemu od teh uporabnikov pripisemo trenutno vlogo na tem projektu
            $scope.setFrontendRoles();
        }).error(function(data, status, headers, config) {
            //err
        });       
    };
    
    $scope.setFrontendRoles = function(){
        /* Vsakemu uporabniku v allUsersFromDB pripisemo vlogo na izbranem projektu - polje frontendProjectRole:
         * 1: TM
         * 2: SM
         * 3: PO
         * 4: TM + PO
         * 5: TM + SM
         * 0: Nič še
         * 
         * Ce nimajo vloge, jih damo v usersNotOnProject, sicer v usersOnProject
         */
        var len = $scope.allUsersFromDB.length;
        for(var i=0; i<len; i++){
            var currU = $scope.allUsersFromDB[i];
            var thisUserRole = $scope.getFrontendRoleInt(currU);
            currU['frontendProjectRole'] = thisUserRole;
            if(thisUserRole > 0){
                //uporabnik je na trenutnem projektu - damo ga v usersOnProject
                $scope.usersOnProject.push(currU);
            }
            else{
                //damo ga v usersNotOnProject
                $scope.usersNotOnProject.push(currU);
            }
        }      
    };
    
        
    $scope.getFrontendRoleInt = function(user){   
        
        //VLOGA TEGA USERJA NA TEM PROJEKTU: TM, PO, SM, SM+TM, PO+TM
        $scope.roles = $scope.getUserProjectRoles(user['id']);
        var len = $scope.roles.length;
        if(typeof $scope.roles === 'undefined' || len === 0){
            //nima vloge na tem projektu
            return 0;
        }
        else{
            var isPO = false;
            var isSM = false;
            var isTM = false;
            //ima vlogo - katero?
            for(var k=0; k<len; k++){                
                var currRole = $scope.roles[k];
                if(currRole['name'].indexOf('Team Member') > -1){
                    //is team member
                    isTM = true;
                }
                if(currRole['name'].indexOf('Scrum Master') > -1){
                    //is SM
                    isSM = true;
                }
                if(currRole['name'].indexOf('Product Owner') > -1){
                    //is team member
                    isPO = true;
                }
            }

            var roleInt = $scope.findRoleInt(isTM, isSM, isPO);
            return roleInt;
        }            
    };
    
    $scope.findRoleInt = function(isTM, isSM, isPO){
        var int = 0;
        if(isTM){
            if(isPO){
                // je TM + PO
                int = 4;
            }
            else if(isSM){
                //je TM + SM
                int = 5;
            }
            else{
                //je samo TM
                int = 1;
            }
        }
        else{
            if(isPO){
                // je PO
                int = 3;
            }
            else if(isSM){
                //je SM
                int = 2;
            }
        }
        return int;
    };
    
    $scope.getUserProjectRoles = function(userID){
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/users/'+userID+'/projects/'+$rootScope.currentProjectID+'/groups',
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };
        
    /*--- Metode za premikanje iz leve na desno in obratno ---*/
    $scope.chooseUser = function(user){
        //dodamo uporabnika na seznam na desni, ce ga se ni
        var index = $scope.usersOnProject.indexOf(parseInt(user['id']));
        if (index < 0) {
            //ga se ni - dodamo na desno
            $scope.usersOnProject.push(user);
            //Odstranimo iz levega seznama
            var ind = $scope.usersNotOnProject.indexOf(user);
            $scope.usersNotOnProject.splice(ind, 1);
        }
    };
    
    $scope.removeUser = function(user){
        //odstranimo iz desnega
        var index = $scope.usersOnProject.indexOf(user);
        $scope.usersOnProject.splice(index, 1);
        //vrnemo na levi seznam
        $scope.usersNotOnProject.push(user);
    };
    
    /* Funkcija, ki pobrise celoten seznam izbranih uporabnikov na desni */
    $scope.clearUsersList = function() {
        //dodas jih vse na levo
        for (var i = 0; i < $scope.usersOnProject.length; i++) {
            var currUser = $scope.usersOnProject[i];
            $scope.usersNotOnProject.push(currUser);
        }
        $scope.usersOnProject = [];    //spraznes seznam na levi
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

Date.prototype.addDays = function(days) {
    var dat = new Date(this.valueOf());
    dat.setDate(dat.getDate() + days);
    return dat;
};