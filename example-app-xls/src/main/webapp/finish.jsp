<!doctype html>
	<html ng-app="myApp">
	
	  <head>
	    <script>document.write('<base href="' + document.location + '" />');</script>
	    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.6.6/angular.min.js"></script>
	    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment-with-locales.js"></script>
	    <script src="js/utils.js" type="text/javascript"></script>
	    <!-- datetimepicker v4.17.47 -->
	    <script src="https://cdn.rawgit.com/Eonasdan/bootstrap-datetimepicker/25c11d79e614bc6463a87c3dd9cbf8280422e006/src/js/bootstrap-datetimepicker.js"/></script>
	    <link rel="stylesheet" type="text/css" media="screen" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" />
	    <link href="https://cdn.rawgit.com/Eonasdan/bootstrap-datetimepicker/e8bddc60e73c1ec2475f827be36e1957af72e2ea/build/css/bootstrap-datetimepicker.css" rel="stylesheet">
	    
	    <link href="css/bootstrap-slide-switch.css" rel="stylesheet">
	    <link rel="stylesheet" type="text/css" href="css/demo.css">
	    
	    <script src="https://github.com/chartjs/Chart.js/releases/download/v2.6.0/Chart.min.js"></script>
	    <script src="js/chart-support.js"/></script>
	    
	    
	    <script src="components/number-spinner.js"/></script>
	    <link  href="components/number-spinner.css" rel="stylesheet" type="text/css">
	    
	    <script src="components/percentage.js"/></script>
			
	  </head>
  
  <script>
    
	  var app = angular.module('myApp', []);
	  
	  var controllerName="<%=com.redhat.sso.wizard.view.SimpleController.class.getAnnotation(javax.ws.rs.Path.class).value()%>";
	  
	  app.controller('FormController', ['$scope', '$http', '$parse', 
	    function($scope, $http) {
	      $scope.master = {};
	      $scope.models = {};
	      $scope.questions={};
	      
	      $http.get('${pageContext.request.contextPath}/api'+controllerName+'/pages/result?sessionId=<%=request.getParameter("sessionId")%>').then(successCallback, errorCallback);
	      function successCallback(response){
	      	$scope.data = response.data;
	      	
	      	$scope.questions=[];
	      	$scope.models={};
	      	
	      	// Amalgamate all the questions/answers in a single page summary
	      	for(i=0;i<response.data.length-1;i++){
	      		if (null!=response.data[i].questions){
	      			for(q=0;q<response.data[i].questions.length-1;q++){
			      		$scope.questions.push(response.data[i].questions[q]);
			      		$scope.models[response.data[i].questions[q]['id']]=response.data[i].questions[q]['value'];
	      			}
	      		}
	      	}
	      	
	      }
	      function errorCallback(response){
	      	console.log(response.data);
	      }
	      
	    }
	  ]);
	</script>
	
	<body ng-controller="FormController">
		
		<div class="col-sm-4 bold">
					<div class="header">A sample Finish page</div>
					<div class="questions">
						<div ng-repeat="q in questions">
							<div class="row">
								<div class="col-sm-4"><label for="{{q.id}}">{{q.title}}</label></div>
								<div class="col-sm-4">
									<input type="text"    id="{{q.id}}" name="{{q.id}}" class="form-control" ng-model="models[q.id]" ng-disabled="true" placeholder="{{q.placeholder}}"/><br/>
								</div>
							</div>
						</div>
					</div>
			</div>
		
	</body>
</html>


<!--
		  	<table>
		  		<tr>
		        <td class="half">
		          <div class="container">
		            <div class="header">Debug</div>
		            <div class="questionsx">
		            	<div><b>SessionID:</b> <span id="">{{ sessionId }}</span></div>
							    <pre>models   = {{models | json}}</pre>
							    <pre>questions   = {{questions | json}}</pre>
							    <pre>master = {{master | json}}</pre>
		            </div>
		          </div>
		        </td>
		  		</tr>
	  		</div>
	  	</table>
-->