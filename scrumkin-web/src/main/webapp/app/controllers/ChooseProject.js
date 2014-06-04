//kontroler za obnašanje about strani
scrumkin.controller('ChooseProject', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService) {
    
    //preverimo, ce je uporabnik logiran    
    $rootScope.userLogged = false;
    $scope.user = LoggedUserService.getUserToken();
    $rootScope.usertypeDesc = 'User';
    
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
    }
    
    $rootScope.isProductOwner = false;
    $rootScope.isScrumMaster = false;
    $rootScope.isTeamMember = false;
    
    //prikazemo vse projekte na katerih je ta uporabnik
    $scope.userID = $scope.user['id'];
    $scope.usersProjects = {};
    $scope.getUsersProjectsErr = '';
    $scope.getAllUsersProjects = function(){
        if(typeof $scope.userID !== 'undefined' && 
                $scope.userID !== null && $scope.userID !== 'NaN' && $scope.userID !== ''){
            $http({ 
                method: 'GET', 
                url: '/api/users/'+JSON.parse($scope.userID)+'/projects'})
            .success(function(data, status, headers, config) {
                $scope.usersProjects = data;
            }).error(function(data, status, headers, config) {
                $scope.getUsersProjectsErr = "Error retrieving your projects: " + status;
            });
        }
        else{
            $scope.getUsersProjectsErr = "User ID not defined.";
        }        
    };
    
    $scope.getUserProjectRole = function(projID){
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/users/'+$scope.userID+'/projects/'+projID+'/groups',
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };
    
    
    $scope.confirmProjectChoice = function(){
        $rootScope.currentProjectID = $scope.currProject;
        $rootScope.currentProjectName = '';
        //$rootScope.currentProjectRole = 'Team member';
        var dataLen = Object.keys($scope.usersProjects).length;
        for(var i=0; i<dataLen; i++){
            var currP = $scope.usersProjects[i];
            
            if(parseInt(currP['id']) === parseInt($rootScope.currentProjectID)){
                //to je projekt ki ga je uporabnik izbral                
                $rootScope.currentProjectName = currP['name'];  //ime projekta
                
                //VLOGA NA PROJEKTU: TM, PO, SM, SM+TM, PO+TM
                $scope.roles = $scope.getUserProjectRole($rootScope.currentProjectID);
                var len = $scope.roles.length;
                for(var k=0; k<len; k++){
                    var currRole = $scope.roles[k];
                    if(currRole['name'].indexOf('Team Member') > -1){
                        //is team member
                        $rootScope.isTeamMember = true;
                    }
                    if(currRole['name'].indexOf('Scrum Master') > -1){
                        //is SM
                        $rootScope.isScrumMaster = true;
                    }
                    if(currRole['name'].indexOf('Product Owner') > -1){
                        //is team member
                        $rootScope.isProductOwner = true;
                    }
                }
                
                //pridobimo aktiven sprint tega projekta
                $scope.projectSprints = {};
                if(typeof $rootScope.currentProjectID !== 'undefined'){
                    $http({ 
                        method: 'GET', 
                        url: '/api/project/'+$rootScope.currentProjectID+'/sprints'})
                    .success(function(data, status, headers, config) {
                        $scope.projectSprints = data;
                        $rootScope.activeSprint = $scope.getActiveSprint($scope.projectSprints);
                        //alert(JSON.stringify($rootScope.activeSprint));
                        if($rootScope.activeSprint === null){
                            $rootScope.activeSprintExists = false;
                            $rootScope.activeSprintID = -1;                            
                        }
                        else{
                            $rootScope.activeSprintExists = true;
                            $rootScope.activeSprintID = $rootScope.activeSprint['id'];
                            $rootScope.activeSprintStart = $rootScope.activeSprint['startDate'];
                            $rootScope.activeSprintEnd = $rootScope.activeSprint['endDate'];                            
                        }                        
                    }).error(function(data, status, headers, config) {
                        //alert('Error getting all sprints of project!');
                    });
                }
                else{
                    //undefined current project ID
                }
            }
        }           
        $location.path("/ProjWall");
    };   
    
    $scope.getActiveSprint = function(sprints){
        var activeSprint = null;
        var dataLen = Object.keys(sprints).length;
        for(var j=0; j<dataLen; j++){
            var currSprint = sprints[j];
            var currStartDate = new Date(currSprint['startDate']);
            var currEndDate = new Date(currSprint['endDate']);
            var today = new Date();
            if(currStartDate <= today && today <= currEndDate){
                activeSprint = currSprint;
                break;
            }
        }
        return activeSprint;
    };
});
