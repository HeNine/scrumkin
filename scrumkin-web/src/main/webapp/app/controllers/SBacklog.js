//kontroler za obnašanje about strani
scrumkin.controller('SBacklog', function($scope, $route, $http, $rootScope, $location, LoggedUserService,
        AcceptanceTestsService, StoryService, UserService) {
            
    $scope.activeTab = 'userStories';
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
        $rootScope.userLoggedID = $scope.user['id'];
        
        
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
    else{
        //ce je izbran, preverimo, ce ima projekt aktiven sprint --> ce ga nima, potem je sprint backlog prazen - opozorimo
        if($rootScope.activeSprintID !== -1 && typeof $rootScope.activeSprintID !== 'undefined'){
            $scope.activeSprintSelected = true;
            
            //preverimo, ce je product owner --> ce je, nima moznosti sprejemanja nalog
            if($rootScope.currentProjectRole==='Product Owner'){
                $scope.isPO = true;
            }
            else{
                $scope.isPO = false;
            }
        }
        else{
            $scope.activeSprintSelected = false;
        }
    }
    
    /* ----------------------------- koda za OBOJE ------------------------------------------------ */
    /*pridobivanje zgodb aktivnega sprinta:  
     * - id active sprinta maš v tej spremenljivki: $rootScope.activeSprintID. 
     * Če je ta spremelnjivka === -1, potem al nimaš izbranga projekta, al pa ta projekt nima nobenga aktivnega
     * sprinta, in je treba narest nov sprint (aktiven je, če je startDate <= today <= endDate).
     * 
     * Več je v kodi v chooseProject.js.
     */
    
    $scope.allStoriesOfActiveSprintSB = [];
    $scope.getAllStoriesOfActiveSprint = function(){
        if(typeof $rootScope.activeSprintID !== 'undefined' && $rootScope.activeSprintID !== null
                && $rootScope.activeSprintID !== '' && $rootScope.activeSprintID > 0){
            $scope.sprintBacklogLoader = true;
            $http({ 
                method: 'GET', 
                url: '/api/sprint/'+$rootScope.activeSprintID+'/stories'
            }).success(function(data, status, headers, config) {
                $scope.allStoriesOfActiveSprintSB = data;
                $scope.workStories();
                $scope.sprintBacklogLoader = false;
            }).error(function(data, status, headers, config) {
                $scope.sprintBacklogLoader = false;
                //alert('Error getting all stories of active sprint!');
            });
        }
        else{
            //undefined active sprint
        } 
    };
    
    $scope.disableSaveTask = false;
    $scope.allStoriesOfActiveSprint = [];
    $scope.getAllStoriesOfActiveSprintTask = function(){
        if(typeof $rootScope.activeSprintID !== 'undefined' && $rootScope.activeSprintID !== null
                && $rootScope.activeSprintID !== '' && $rootScope.activeSprintID > 0){
            $http({ 
                method: 'GET', 
                url: '/api/sprint/'+$rootScope.activeSprintID+'/stories'
            }).success(function(data, status, headers, config) {
                //$scope.allStoriesOfActiveSprint = data;
               var dataLen = Object.keys(data).length;
                //ce ni zgodb na aktivnem sprintu
                if(dataLen === 0){
                    $scope.disableSaveTask = true;
                }
                else{
                    $scope.disableSaveTask = false;
                    //izlocimo zgodbe, ki so ze finished                    
                    for(var i=0; i<dataLen; i++){
                        var currS = data[i];
                        if(AcceptanceTestsService.isStoryFinished(currS)===false){
                            $scope.allStoriesOfActiveSprint.push(currS);
                        }
                    };
                }
            }).error(function(data, status, headers, config) {
                //alert('Error getting all stories of active sprint!');
            });
        }
        else{
            //undefined active sprint
        } 
    };
    
    /*---------------------- koda za USER STORIES tab -------------------------------------*/ 
    
    $scope.progressOnSprint = 0;
    $scope.remainingOnSprint = 0;
    $scope.alreadyRejected = false;
    $scope.alreadyAccepted = false;
    
    $scope.workStories = function() {
        
        var dataLen = $scope.allStoriesOfActiveSprintSB.length;
        for(var i=0; i<dataLen; i++){
            $scope.currSB = $scope.allStoriesOfActiveSprintSB[i];
            
            //priority
            if($scope.currSB['priority'] === 3){
                $scope.currSB['priorityText'] = 'Must have';
            }
            else if($scope.currSB['priority'] === 7){
               $scope.currSB['priorityText'] = 'Should have';
            }
            else if($scope.currSB['priority'] === 6){
                $scope.currSB['priorityText'] = 'Could have';
            }
            else {
                $scope.currSB['priorityText'] = "Won't have this time";
            }
            
            //komentarji zgodbe
            if(typeof $scope.currSB['id'] !== 'undefined'){
                $scope.currSB['comments'] = StoryService.getStoryComment($scope.currSB);     
            }
            
            //acceptance tests
            $scope.accTests = $scope.currSB['acceptenceTests'];  //tabela z id-ji testov tega story-ja [12,13]
            $scope.currSB['acceptenceTestsText'] = [];
            for(var j=0; j<$scope.accTests.length; j++){
                $scope.currTestID = $scope.accTests[j]; 
                $scope.rez = AcceptanceTestsService.getAccTest($scope.currSB['id'], $scope.currTestID);
                $scope.currSB['acceptenceTestsText'].push($scope.rez['test']);
            }  
            
            //tasks
            $scope.tasks = $scope.currSB['tasks'];
            $scope.currSB['tasksOnStory'] = [];
            $scope.totalAll = 0;    //total remaining work
            $scope.totalDone = 0;
            $scope.allCompleted = true;
            for (var j=0; j<$scope.tasks.length; j++){
                $scope.currTaskID = $scope.tasks[j];
                $scope.currTask = StoryService.getStoryTask($scope.currTaskID);
                $scope.totalAll = $scope.totalAll + parseFloat($scope.currTask['estimatedTime']);   //ZAKAJ VRZE ERROR???
                $scope.totalDone = $scope.totalDone + parseFloat($scope.currTask['workDone']);
                
                //ali je assigned
                var assignedUser = $scope.isTaskAssigned($scope.currTask);                
                if(typeof assignedUser === 'undefined' || assignedUser === false || assignedUser === null){
                    $scope.currTask['isAssigned'] = false;
                    $scope.currTask['assignee'] = 'Not selected';
                    $scope.currTask['isCompleted'] = false;
                    $scope.allCompleted = $scope.allCompleted && $scope.currTask['isCompleted'];
                }
                else{
                    $scope.currTask['isAssigned'] = true;
                    $scope.currTask['assignee'] = assignedUser['name'];
                    
                    //ali je tudi accepted
                    $scope.currTask['isAccepted'] = $scope.isAccepted($scope.currTask);
                    
                    //ali je completed --> se prikaze samo, ce je task tudi assigned
                    var isCompleted = $scope.isTaskCompleted($scope.currTask);
                    $scope.currTask['isCompleted'] = isCompleted;
                    $scope.allCompleted = $scope.allCompleted && $scope.currTask['isCompleted'];
                }                
                $scope.currSB['tasksOnStory'].push($scope.currTask);
            }
            
            /* POTRJEVANJE/ZAVRACANJE ZGODB
            * Za vsako posamezno zgodbo se gumb prikaze le, ce je:
            * - user = PO
            * - zgodba se ni bila potrjena/zavrnjena
            * - vsi taski so Completed
            * - zgodba je v tekocem sprintu --> itak, ce je v sprint backlogu
            */
            $scope.currSB['allCompleted'] = $scope.allCompleted;
            
            //ali je bila potrjena/zavrnjena ze prej
            $scope.notAcceptedRejected = $scope.isStoryAcceptedRejected($scope.currSB);
            $scope.currSB['alreadyRejected'] = $scope.alreadyRejected;
            $scope.currSB['alreadyAccepted'] = $scope.alreadyAccepted;
            
            $scope.showAccept = false;
            $scope.showReject = false;
            
            //ali prikaze accepted - le ce je PO, ce je vse zakljuceno in se ni bila potrjena/zavrnjena
            if($rootScope.isProductOwner && $scope.allCompleted && $scope.notAcceptedRejected){
                $scope.showAccept = true;
            }
            $scope.currSB['showAccept'] = $scope.showAccept;
            
            //ali prikaze rejected - ce je PO in ce ni bila potrjena/zavrnjena ze
            if($rootScope.isProductOwner && $scope.notAcceptedRejected){
                $scope.showReject = true;
            }
            $scope.currSB['showReject'] = $scope.showReject;
            
            var temp = $scope.totalAll - $scope.totalDone;                    
            $scope.currSB['totalRemainingWork'] = temp.toFixed(1);
            //povečaj števca za progress bar
            $scope.progressOnSprint += parseFloat($scope.totalDone);
            $scope.remainingOnSprint += parseFloat($scope.currSB['totalRemainingWork']);
        } 
        $scope.progressBar = parseFloat($scope.progressOnSprint*(parseFloat(100/($scope.progressOnSprint+$scope.remainingOnSprint))));
    };
    
    $scope.isStoryAcceptedRejected = function(story){
        $scope.alreadyAccepted = AcceptanceTestsService.isStoryAccepted(story);
        if(!$scope.alreadyAccepted){
            $scope.alreadyRejected = AcceptanceTestsService.isStoryRejected(story);
        }
        else{
            $scope.alreadyRejected = false;
        }
        
        return !($scope.alreadyRejected || $scope.alreadyAccepted);
    };
    
    $scope.acceptStory = function(story){
        var rez = AcceptanceTestsService.setTestsAccepted(true, story);
        if(rez === true){
            //accepted
            alert('Story successfully accepted!');            
            //prikaz gumbov
            story['alreadyRejected'] = false;
            story['alreadyAccepted'] = true;  
            story['showReject'] = false;
            story['showAccept'] = false;
            story['allCompleted'] = true;
        }
        else{
            //error
            alert('Error accepting story!');
        }
    };
    
    $scope.rejectStoryTemp = function(story){
        $scope.storyToReject = story;
    };
    
    $scope.rejectStory = function(story, comment){
        //komentar je obvezen
        if(typeof comment === 'undefined' || comment === '' || comment === null){
            $scope.rejectCommentErr = true;
        }
        else{
            $scope.rejectCommentErr = false;
            var rez = AcceptanceTestsService.setTestsAccepted(false, story);    //vse acc teste da na false - zavrne zgodbo
            if(rez === true){
                //rejected --> shranimo komentar 
                if(typeof comment !== 'undefined' && comment !== '' && comment !== null){
                    $scope.commentJSON = {
                        "comment": comment, 
                        "role": 1
                    };                
                    $http({ 
                        method: 'POST', 
                        url: '/api/userStories/'+story['id']+"/comments",
                        data: $scope.commentJSON
                    }).success(function(data, status, headers, config) {
                        alert('Story successfully rejected!');
                        //prikaz gumbov
                        story['alreadyRejected'] = true;
                        story['alreadyAccepted'] = false;  
                        story['showAccept'] = false;
                        story['showReject'] = false;
                        story['allCompleted'] = true;
                        if(typeof story['comments'] === 'undefined'){
                            var comm = [];
                            comm.push($scope.commentJSON);
                            story['comments'] = comm;
                        }
                        else{
                            story['comments'].push($scope.commentJSON);
                        }
                    }).error(function(data, status, headers, config) {
                        alert('Error saving comment!');
                    });
                }
                else{
                    alert('Story successfully rejected!');
                }            
            }
            else{
                //error
                alert('Error rejecting story!');
            }
        }
        
        
    };
    
    $scope.isTaskCompleted = function(task){
        if(parseFloat(task['estimatedTime']) === 0){
            return true;
        }
        else{
            return false;
        }
    };
    
    $scope.isTaskAssigned = function(task){     //ali je Scrum Master ga dolocu ob izdelavi taska?
        if(typeof task['assigneeID'] !== 'undefined' && task['assigneeID'] !== null && task['assigneeID'] !== ''
                && parseInt(task['assigneeID']) > 0 ){
            var user = UserService.getUser(task['assigneeID']);
            return user;
        }
        else{
            return false;
        }
    };
    
    $scope.isAccepted = function(task){
        if(typeof task['accepted'] !== 'undefined' && task['accepted'] !== null && task['accepted'] === true){
            return true;
        }
        else{
            return false;
        }
    };
    
    
    /*----------------------koda za ACCEPT TASK -------------------------------------*/
    
    $scope.acceptTask = function (task){
        $scope.taskToAccept = task;
        $scope.taskToAccept['assigneeID'] = $rootScope.userLoggedID;
        //update task
        $http({
            method: 'PUT',
            url: '/api/tasks/'+$scope.taskToAccept['id'],
            data: {
                "accepted":true,
                "assigneeID":parseInt($rootScope.userLoggedID)
            },
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config) {
            $scope.taskSaveSuccess = true;
            $scope.errAcceptingTask = '';
            task['isAssigned'] = true;  //je dolocena uporabniku
            task['isAccepted'] = true;  //je sprejeta
            //se enkrat prever ce je completed
            task['isCompleted'] = $scope.isTaskCompleted(task);
            //member
            var user = UserService.getUser($rootScope.userLoggedID);
            task['assignee'] = user['name'];
        }).error(function(data, status, headers, config) {
            $scope.taskSaveSuccess = false;  
            $scope.errAcceptingTask = 'Error accepting task: ' + status;
        });
    };
    
    //dodajanje opomb k zgodbam
    $scope.addStoryCommentTemp = function(story){
        $scope.storyToComment = story;
    };
    
    $scope.addComment = function(story, comment){
        //komentar je obvezen
        if(typeof comment === 'undefined' || comment === '' || comment === null){
            $scope.storyCommentErr = true;
        }
        else{
            $scope.storyCommentErr = false;
            
            //shranimo komentar 
            $scope.commentJSON = {
                "comment": comment, 
                "role": 0
            };                
            $http({ 
                method: 'POST', 
                url: '/api/userStories/'+story['id']+"/comments",
                data: $scope.commentJSON
            }).success(function(data, status, headers, config) {
                //comment saved
                $scope.storyCommentErr1 = false;
                $scope.commentSaved = true;
                //update html 
                if(typeof story['comments'] === 'undefined'){
                    var comm = [];
                    comm.push($scope.commentJSON);
                    story['comments'] = comm;
                }
                else{
                    story['comments'].push($scope.commentJSON);
                }
                        
            }).error(function(data, status, headers, config) {
                //error saving comment
                $scope.storyCommentErr1 = true;
                $scope.commentSaved = false;
            });    
        }
    };
    
    
    /*----------------------koda za NEW TASK TAB-------------------------------------*/
    
    //pridobimo uporabnike v tej skupini
    $scope.developerTeamMembers = [];
    $scope.developerTeamMembersSB = [];
    $scope.getTeamMembers = function(){
        if(typeof $rootScope.currentProjectID !== 'undefined'){
            $http({ 
                method: 'GET', 
                url: '/api/project/'+$rootScope.currentProjectID+'/developers'})
            .success(function(data, status, headers, config) {
                $scope.developerTeamMembers = data;
                $scope.developerTeamMembersSB = data;
            }).error(function(data, status, headers, config) {
                //alert('Error getting devs');
            });
        }
    };
    
    
    $scope.newTaskData = {};    
    $scope.confirmSummary = function (){
        $scope.newTaskLoader = true;
        //validacija polj
        $scope.taskErr = false;
        $scope.decErr = false;
        $scope.storyErr = false;
        $scope.showConfirmed = false;
        
        $scope.desc = $scope.newTaskData['desTask'];
        $scope.time = $scope.newTaskData['time'];
        $scope.story = $scope.newTaskData['selectedStory'];
        $scope.teamMember = $scope.newTaskData['selectedUser'];
        
        //ali imamo description napisan
        if ($scope.desc === '' || typeof $scope.desc === 'undefined' || $scope.desc === null){
            $scope.taskErr = true;
        }
        else{
            $scope.taskErr = false;
        }
        
        //ali je time positive number --> format xx.yy...y --> decimalke nato zaokrozim na 1
        if(typeof $scope.time === 'undefined' || $scope.time === null || $scope.time === ''){
            $scope.decErr = true;
        }
        else{
            if (typeof $scope.time === 'undefined' || $scope.time.indexOf(",") > -1
                    || $scope.time.indexOf("-") > -1
                    || !validateEntry($scope.time, /^\d{0,2}(\.\d{0,4}){0,1}$/)){
                $scope.decErr = true;
            }
            else{
                //ne sme bit 0
                var temp = parseFloat($scope.time);
                if(temp === 0){
                    $scope.decErr = true;
                }
                else{
                    //zaokrozimo in to prikazemo                
                    $scope.newTaskData['time'] = temp.toFixed(1);
                    $scope.decErr = false; 
                }                               
            }
        }
        
        //Ali je zgodba aktivnega izbrana
        if ($scope.story === '' || typeof $scope.story === 'undefined' || $scope.story === null){
            $scope.storyErr = true;
        }
        else{
            $scope.storyErr = false;
        }
        
        //ali je vse OK - prikazemo summary
        if( !$scope.storyErr && !$scope.taskErr && !$scope.decErr){
            //$scope.showConfirmed = true;
            $scope.saveNewTask();
        }
        else{
            $scope.showConfirmed = false;
            $scope.taskSaved=false;
            $scope.newTaskLoader = false;
        }
    };
    
    $scope.saveNewTask = function(){
          
        if(typeof $scope.newTaskData['selectedUser'] === 'undefined' ||
                $scope.newTaskData['selectedUser'] === null ||
                $scope.newTaskData['selectedUser'] === ''){
            $scope.userID = '';
        }
        else{
            $scope.userID = parseInt($scope.newTaskData['selectedUser']);
        }
        
        /*
         *  public int id;
            public String description;
            public Double estimatedTime;
            public Double workDone;
            public Boolean accepted;
            public int userStoryID;
            public int assigneeID;
         */
        
        $scope.taskID = null;
        $scope.newTask = {
            "description": $scope.newTaskData['desTask'],
            "estimatedTime": parseFloat($scope.newTaskData['time']),
            "workDone": 0,
            "accepted": false,
            "userStoryID": parseInt($scope.newTaskData['selectedStory']),
            "assigneeID": $scope.userID
        };
        
        $http({ 
            method: 'POST', 
            url: '/api/tasks/sprint/'+$rootScope.activeSprintID,
            data: $scope.newTask
        }).success(function(data, status, headers, config) {
            //alert('Task saved!');
            $scope.taskSaved = true;
            $scope.taskSavedErr='';
            //clear form
            $scope.newTaskData = {};
            $scope.newTaskLoader = false;
        }).error(function(data, status, headers, config) {
            //alert('Error saving task');
            $scope.taskSaved=false;
            $scope.taskSavedErr='Error saving task: ' + status;
            $scope.newTaskLoader = false;
        });
    };
    
    /*----------------------koda za EDIT TASK -------------------------------------*/
    $scope.task2Edit = {};
    $scope.newTaskInfo = {};
    $scope.editTaskLoader = false;
    $scope.editTaskTemp = function(task){
        $scope.task2Edit = task;
        $scope.taskUpdateSuccess = false;
        $scope.taskUpdateErr = false;
        $scope.noInput = false;
        $scope.etErr = false;
        $scope.editTaskLoader = false;
        $scope.newTaskInfo['description'] = '';
        $scope.newTaskInfo['estimatedTime'] = '';
        $scope.newTaskInfo['assignedUser'] = '';
    };
    
    
    $scope.editTask = function(task){
        $scope.editTaskLoader = true;
        //Validiramo
        $scope.newDesc = $scope.newTaskInfo['description'];
        $scope.newET = $scope.newTaskInfo['estimatedTime'];
        $scope.newAssigned = $scope.newTaskInfo['assignedUser'];
        
        //ce je vse troje prazno - opozorilo
        if((typeof $scope.newTaskInfo['description'] === 'undefined' || $scope.newTaskInfo['description'] === '' ||
                $scope.newTaskInfo['description'] === null) && (typeof $scope.newTaskInfo['estimatedTime'] === 'undefined' ||
                $scope.newTaskInfo['estimatedTime'] === '' || $scope.newTaskInfo['estimatedTime'] === null)
                && (typeof $scope.newTaskInfo['assignedUser'] === 'undefined' ||
                $scope.newTaskInfo['assignedUser'] === '' || $scope.newTaskInfo['assignedUser'] === null)){
            $scope.noInput = true;
        }
        else{
            $scope.noInput = false;
            //ce je description prazen, mu damo vrednost k ga ma task
            if(typeof $scope.newTaskInfo['description'] === 'undefined' || $scope.newTaskInfo['description'] === '' ||
                $scope.newTaskInfo['description'] === null){
                $scope.newDesc = task['description'];
            }
            
            $scope.editTaskNewModelAssigned = false;
            //ce je assigned prazen
            if(typeof $scope.newTaskInfo['assignedUser'] === 'undefined' || $scope.newTaskInfo['assignedUser'] === '' ||
                $scope.newTaskInfo['assignedUser'] === null){
                //ohranimo prejšnje stanje
                $scope.newAssigned = task['assigneeID'];
                $scope.editTaskNewModelAssigned = task['isAssigned'];
                $scope.editTaskNewModelAccepted = task['isAccepted'];
            }
            else{
                //ce ni prazen - nekoga bomo shranili
                $scope.editTaskNewModelAssigned = true;
                $scope.editTaskNewModelAccepted = false;
            }
            
            //ce je estimated time prazen
            if(typeof $scope.newTaskInfo['estimatedTime'] === 'undefined' ||
                $scope.newTaskInfo['estimatedTime'] === '' || $scope.newTaskInfo['estimatedTime'] === null){
                $scope.newET = task['estimatedTime'];
            }
            else{
                //ce ni prazen, prevermo ce je ustreazna vrednost
                if (typeof $scope.newTaskInfo['estimatedTime'] === 'undefined' || $scope.newTaskInfo['estimatedTime'].indexOf(",") > -1
                        || $scope.newTaskInfo['estimatedTime'].indexOf("-") > -1
                        || !validateEntry($scope.newTaskInfo['estimatedTime'], /^\d{0,2}(\.\d{0,4}){0,1}$/)){
                    $scope.etErr = true;
                }
                else{
                    //ne sme bit 0
                    var temp = parseFloat($scope.newTaskInfo['estimatedTime']);
                    if(temp === 0){
                        $scope.etErr = true;
                    }
                    else{
                        //zaokrozimo in to prikazemo                
                        $scope.newET = $scope.newTaskInfo['estimatedTime'];
                        $scope.etErr = false; 
                    }                               
                }
            }
        }
        
        if(!$scope.etErr && !$scope.noInput){
            /*Ce vse ok - update task: PUT api/tasks/taskId. JSON:
            * int id
            * string description
            * double estimatedTime
            * double workDone
            * Boolean accepted
            * int userStoryID
            * int assigneeID
            */
           
            $scope.editTaskJSON = {
                "id": parseInt(task['id']), 
                "description": $scope.newDesc, 
                "estimatedTime": parseFloat($scope.newET).toFixed(1), 
                "workDone": task['workDone'],
                "accepted": task['accepted'],
                "userStoryID": task['userStoryID'], 
                "assigneeID": parseInt($scope.newAssigned)  //novi assigned user
            };
            
            $http({
                method: 'PUT',
                url: '/api/tasks/'+task['id'],
                data: $scope.editTaskJSON,
                headers: {'Content-Type': 'application/json'}
            }).success(function(data, status, headers, config) {
                //update frontend model
                task['description'] = $scope.newDesc;
                task['estimatedTime'] = $scope.editTaskJSON['estimatedTime'];
                    
                if($scope.editTaskNewModelAssigned){
                    //updateana zgodba je assigned - ali je tudi accepted    
                    task['isAssigned'] = true;
                    var assignedUser = $scope.isTaskAssigned($scope.editTaskJSON); 
                    task['assignee'] = assignedUser['name'];
                    if($scope.editTaskNewModelAccepted){
                        //je accepted
                        task['isAccepted'] = true;
                    }
                    else{
                        task['isAccepted'] = false;
                    }                    
                }
                else{
                    task['assignee'] = 'Not selected';
                    task['isAssigned'] = false; 
                }
            
                //skinemo opozorila
                $scope.taskUpdateSuccess = true;
                $scope.taskUpdateErr = false;  
                $scope.editTaskLoader = false;
                //izpraznemo model
                $scope.newTaskInfo['description'] = '';
                $scope.newTaskInfo['estimatedTime'] = '';
                $scope.newTaskInfo['assignedUser'] = '';
            }).error(function(data, status, headers, config) {
                $scope.taskUpdateSuccess = false;  
                $scope.taskUpdateErr = true;
                $scope.editTaskLoader = false;
            });            
        }            
        else{
            $scope.taskUpdateSuccess = false;  
        }
    };
    
    /*----------------------koda za DELETE TASK -------------------------------------*/
    $scope.deleteTaskLoader = false;
    $scope.task2Delete = {};
    $scope.deleteTaskTemp = function(task){
        $scope.taskDeleteSuccess = false;
        $scope.taskDeleteErr = false;
        $scope.deleteTaskLoader = false;
        $scope.task2Delete = task;
        
        /*
         * Task lahko izbrišemo, če še ni sprejet oz če ni assigned
         */
        
        if(typeof task['isAccepted'] === 'undefined' || task['isAccepted'] === false){
            //izbrisi
            $scope.allowDelete = true;
        }
        else{
            $scope.allowDelete = false;
        }
        
    };
    
    $scope.deleteTask = function(task, storyOfTask){
        $scope.deleteTaskLoader = true;
        //Rest na: DELETE api/tasks/taskId
        $http({
            method: 'DELETE',
            url: '/api/tasks/'+task['id'], 
            data: {},
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config) {
            //opozorila
            $scope.taskDeleteSuccess = true;
            $scope.taskDeleteErr = false;
            $scope.allowDelete = false;
            $scope.deleteTaskLoader = false;
            
            //odstranimo task iz sprint backloga - iz $scope.allStories...
            var storyOfTaskID = task['userStoryID'];
            //find story v arrayu
            for(var i=0; i<$scope.allStoriesOfActiveSprintSB.length; i++){
                var currStory = $scope.allStoriesOfActiveSprintSB[i];
                if(parseInt(currStory['id']) === parseInt(storyOfTaskID)){                
                    //to je ta story  
                    var storyTasks = currStory['tasks'].toString().split(',');  //taski tega storyja
                    for(var j=0; j<storyTasks.length; j++){
                        storyTasks[j] = parseInt(storyTasks[j]);
                        var currTaskID = storyTasks[j];
                        //var currTaskOnStory = currStory['tasksOnStory'][j];
                        if(parseInt(task['id']) === parseInt(currTaskID)){
                            //najdl smo task - ga odstranmo iz story Tasks in story TasksOnStory
                            storyTasks.splice(j, 1);
                            currStory['tasksOnStory'].splice(j, 1);
                            break;
                        }
                    }
                    //Pobrisal smo task iz tabele tasks - spremenimo to se v arrayu
                    currStory['tasks'] = storyTasks;
                    break;
                }
            }
        }).error(function(data, status, headers, config) {
            $scope.taskDeleteSuccess = false;
            $scope.taskDeleteErr = true;
            $scope.deleteTaskLoader = false;
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
