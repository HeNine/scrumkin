//kontroler za obnašanje about strani
scrumkin.controller('ProgressReport', function($scope, $route, $http, $rootScope, $location, $anchorScroll, LoggedUserService) {
    
    $scope.activeTab = 'burnDownChart';
    
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
    
    //pridobimo vse sprinte tega projekta do sedaj (vključno s trenutno aktivnim)
    $scope.projectSprints = [];
    $scope.yAxis = [];
    $scope.getAllSprintsOfProject = function(){
        if(typeof $rootScope.currentProjectID !== 'undefined'){
            $http({ 
                method: 'GET', 
                url: '/api/project/'+$rootScope.currentProjectID+'/sprints'})
            .success(function(data, status, headers, config) {
                $scope.projectSprints = data;
                //pridobimo vse potrebne podatke oz posortiramo/filtriramo sprinte
                $scope.makeGraphData();
                $scope.yAxis = $scope.yAxisData();
                //Narisemo graf
                $scope.drawGraph();
            }).error(function(data, status, headers, config) {
                //alert('Error getting all sprints of project!');
            });
        }
        else{
            //undefined current project ID
        }
    };
    
    
    $scope.drawGraph = function(){
        
        var data = {
            labels: $scope.xAxisPoints,
            datasets: [
                {
                    fillColor: "rgba(151,187,205,0.5)",
                    strokeColor: "rgba(151,187,205,1)",
                    pointColor: "rgba(151,187,205,1)",
                    pointStrokeColor: "#fff",
                    data: $scope.yAxis
                }/*,
                {
                    fillColor: "rgba(151,187,205,0.5)",
                    strokeColor: "rgba(151,187,205,1)",
                    pointColor: "rgba(151,187,205,1)",
                    pointStrokeColor: "#fff",
                    data: [28, 48, 40, 19, 96, 27, 100]
                }*/
            ]
        };
        
        var options = {
            //Boolean - If we show the scale above the chart data			
            scaleOverlay: false,
            //Boolean - If we want to override with a hard coded scale
            scaleOverride: false,
            //** Required if scaleOverride is true **
            //Number - The number of steps in a hard coded scale
            scaleSteps: null,
            //Number - The value jump in the hard coded scale
            scaleStepWidth: null,
            //Number - The scale starting value
            scaleStartValue: null,
            //String - Colour of the scale line	
            scaleLineColor: "rgba(0,0,0,.1)",
            //Number - Pixel width of the scale line	
            scaleLineWidth: 1,
            //Boolean - Whether to show labels on the scale	
            scaleShowLabels: true,
            //Interpolated JS string - can access value
            scaleLabel: "<%=value%>",
            //String - Scale label font declaration for the scale label
            scaleFontFamily: "'Arial'",
            //Number - Scale label font size in pixels	
            scaleFontSize: 12,
            //String - Scale label font weight style	
            scaleFontStyle: "normal",
            //String - Scale label font colour	
            scaleFontColor: "#666",
            ///Boolean - Whether grid lines are shown across the chart
            scaleShowGridLines: false,
            //String - Colour of the grid lines
            scaleGridLineColor: "rgba(0,0,0,.05)",
            //Number - Width of the grid lines
            scaleGridLineWidth:1,
            //Boolean - Whether the line is curved between points
            bezierCurve: false,
            //Boolean - Whether to show a dot for each point
            pointDot: true,
            //Number - Radius of each point dot in pixels
            pointDotRadius: 3,
            //Number - Pixel width of point dot stroke
            pointDotStrokeWidth: 0.5,
            //Boolean - Whether to show a stroke for datasets
            datasetStroke: true,
            //Number - Pixel width of dataset stroke
            datasetStrokeWidth: 2,
            //Boolean - Whether to fill the dataset with a colour
            datasetFill: false,
            //Boolean - Whether to animate the chart
            animation: false,
            //Number - Number of animation steps
            animationSteps: 60,
            //String - Animation easing effect
            animationEasing: "easeOutQuart",
            //Function - Fires when the animation is complete
            onAnimationComplete: null

        };
        
        //Get the context of the canvas element we want to select
        var ctx = document.getElementById("myChart").getContext("2d");
        new Chart(ctx).Line(data,options);
    };
    
    $scope.getXpoint = function(date){
        
        //TODO REST za vsak datum dobimo podatke o tem dnevu za graf: api/project/projID/burndown/date
        //JSON date
        $scope.date = new Date(date);    
        var month = parseInt($scope.date.getMonth())+1;
        if(month < 10){
            month = '0'+month;
        }
        
        var day = parseInt($scope.date.getDate());
        if(day < 10){
            day = '0'+day;
        }
        
        $scope.jsonDate = $scope.date.getFullYear()+"-"+month+"-"+day;
        
        var returnedData;
        $.ajax({
            type: "GET",
            url: '/api/project/'+$rootScope.currentProjectID+"/burndown/"+$scope.jsonDate,
            dataType: 'json',
            async: false,
            success: function (data) { 
                returnedData = data;
            }
        });
        return returnedData;
    };
    
    $scope.yAxisData = function(){
        /*TODO REST
         * Pridobi za vsak dan v tabeli xAxisDates podatke o remaining time hours za zelen graf
         */
        
        var dateLen = $scope.xAxisDates.length;
        var yAxis = [];
        /*for(var i=0; i<dateLen; i++){
            var date = $scope.xAxisDates[i];
            var rez = $scope.getXpoint(date);
            yAxis.push(rez);
        };*/
        
        //simulacija
        for(var i=0; i<dateLen; i++){
            yAxis.push(dateLen-i);
        }
        
        return yAxis;
        
        /*TODO REST - ni treba
         * Pridobi za vsak dan v tabeli xAxisDates podatke o work load hours za rdeč
         */
    
    };
    
    
    //kdaj se je začel trenutni projekt
    $scope.projectStartDate = '';
    //koliko ur je še remaining na projektu
    $scope.remainingH = '';
    //koliko ur dela je bilo narejenega na projektu
    $scope.doneH = '';
    $scope.xAxisDates = []; //datumi za os x na grafu
    $scope.xAxisPoints = [];
    $scope.makeGraphData = function(){
        var len = $scope.projectSprints.length;
        
        //zacetek projekta
        var firstSprint = $scope.projectSprints[0];
        var projStart = firstSprint['startDate'];
        $scope.projectStartDate = new Date(projStart);
        
        /* naredimo seznam datumov:
         * - če je konec zadnjega sprinta < today --> projekt je zaključen, do tega datum
         * - sicer: do danes
         */
        $scope.today = new Date();
        $scope.graphEndDate = '';
        $scope.projEndDate = $scope.projectStartDate;
        for(var i=0; i<len; i++){
            var currSprint = $scope.projectSprints[i];
            var currSprintEnd = new Date(currSprint['endDate']);            
            //ce je currSprintEnd > $scope.projEndDate --> projEndDate = currSprintEnd
            if(currSprintEnd > $scope.projEndDate){
                $scope.projEndDate = currSprintEnd;
            }
        }
        
        if($scope.projEndDate < $scope.today){
            $scope.graphEndDate = $scope.projEndDate;
        }
        else{
            $scope.graphEndDate = $scope.today;
        }
        
        //nardimo tabelo datumov
        $scope.xAxisDates = getDates($scope.projectStartDate, $scope.graphEndDate);
        for(var i=0; i<$scope.xAxisDates.length; i++){
            $scope.xAxisPoints.push(i+1);
        }
    };    
});


Date.prototype.addDays = function(days) {
    var dat = new Date(this.valueOf());
    dat.setDate(dat.getDate() + days);
    return dat;
};

function getDates(startDate, stopDate) {
    var dateArray = new Array();
    var currentDate = startDate;
    
    while (currentDate <= stopDate) {
        //var json = {"date":currentDate, "doneDB":0, "remainingDB":0, "changed":false};
        dateArray.push(currentDate);
        currentDate = currentDate.addDays(1);
    }
    return dateArray;
}
