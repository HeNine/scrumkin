//kontroler za obnašanje about strani
scrumkin.controller('PBacklog', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService, 
    AcceptanceTestsService) {
        
    $scope.activeTab = 'unfinishedStories';
    
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
    
    // REST - vse zgodbe na izbranem projektu: $rootScope.currentProjectID
    $scope.allProjectStories = {};
    $scope.finishedStories = [];    //koncane zgodbe
    $scope.unfinishedAssignedStories = [];  //nedokoncane, dodeljene sprintu
    $scope.unfinishedNotAssignedStories = [];   //nedokoncane, niso dodeljene sprintu  
    $scope.errTE = '';
    $scope.okTE = '';
    $scope.productBacklogLoader = false;
    
    if(typeof $rootScope.currentProjectID !== 'undefined' && $rootScope.currentProjectID !== ''
            && $rootScope.currentProjectID !== null){
        $scope.productBacklogLoader = true;
        $http({
            method: 'GET', 
            url: '/api/project/'+parseInt($rootScope.currentProjectID)+'/stories'})
        .success(function(data, status, headers, config) {
            $scope.allProjectStories = data;
            //alert('All: ' + JSON.stringify(data));
            $scope.sortStories();
            $scope.productBacklogLoader = false;
        }).error(function(data, status, headers, config) {
            //alert("Error getting all stories of project: " + status);
            $scope.productBacklogLoader = false;
        });
    }
    else{
        //project ID undefined
    }    
    
    $scope.removeWarningsUnfinishedStories = function(){
        $scope.errTE = '';
        $scope.okTE = '';
    };
    
    $scope.sortStories = function(){
        //filtriramo zgodbe najprej na finished/unfinished in pol na assigned/not assgined
        var dataLen = Object.keys($scope.allProjectStories).length;
        
        for(var i=0; i<dataLen; i++){
            $scope.currS = $scope.allProjectStories[i];
            
            //UREDIMO PODATKE
            //priority
            if($scope.currS['priority'] === 3){
                $scope.currS['priorityText'] = 'Must have';
            }
            else if($scope.currS['priority'] === 7){
               $scope.currS['priorityText'] = 'Should have';
            }
            else if($scope.currS['priority'] === 6){
                $scope.currS['priorityText'] = 'Could have';
            }
            else {
                $scope.currS['priorityText'] = "Won't have this time";
            }
            
            //$scope.currS['comments'] = StoryService.getStoryComment($scope.currS); 
            
            //acceptance tests
            $scope.accTests = $scope.currS['acceptenceTests'];  //tabela z id-ji testov tega story-ja [12,13]
            $scope.currS['acceptenceTestsText'] = [];
            for(var j=0; j<$scope.accTests.length; j++){
                $scope.currTestID = $scope.accTests[j]; 
                $scope.rez = AcceptanceTestsService.getAccTest($scope.currS['id'], $scope.currTestID);
                $scope.currS['acceptenceTestsText'].push($scope.rez['test']);
            }            
            
            $scope.currS['finished'] = AcceptanceTestsService.isStoryFinished($scope.currS);
            if($scope.currS['finished']){
                //finished
                $scope.finishedStories.push($scope.currS);
            }
            else{
                //not finished - assigned/not assigned
                if($scope.currS['sprint'] === 0){
                    $scope.unfinishedNotAssignedStories.push($scope.currS);
                }
                else{
                    $scope.unfinishedAssignedStories.push($scope.currS);
                }
            }
            
        }
    };
    
    $scope.changeTimeEstimate = function(newTimeEstimate, story){
        //iz all project stories potegnemo originalno zgodbo
        var id = story['id'];
        var editedStory = {};
        var dataLen = Object.keys($scope.allProjectStories).length;
        for(var i=0; i<dataLen; i++){
            var cs = $scope.allProjectStories[i];
            if(cs['id'] === id){
                editedStory = cs;
                break;
            }
        }
        
        if(typeof newTimeEstimate === 'undefined' || newTimeEstimate === '' || newTimeEstimate === null){
            //ali je timeEstimate definiran
            $scope.errTE = 'You have to enter a new Time Estimate!';
            $scope.okTE = '';
        }
        else{
            //ali je time estimate pozitivna cela ali decimalna stevilka
            if (newTimeEstimate.indexOf(",") > -1 || newTimeEstimate.indexOf("-") > -1
                    || !validateEntry(newTimeEstimate, /^\d{0,3}(\.\d{0,4}){0,1}$/)){
                $scope.errTE = 'New time estimate must be an integer or a double, separated by "." !';
                $scope.okTE = '';
            }
            else{   
                var temp = parseFloat(newTimeEstimate);
                var temp1 = temp.toFixed(1);        //zaokrozimo in to shranimo                
                story['estimatedTime'] = parseFloat(temp1);     //shranimo objekt story v bazo
                                                    
                var storyToUpdate = {
                    /*"id": story['id'],
                    "title": story['title'],
                    "story": story['story'],
                    "bussinessValue": story['bussinessValue'],*/
                    "estimatedTime": story['estimatedTime']/*,
                    "acceptenceTests": story['acceptenceTests'],
                    "tasks": story['tasks'],
                    "priority": story['priority'],
                    "project": story['project'],
                    "sprint": story['sprint']*/};
                
                //REST - update izbranega story-ja z novo time estimate vrednostjo
                if(typeof story['id'] !== 'undefined' && story['id'] !== '' && story['id'] !== null){
                    $http({
                    method: 'PUT',
                    url: '/api/userStories/'+story['id'],
                    data: storyToUpdate,
                    headers: {'Content-Type': 'application/json'}})
                        .success(function(data, status, headers, config) {
                            $scope.errTE = '';
                            $scope.okTE = 'Time estimate succesfuly updated!';
                            var els = document.getElementsByClassName('newEstimate');
                            for(var k=0; k<els.length; k++){
                                document.getElementsByClassName('newEstimate')[k].value = '';
                            }
                        }).error(function(data, status, headers, config) {
                            $scope.errTE = 'Error updating time estimate: ' + status;
                            $scope.okTE = '';
                    });
                }
                else{
                    //undefined story id
                }                
            }
        }
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


