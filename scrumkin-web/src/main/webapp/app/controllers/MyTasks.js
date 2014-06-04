//kontroler za obnašanje about strani
scrumkin.controller('MyTasks', function($scope, $http, $rootScope, $location, LoggedUserService, StoryService, UserService) {

    //preverimo, ce je uporabnik logiran    
    $rootScope.userLogged = false;
    $scope.user = LoggedUserService.getUserToken();
    $rootScope.usertypeDesc = 'User';
    
    if (typeof $scope.user === 'undefined') {
        $rootScope.userLogged = false;
        $location.path("/Login");
    }
    else {
        $rootScope.userLogged = true;
        $rootScope.userUname = $scope.user['username'];
        $rootScope.userLoggedID = $scope.user['id'];

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

    //ALI je izbran projekt za delo? - ce ne, redirect
    if (typeof $rootScope.currentProjectID === 'undefined' || $rootScope.currentProjectID === ''
            || $rootScope.currentProjectID === null) {
        $location.path("/ChooseProject");
    }
    else {
        //ce je izbran, preverimo, ce ima projekt aktiven sprint --> ce ga nima, potem je sprint backlog prazen - opozorimo
        if ($rootScope.activeSprintID !== -1 && typeof $rootScope.activeSprintID !== 'undefined') {
            $scope.activeSprintSelected = true;

            //preverimo, ce je product owner --> ce je, nima moznosti sprejemanja nalog
            if ($rootScope.currentProjectRole === 'Product Owner') {
                $scope.isPO = true;
            }
            else {
                $scope.isPO = false;
            }
        }
        else {
            $scope.activeSprintSelected = false;
        }
    }

    /* -------------------------------------------------------------------------- */

    //pogledamo, katere taske ima uporabnik assignane
    $scope.assignedTasks = [];
    $scope.taskStoriesIDs = [];
    $scope.taskStories = [];

    $scope.getAssignedTasks = function() {
        //katere taske ima assignane - le na story-jih iz tega projekta!
        if ($rootScope.userLoggedID !== null && typeof $rootScope.userLoggedID !== 'undefined'
                && $rootScope.userLoggedID !== '') {
            $http({
                method: 'GET',
                url: '/api/users/' + parseInt($rootScope.userLoggedID) + '/tasks'})
                    .success(function(data, status, headers, config) {
                        $scope.assignedTasks = data;
                        $scope.getTaskStories();
                        $scope.matchTasks2Stories();
                    }).error(function(data, status, headers, config) {
                //alert('Error getting user tasks!');
            });
        }
    };

    $scope.matchTasks2Stories = function() {
        for (var i = 0; i < $scope.taskStories.length; i++) {
            var currS = $scope.taskStories[i];
            var currST = currS['tasks'];    //id-ji taskov na tej zgodbi - pogledamo, kateri so assignani na tega userja
            if(typeof currS['id'] !== 'undefined'){
                currS['comments'] = StoryService.getStoryComment(currS);
                if(Object.keys(currS['comments']).length > 0){
                    currS['showComments'] = true;
                }
                else{
                    currS['showComments'] = false;
                }
            }             
            var assigned = [];
            for (var k = 0; k < $scope.assignedTasks.length; k++) {
                var currT = $scope.assignedTasks[k];
                if (currST.indexOf(currT['id']) > -1) {
                    $scope.isCompleted = $scope.isTaskCompleted(currT);     //ali je completed --> prikazi completed  
                    var isAccepted = $scope.isAccepted(currT);      //ali je accepted 
                    if (isAccepted) {
                        //assigned in accepted --> prikazi release
                        $scope.showRelease = true;
                        $scope.showAccept = false;
                        currT['showEditHistory'] = true;
                    }
                    else {
                        //assigned ampak ni accepted --> prikazi accept
                        $scope.showRelease = true;
                        $scope.showAccept = true;
                        currT['showEditHistory'] = false;
                    }
                    currT['showRelease'] = $scope.showRelease;
                    currT['showAccept'] = $scope.showAccept;
                    currT['isCompleted'] = $scope.isCompleted;
                    assigned.push(currT);
                }
            }
            currS['assignedTasks'] = assigned;
        }
    };

    $scope.isTaskCompleted = function(task) {        
        if (parseFloat(task['estimatedTime']) === 0) {
            return true;
        }
        else {
            return false;
        }
    };

    $scope.isAccepted = function(task) {
        if (typeof task['accepted'] !== 'undefined' && task['accepted'] !== null && task['accepted'] === true) {
            return true;
        }
        else {
            return false;
        }
    };

    $scope.getTaskStories = function() {
        //seznam id-jev zgodb, na katerih so taski ki jih ima uporabnik assignane (tega projekta in tega sprinta)
        var dataLen = $scope.assignedTasks.length;
        for (var i = 0; i < dataLen; i++) {
            var currT = $scope.assignedTasks[i];
            if ($scope.taskStoriesIDs.indexOf(currT['userStoryID']) === -1) { //id-taska
                $scope.taskStoriesIDs.push(currT['userStoryID']);
                //poberemo to zgodbo iz baze
                var story = StoryService.getUserStory(currT['userStoryID']);
                if (parseInt(story['project']) === parseInt($rootScope.currentProjectID)
                        && parseInt(story['sprint']) === parseInt($rootScope.activeSprintID)) {
                    $scope.taskStories.push(story);
                }
            }
        }
    };

    $scope.isTaskAssigned = function(task) {
        if (typeof task['assigneeID'] !== 'undefined' && task['assigneeID'] !== null && task['assigneeID'] !== ''
                && parseInt(task['assigneeID']) > 0) {
            var user = UserService.getUser(task['assigneeID']);
            return user;
        }
        else {
            return false;
        }
    };


    /*----------------------koda za ACCEPT TASK -------------------------------------*/
    $scope.acceptTask = function(task) {
        $scope.taskToAccept = task;
        $scope.taskToAccept['assigneeID'] = $rootScope.userLoggedID;
        //update task
        $http({
            method: 'PUT',
            url: '/api/tasks/' + $scope.taskToAccept['id'],
            data: {
                "accepted": true,
                "assigneeID": parseInt($rootScope.userLoggedID)
            },
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config) {
            $scope.taskAcceptSuccess = true;
            $scope.errAcceptingTask = '';
            task['showAccept'] = false; //ne prikazi, ker je sprejeta
            task['showRelease'] = true;
            task['showEditHistory'] = true;
            //se enkrat prever ce je completed
            task['isCompleted'] = $scope.isTaskCompleted(task);
        }).error(function(data, status, headers, config) {
            $scope.taskAcceptSuccess = false;
            $scope.errAcceptingTask = 'Error accepting task: ' + status;
        });
    };


    /*----------------------koda za RELEASE TASK -------------------------------------*/
    $scope.releaseTask = function(task) {
        $scope.taskToRelease = task;
        $http({
            method: 'PUT',
            url: '/api/tasks/' + $scope.taskToRelease['id'],
            data: {
                "accepted": '',
                "assigneeID": ''
            },
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config) {
            $scope.taskReleaseSuccess = true;
            $scope.errReleasingTask = '';
            //uporabnik se ji je odpovedou --> odstrant jo mormo iz seznama
            task['hideReleasedTask'] = true;
        }).error(function(data, status, headers, config) {
            $scope.taskReleaseSuccess = false;
            $scope.errReleasingTask = 'Error releasing task: ' + status;
            task['hideReleasedTask'] = false;
        });
    };
    
    /*---------------------- koda za start work ---------------------------------------- */
    
    $scope.startWork = function(task){
        if(typeof $rootScope.globalWorkingTask === 'undefined' || $rootScope.globalWorkingTask === null){
            //uporabnik trenutno ne dela na nobeni zgodbi - zapomnimo si kdaj je zacel delo            
            //čas začetka dela
            //$scope.workingTask = task;  //naloga, na kateri dela
            $rootScope.workStartTime = new Date();        
            $rootScope.showCurrentWorkingTask = true;   //prikazemo napis da dela na enem tasku
            $rootScope.globalWorkingTask = task;
        }
        else{
            //uporabnik ze dela na eni zgodbi 
            
            if(parseInt(task['id']) === parseInt($rootScope.globalWorkingTask['id'])){
                //dela na istem tasku
                alert('You are already working on this task!');
            }
            else{
                //dela na drugem tasku
                alert('You are currently working on another task. You cannot work on two tasks at the same time!');
            }
        }
    };
    
    $scope.stopWork = function(workingTask){
        $scope.workEndTime = new Date();   //čas konca dela
        var workingTimeMS = $scope.workEndTime - $rootScope.workStartTime;
        //za pretvorbe iz MS v druge enote
        var hours = 3600000;
        var minutes = 60000;
        var seconds = 1000;
        var workingTime = workingTimeMS/hours;   //kolk ur je delou
        
        if(workingTime > 12){
            //delal je več kot 12 h na enem tasku - se ne upoštev
            alert('You have worked more than 12 hours on this task! ');
        }
        else{
            //čas, ki ga je uporabnik oddelal, prištejemo času, ki je že v logu za ta dan:
            $http({
                method: 'GET', 
                url: '/api/tasks/' + parseInt(workingTask['id']) + '/log'})
            .success(function(data, status, headers, config) {
                //poiscemo ta dan in sestejemo ter nato shranimo
                $scope.addWorkDone(data, workingTime, workingTask, $scope.workEndTime);
            }).error(function(data, status, headers, config) {
                alert('Error getting work log of this task - automatic work logging');
            });
            
        }
        
        //počistmo podatke da si ne zapomne
        $rootScope.showCurrentWorkingTask = false;
        $rootScope.globalWorkingTask = null;
    };
    
    
    $scope.addWorkDone = function(log, workTime, task, endTime){
        var logEntry = {};
        //poiscemo ta log entry
        for(var i=0; i<log.length; i++){
            var logLine = log[i];
            var logDate = new Date(logLine['date']);
            var endDate = new Date(endTime);
            if(logDate.getDate() === endDate.getDate() && logDate.getMonth() === endDate.getMonth() && logDate.getYear() === endDate.getYear()){
                logEntry = log[i];
            }
        }
        
        var newWorkDone = parseFloat(logEntry['workDone']);
        if(Object.keys(logEntry).length > 0){
            //imamo log - povecamo workDone na ta datum
            newWorkDone = parseFloat(logEntry['workDone']) + workTime;
        }
        else{
            //nimamo še loga
            newWorkDone = workTime;
        }
        
        //shranimo novo vrednost za ta datum
        //JSON date
        $scope.logDate = new Date(endTime);    
        var month = parseInt($scope.logDate.getMonth())+1;
        if(month < 10){
            var month = '0'+month;
        }
        $scope.jsonLogDate = $scope.logDate.getFullYear()+"-"+month+"-"+$scope.logDate.getDate();

        $scope.newDataLog = {
            "user": parseInt($rootScope.userLoggedID),
            "task": parseInt(task['id']),
            "workDone": newWorkDone.toFixed(2),
            "workRemaining": parseFloat(task['estimatedTime']).toFixed(2),
            "date": $scope.jsonLogDate
        };
        
        $.ajax({
            type: "PUT",
            url: '/api/tasks/' + task['id'] + '/log/'+$scope.jsonLogDate,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            async: false,
            data: JSON.stringify($scope.newDataLog),
            success: function (data) { 
                
                task['workDone'] = data['workDone'].toFixed(2);
                //poiscemo ta task v taskStories --> story.assignedTasks --> ce prekljapljamo med tabi je treba tko, ker pozab na kjerem tasku dela
                var found = false;
                for(var i=0; i<$scope.taskStories.length; i++){
                    var currS = $scope.taskStories[i];
                    var tasks = currS['assignedTasks'];
                    var tasksLen = Object.keys(tasks).length;
                    for(var j=0; j<tasksLen; j++){
                        var currTask = tasks[j];
                        if(parseInt(currTask['id']) === parseInt(data['id'])){
                            //nasli smo ta task
                            currTask['workDone'] = data['workDone'].toFixed(2);
                            found = true;
                            break;
                        }
                    }
                    if(found){
                        break;
                    }
                }
                
                //prikazemo mu opozorilo - ali želi spremeniti tudi remaining work?
                alert('You have worked on this task for ' +data['workDone'].toFixed(2) +' h. If you wish to change the remaining time of work for this task, click on the "Edit work history" link of this task.');
            },
            error: function(data, status){
                alert('Error changing task log! - automatic work logging');
            }
        }); 
    };

    /*------------- Koda za popup - beleženje porabe časa (zgodovina)  ------------------------- */
    
    /*
     * Vsakič se doda nov vnos in se sešteva skupno št. oddelanih ur na tasku 
     * V primeru da št. ur preseže oceno se označi task kot zaključen (razlika med workDone in workRemaining)
     * Taks lahko zaključimo tudi ročno (klic metode markCompletedTask) - tuki se pa časovna ocena nastavi na 0 = nepovratna operacija 
     */
    
    $scope.sprintWorkLog = [];
    $scope.taskHistoryArray = [];
    $scope.showSaveButton = false;
    $scope.taskHistoryLoader = false;
    $scope.editWorkHistory = function(task) {
        // postavmo vsa opozorila na nevtralno ko uporabnik odpre popup okno - da ne ostane še kej od prejšnjega odpiranja okna
        $scope.workDoneErr = false;
        $scope.workRemainingErr = false;
        $scope.somethingWentWrong = false;
        $scope.changesSaved = false;
        $scope.workDoneEmpty = false;
        $scope.workRemainingEmpty = false;       
        $scope.showSaveButton = false;
        $scope.taskHistoryLoader = false;
        $scope.historyOK = true;
        
        $scope.editingTask = task;        
        /*
         * Vsaka vrstica v history logu je sestavljena iz JSON:
         * {"date"
         * "doneDB"
         * "remainingDB"
         * "changed"}        //polje, ki nam pove ali je uporabnik spremenil to vrstico - same te update-amo/shranjujemo
         */
        
        //generiramo seznam vseh dni od zacetka sprinta do danes --> $rootScope.activeSprintStart  //TODO: od sprejetja taska do danes
        $scope.startDate = new Date($rootScope.activeSprintStart);
        $scope.todayDate = new Date();
        $scope.taskHistoryArray = getDatesHistory($scope.startDate, $scope.todayDate).reverse();   //od najbolj recent datuma do danes
        
        //Pridobimo work log za ta task in vnesemo vrednosti za te taske iz baze v ustrezne vrstice history-ja
        $scope.getAndFillHistoryArray(task);
    };
    
    $scope.getAndFillHistoryArray = function(task){
        //dobimo history LOG od tega taska        
        $http({
            method: 'GET', 
            url: '/api/tasks/' + parseInt(task['id']) + '/log'})
        .success(function(data, status, headers, config) {
            $scope.taskLog = data;
            $scope.fillHistoryArray(data, task);        //napolnimo history array s podatki v LOGU
            $scope.showSaveButton = true;
        }).error(function(data, status, headers, config) {
            alert('Error getting work log of this task');
        }); 
    };
    
    $scope.fillHistoryArray = function(taskLog, task){
        //TODO - tko, da je vedno stanje tistega dne - če vmes uporabnik ni vnesu nč, potem od zadnjega vnosa do takrt vpišeš
        
        //history iz work loga
        var dataLen = Object.keys(taskLog).length;
        var historyArrayLen = Object.keys($scope.taskHistoryArray).length;
        for(var i=0; i<dataLen; i++){
            var line = taskLog[i];
            var date = new Date(line['date']);
            var done = line['workDone'];
            var remaining = line['workRemaining'];
            for(var j=0; j<historyArrayLen; j++){
                var historyLine = $scope.taskHistoryArray[j];
                var hDate = new Date(historyLine['date']);
                if(hDate.getTime() === date.getTime()){
                    //zapisemo podatke v pripadajoco vrstico zgodovine
                    historyLine['doneDB'] = done;
                    historyLine['remainingDB'] = remaining;
                }
            }
        }
    };
    
    $scope.historyOK = true;
    $scope.atLeastOneChanged = false;
    $scope.validateHistory = function(){
        $scope.historyOK = true;
        /*
         * vsi vnosi morajo biti float/double/integer
         * noben vnos ni obvezen - ce je prazno polje, se smatra kot 0
         */
        $scope.atLeastOneChanged = false;
        var dataLen = Object.keys($scope.taskHistoryArray).length;
        for(var i=0; i<dataLen; i++){
            var line = $scope.taskHistoryArray[i];            
            if(line['changed'] === true){       //preverimo samo vrstice, ki so bile spremenjene
                $scope.atLeastOneChanged = $scope.atLeastOneChanged || true;
                var currDone = line['doneDB'];
                var currRemaining = line['remainingDB'];
                if(typeof currDone === 'undefined' || currDone === '' || currDone === null){
                    //prazno polje
                    /*line['doneLineErr'] = true;
                    $scope.historyOK = $scope.historyOK && false; */
                    line['doneLineErr'] = false;
                    $scope.historyOK = $scope.historyOK && true; 
                    line['doneDB'] = 0;
                }
                else{
                    //nekaj vneseno - ce je stevilo je ok, sicer ni. More bit stevilo s piko - ker ce je z vejco, pol parsa samo del pred vejco 
                    if(currDone.toString().indexOf(",") > -1 || currDone.toString().indexOf("-") > -1
                        || !validateEntry(currDone.toString(), /^\d{0,3}(\.\d{0,4}){0,1}$/)){
                        //ni stevilo, ni ok
                        line['doneLineErr'] = true;
                        $scope.historyOK = $scope.historyOK && false; 
                    }
                    else{
                        //je stevilo, je ok
                        $scope.historyOK = $scope.historyOK && true; 
                        line['doneLineErr'] = false;
                        line['doneDB'] = parseFloat(currDone.toString());
                    }
                }

                if(typeof currRemaining === 'undefined' || currRemaining === '' || currRemaining === null){
                    //prazno polje
                    /*line['remainingLineErr'] = true;
                    $scope.historyOK = $scope.historyOK && false; */
                    line['remainingLineErr'] = false;
                    $scope.historyOK = $scope.historyOK && true; 
                    line['remainingDB'] = 0;
                }
                else{
                    //nekaj vneseno - ce je stevilo je ok, sicer ni
                    if(currRemaining.toString().indexOf(",") > -1 || currRemaining.toString().indexOf("-") > -1
                        || !validateEntry(currRemaining.toString(), /^\d{0,3}(\.\d{0,4}){0,1}$/)){
                        //ni stevilo, ni ok
                        line['remainingLineErr'] = true;
                        $scope.historyOK = $scope.historyOK && false; 
                    }
                    else{
                        //je stevilo, je ok
                        line['remainingLineErr'] = false;
                        $scope.historyOK = $scope.historyOK && true; 
                        line['remainingDB'] = parseFloat(currRemaining.toString());
                    }
                }
            }            
        }
    };
    
    $scope.changedLine = function(line){
       line['changed'] = true;
    };
    
    $scope.changesSaved = false;
    $scope.somethingWentWrong = false;    
    $scope.updateWorkHistoryModal = function(task) {        //se poklice ko klikne shrani na editWorkHistory
        $scope.allOK = true;  
        $scope.validateHistory();       //validiramo 
        
        if($scope.historyOK && $scope.atLeastOneChanged){
            $scope.taskHistoryLoader = true;
            /*
             * Zdej mamo tabelo taskHistoryArray z doneDB in remainingDB, ki so ustrezni, torej >=0
            
             * shranimo vrstice, ki imajo polje changed = true, v bazo:
             * - uporabimo PUT za update - ce vpisa v bazi se ni, nardi novga
             */
            $scope.lastReturned = task;            
            $scope.dataLen = Object.keys($scope.taskHistoryArray).length;
            for(var i=0; i<$scope.dataLen; i++){
                $scope.currLine = $scope.taskHistoryArray[i];
                if($scope.currLine['changed'] === true){
                    
                    //JSON date
                    $scope.logDate = $scope.currLine['date'];  
                    var month = parseInt($scope.logDate.getMonth())+1;
                    if(month < 10){
                        var month = '0'+month;
                    }
                    var day = $scope.logDate.getDate();
                    if(day < 10){
                        day = '0'+day;
                    }
                    
                    $scope.jsonLogDate = $scope.logDate.getFullYear()+"-"+month+"-"+day;
                    
                    $scope.newDataLog = {
                        "user": parseInt($rootScope.userLoggedID),
                        "task": parseInt(task['id']),
                        "workDone": parseFloat($scope.currLine['doneDB']),
                        "workRemaining": parseFloat($scope.currLine['remainingDB']),
                        "date": $scope.jsonLogDate
                    };
                    
                    $.ajax({
                        type: "PUT",
                        url: '/api/tasks/' + task['id'] + '/log/'+$scope.jsonLogDate,
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        async: false,
                        data: JSON.stringify($scope.newDataLog),
                        success: function (data) { 
                            $scope.lastReturned = data;
                            $scope.allOK = $scope.allOK && true;
                        },
                        error: function(data, status){
                            //alert('Error changing task log!');
                            $scope.allOK = $scope.allOK && false;
                        }
                    });                 
                }
            }
            
            //novo stanje tega taska
            task['estimatedTime'] = $scope.lastReturned['estimatedTime'];
            task['workDone'] = $scope.lastReturned['workDone'];
            $scope.com = $scope.isTaskCompleted(task);
            task['isCompleted'] = $scope.com;
            
            //opozorila o napakah
            $scope.changesSaved = $scope.allOK;
            $scope.somethingWentWrong = !$scope.allOK;
            
            $scope.taskHistoryLoader = false;
        };    
        
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

function getDatesHistory(startDate, stopDate) {
    var dateArray = new Array();
    var currentDate = startDate;
    
    while (currentDate <= stopDate) {
        var json = {"date":currentDate, "doneDB":0, "remainingDB":0, "changed":false};
        dateArray.push(json);
        currentDate = currentDate.addDays(1);
    }
    
    return dateArray;
}

