
//Define an angular module for our app
var scrumkin = angular.module('scrumkin', []);

//Define Routing for app
scrumkin.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/ProjWall', {
                templateUrl: 'app/views/ProjectWall.html',
                controller: 'ProjWall'
            }).
            when('/ProgressReport', {
                templateUrl: 'app/views/ProgressReport.html',
                controller: 'ProgressReport'
            }).
            when('/ChooseProject', {
                templateUrl: 'app/views/ChooseProject.html',
                controller: 'ChooseProject'
            }).
            when('/Login', {
                templateUrl: 'app/views/Login.html',
                controller: 'Login'
            }).
            when('/PBacklog', {
                templateUrl: 'app/views/PBacklog.html',
                controller: 'PBacklog'
            }).
            when('/Logout', {
                templateUrl: '',
                controller: 'Logout'
            }).
            when('/AdminConsole', {
                templateUrl: 'app/views/AdminConsole.html',
                controller: 'AdminConsole'
            }).
            when('/ScrumMasterConsole', {
                templateUrl: 'app/views/ScrumMasterConsole.html',
                controller: 'ScrumMasterConsole'
            }).
            when('/404', {
                templateUrl: 'app/views/404.html',
                controller: ''
            }).
            when('/SBacklog', {
                templateUrl: 'app/views/SBacklog.html',
                controller: 'SBacklog'
            }).
            when('/MyTasks', {
                templateUrl: 'app/views/MyTasks.html',
                controller: 'MyTasks'
            }).
            when('/Progress', {
                redirectTo: '404'
            }).
            otherwise({
                redirectTo: 'Login'
            });
    }]);

scrumkin.service('LoggedUserService', function(){
    this.getUserToken = function(){
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/login',
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            },
            error: function(){
                //401 unauthorised
            }
        });
        return returnedData;
    };    
});

scrumkin.service('UserService', function(){
    this.getUser = function(userID){
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/users/'+userID,
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            },
            error: function(){
                //401 unauthorised
            }
        });
        return returnedData;
    }; 
});

scrumkin.service('StoryService', function(){
    this.getUserStory = function(storyID){
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/userStories/'+storyID,
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            },
            error: function(){
                //401 unauthorised
            }
        });
        return returnedData;
    }; 
    
    this.getStoryTask = function(taskID){
        //funkcija, ki vrne task za neko zgodbo
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/tasks/'+taskID,
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };
    
    this.getStoryComment = function(story){
        //funkcija, ki vrne komentarje na zgodbi
        var returnedData;
        
        $.ajax({
            type: "GET",
            url: '/api/userStories/'+story['id']+'/comments',
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };
});

scrumkin.service('AcceptanceTestsService', function(){
    this.isStoryFinished = function(story){
        //funkcija, ki preveri ali je zgodba koncana - ali jo je PO že potrdiu       
        var tests = story['acceptenceTests'];        
        //zgodba je koncana, ko so vsi testi koncani
        var finished = true; 
        for(var i=0; i<tests.length; i++){
            var currTestID = tests[i];
            var test = this.getAccTest(story['id'], currTestID);                      
            if(typeof test['accepted'] === 'undefined'){
                //test ni bil ocenjen - ce eden ali vec ni ocenjen, potem zgodba ni sprejeta
                finished = finished && false; 
            }
            else{
                //ce je accepted == true --> bil sprejet, ce accepted === false --> bil zavrnjen
                finished = finished && test['accepted'];
            }            
        }
        return finished;
    };
    
    this.setTestsAccepted = function(accepted, story){
        var successT = true;
        //metoda, v kateri vse teste nastavimo na true ali false == accepted --> sprejeta zagodba ali zavrnjena
        var tests = story['acceptenceTests'];   //ID-ji testov te zgodbe
        
        for(var i=0; i<tests.length; i++){
            var testID = tests[i];
            var storyID = story['id'];
            //na api/userStories/storyID/tests/testID posljemo PUT: param = accepted: true/false
            $.ajax({
                type: "PUT",
                url: '/api/userStories/'+storyID+'/tests/'+testID,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                async: false,
                data: JSON.stringify({accepted: accepted}),
                success: function (data) { 
                    successT = successT && true;
                },
                error: function(){
                    successT = successT && false;
                }
            });
        }
        return successT;
    };
    
    this.isStoryRejected = function(story){
        //funkcija, ki preveri ali je zgodba Že bila zavrnjena s strani PO    
        var tests = story['acceptenceTests'];        
        //zgodba je zavrnjena, ko so vsi testi FALSE
        var rejected = true; 
        for(var i=0; i<tests.length; i++){
            var currTestID = tests[i];
            var test = this.getAccTest(story['id'], currTestID);                      
            if(test['accepted'] === false){
                //ce accepted === false --> bil zavrnjen
                rejected = rejected && true;
            }      
            else{
                //ce je accepted === true --> bil sprejet, ce undefined --> test ni bil ocenjen zgodba ni bila se sprejeta, ampak tudi ni bila zavrnjena
                rejected = rejected && false; 
            }
        }
        return rejected;
    };
    
    this.isStoryAccepted = function(story){
        //funkcija, ki preveri ali je zgodba Že bila sprejeta s strani PO    
        var tests = story['acceptenceTests'];        
        //zgodba je zavrnjena, ko so vsi testi FALSE
        var accepted = true; 
        for(var i=0; i<tests.length; i++){
            var currTestID = tests[i];
            var test = this.getAccTest(story['id'], currTestID);                      
            if(test['accepted'] === true){
                //ce accepted === true --> bil sprejet
                accepted = accepted && true;
            }      
            else{
                //ce je accepted === false --> ni bil sprejet, ce undefined --> test ni bil ocenjen zgodba ni bila se sprejeta, ampak tudi ni bila zavrnjena
                accepted = accepted && false; 
            }
        }
        return accepted;
    };
    
    this.getAccTest = function(storyID, testID){
        //funkcija, ki vrne acceptance test za neko zgodbo
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/userStories/'+storyID+'/tests/'+testID,
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };    
});
