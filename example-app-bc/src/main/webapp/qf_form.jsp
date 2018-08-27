  
  
    <head>
      <script>document.write('<base href="' + document.location + '" />');</script>
  	  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
  	  <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.6.6/angular.min.js"></script>
  		<script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment-with-locales.js"></script>
  		<script src="js/utils.js" type="text/javascript"></script>
  		
  	  <link rel="stylesheet" type="text/css" media="screen" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" />
  		<!-- datetimepicker v4.17.47 -->
  		<script src="https://cdn.rawgit.com/Eonasdan/bootstrap-datetimepicker/25c11d79e614bc6463a87c3dd9cbf8280422e006/src/js/bootstrap-datetimepicker.js"/></script>
  	  <link href="https://cdn.rawgit.com/Eonasdan/bootstrap-datetimepicker/e8bddc60e73c1ec2475f827be36e1957af72e2ea/build/css/bootstrap-datetimepicker.css" rel="stylesheet">
  		<!--
  	  -->
  	  
  	  <link href="css/bootstrap-slide-switch.css" rel="stylesheet">
      <link rel="stylesheet" type="text/css" href="css/demo.css">
      
      <!--
      <script src="https://github.com/chartjs/Chart.js/releases/download/v2.6.0/Chart.min.js"></script>
      <script src="js/chart-support.js"/></script>
      
      <script src="components/number-spinner.js"/></script>
      <link  href="components/number-spinner.css" rel="stylesheet" type="text/css">
      
      <script src="components/percentage.js"/></script>
      -->
      
      <!-- for percentage & money masks -->
      <!-- source: https://github.com/assisrafael/angular-input-masks -->
			<script src="js/angular-input-masks-standalone.min.js"></script>
  		
    </head>
  
  <script>
    
    //var app = angular.module('myApp', []);
    var app = angular.module('myApp', ['ui.utils.masks']);
    
    var controllerName="<%=com.redhat.sso.wizard.view.SimpleController.class.getAnnotation(javax.ws.rs.Path.class).value()%>";
    
    app.controller('FormController', ['$scope', '$http', '$parse', '$location', function($scope, $http, $location) {
        $scope.master = {};
        $scope.models = {};
        $scope.questions={};
        
        $http.get('${pageContext.request.contextPath}/api'+controllerName+'/pages/start').then(successCallback, errorCallback);
        function successCallback(response){
          $scope.config = response.data
          $scope.backButtonEnabled=response.data.allowBack;
          $scope.nextButtonEnabled=response.data.allowNext;
          $scope.finishButtonEnabled=response.data.allowFinish;
          
          $scope.sessionId = response.data.session;
          $scope.group = response.data.groups[0].name;
          $scope.models.group=$scope.group; // is this still used? can it be removed?
          console.log("sessionId="+$scope.sessionId);
          $scope.questions=response.data.groups[0].questions;
          
          // set the "value" of any question if present
          angular.forEach($scope.questions, function(question, key){
            if (question.value!=undefined){
              $scope.models[question.id]=question.value;
              
            }
            //write the regex from the variable so it can be used by angular
            if (undefined!=question.validation){
              $scope[question.id+"RegEx"]=new RegExp(question.validation); //not my favorite method putting this on base $scope but it works at last so I'm leavin it for now
            }
          });
        }
        function errorCallback(error){
        }
        
        $scope.next=function(){
        	var payload=JSON.stringify($scope.models);
          console.log("Next:: Posting form data: sessionId["+$scope.sessionId+"] => data["+payload+"]");
        	$http.post('${pageContext.request.contextPath}/api'+controllerName+'/pages/next?sessionId='+$scope.sessionId, payload).then(successCallback, errorCallback);
        };
        
        $scope.back=function(){
        	var payload=JSON.stringify($scope.models);
          console.log("Back:: Posting form data: sessionId["+$scope.sessionId+"] => data["+payload+"]");
        	$http.post('${pageContext.request.contextPath}/api'+controllerName+'/pages/back?sessionId='+$scope.sessionId, payload).then(successCallback, errorCallback);
        };
        
        $scope.finish=function($location){
        	var payload=JSON.stringify($scope.models);
          console.log("Finish:: Posting form data: sessionId["+$scope.sessionId+"] => data["+payload+"]");
        	$http.post('${pageContext.request.contextPath}/api'+controllerName+'/pages/finish?sessionId='+$scope.sessionId, payload).then(finishCallback, errorCallback);
        	
        };
        
        function finishCallback(response){
        	window.location='finish.jsp?sessionId='+response.data.sessionId;
        }
        
        $scope.reset = function() {
            console.info('Form Reset');
            $scope.models = angular.copy( $scope.master );
        };
        
        $scope.concat = function(one, two, delimiter) {
        	return one+delimiter+two;
        }
        
        $scope.reset();
    
    }])
//	  .filter('num', function() {    //usage: {{'1'|num}}
//	    return function(input) {
//	      return parseInt(input, 10);
//	    };
//	  })
    ;
	  
  </script>
  	
    <form id="myForm" name="myForm" role="form" ng-controller="FormController" novalidate>
      
        <div>
          <div class="header">{{group}}</div>
          
          <div ng-repeat="q in questions">
            <div ng-show="{{q.visibleIf}}">
            	<div class="question">
              	<div class="row">
                  <div class="col-sm-4">
                  	<label for="{{q.id}}">{{q.title}}<span ng-if="q.required==true"><span class="required">&nbsp;*</span></span></label>
                  </div>
                  <div class="col-sm-4">
                  	
                  	<!-- ############## -->
                  	<!-- ## Controls ## -->
                  	<!-- ############## -->
                  	<span ng-if="q.type=='text'">
                      <input type="text"       id="{{q.id}}" name="{{q.id}}" class="form-control" ng-model="models[q.id]" ng-required="{{q.required}}" ng-disabled="!({{q.enabledIf}})" ng-pattern="{{q.id}}RegEx" ng-change="{{q.onChange}}" placeholder="{{q.placeholder}}"/>
                    </span>
                  	<span ng-if="q.type=='number'">
                      <input type="number"     id="{{q.id}}" name="{{q.id}}" class="form-control" ng-model="models[q.id]" ng-required="{{q.required}}" ng-disabled="!({{q.enabledIf}})" ng-pattern="{{q.id}}RegEx" ng-change="{{q.onChange}}" placeholder="{{q.placeholder}}"/>
	                  </span>
	                  <span ng-if="q.type=='datetime'"><!-- dependent on Eonasdan's "bootstrap-datetimepicker.js"-->
		                  <div class="input-group date" id="{{q.id}}">
	                      <input type="text" class="form-control" ng-model="models[q.id]" ng-blur="$scope.models[q.id]='asd'"/>
	                      <span class="input-group-addon">
	                        <span class="glyphicon glyphicon-calendar"></span>
	                      </span>
	                    </div>
	                    <script>
						            $(function () {
						                $('.date').datetimepicker();
						            });
	                    </script>
                    </span>
	                  <span ng-if="q.type=='percentage'"><!-- dependent on "angular-input-masks-standalone.min.js" -->
                      <input type="text"     id="{{q.id}}" name="{{q.id}}" class="form-control" ng-model="models[q.id]" ng-required="{{q.required}}" ng-disabled="!({{q.enabledIf}})" ng-change="{{q.onChange}}" placeholder="{{q.placeholder}}" ui-percentage-mask="0" ui-hide-space/><br/>
	                  </span>
	                  <span ng-if="q.type=='money'"><!-- dependent on "angular-input-masks-standalone.min.js" -->
                      <input type="text"     id="{{q.id}}" name="{{q.id}}" class="form-control" ng-model="models[q.id]" ng-required="{{q.required}}" ng-disabled="!({{q.enabledIf}})" ng-change="{{q.onChange}}" placeholder="{{q.placeholder}}" ui-money-mask="2" ui-hide-space/><br/>
	                  </span>
	                  <span ng-if="q.type=='date'"><!-- dependent on "angular-input-masks-standalone.min.js" -->
                      <input type="text"     id="{{q.id}}" name="{{q.id}}" class="form-control" ng-model="models[q.id]" ng-required="{{q.required}}" ng-disabled="!({{q.enabledIf}})" ng-change="{{q.onChange}}" placeholder="{{q.placeholder}}" ui-date-mask="DD-MM-YYYY"/><br/>
	                  </span>
	                  <span ng-if="q.type=='checkbox'">
	                    <input type="checkbox" id="{{q.id}}" name="{{q.id}}"  ng-model="models[q.id]" ng-required="{{q.required}}" ng-change="{{q.onChange}}" ng-disabled="!({{q.enabledIf}})"/><br/>
	                  </span>
	                  <span ng-if="q.type=='slide-switch'">
                      <div class="material-switch pull-right">
                          <input id="{{q.id}}" name="{{q.id}}" type="checkbox" ng-model="models[q.id]" ng-required="{{q.required}}" ng-change="{{q.onChange}}"/>
                          <label for="{{q.id}}" class="label-default"></label>
                      </div>
                    </span>
                    <span ng-if="q.type=='radiogroup'">
                    	<div ng-repeat="c in q.choices">
        	            	<input type="radio" id="{{q.id}}_{{c}}" name="{{q.id}}" value="{{c}}" ng-model="models[q.id]" ng-change="{{q.onChange}}"/><label for="{{q.id}}_{{c}}">{{c}}</label>
                    	</div>
                    </span>
                    <span ng-if="q.type=='label'">
                    	<input type="text"    id="{{q.id}}" name="{{q.id}}" class="form-control" ng-model="models[q.id]" ng-disabled="true" placeholder="{{q.placeholder}}"/><br/>
                    </span>
                    
                    <span ng-if="q.type=='BankAccounts'" ng-controller="BankAccountsController">
                      <script>
                        app.controller('BankAccountsController', ['$scope', '$http', '$parse', function($scope, $http) {
                          $scope.load=function(id, type, params){
                            $scope.models[id]=''; // clear it so when going back and forward it doesn't show any old data until the http reload has executed
                            console.log("BankAccounts.params="+params);
                            $http.get('${pageContext.request.contextPath}/api'+controllerName+'/controls/'+type+"?sessionId="+$scope.sessionId+"&id="+id+"&"+params).then(successCallback, errorCallback);
                            function successCallback(response){
                              $scope.bankAccountsOptions=response.data;
                            }
                            function errorCallback(response){
                              //alert("BankAccountsController error = "+response.data);
                            }
                          }
                        }]);
                      </script>
                      <select id="{{q.id}}" name="{{q.name}}" ng-model="models[q.id]" ng-init="load(q.id,q.type,q.custom)" class="form-control">
                        <option ng-repeat="o in bankAccountsOptions" value="{{o.value}}">{{o.value}}</value>
                      </select>
                    </span>
                    
                    <jsp:include page="qf_custom-controls.jsp"/>
                    
                                    
                    <!-- ## Add New Controls Here ## -->
                    <!--
                    <span class="error" ng-show="myForm.{{q.id}}.$error.pattern">{{q.errorMessage}}</span>
                    -->
                  </div>
                  <div class="col-sm-4">
                  	<span class="error" ng-show="myForm.{{q.id}}.$error.pattern">{{q.errorMessage}}</span>
                  </div>
                  <!--
                  -->
                </div>
              </div>
            </div>
          </div>
          
        </div>
        <div class="bar">
          <input   class="buttons form-control" type="button" value="Back"     ng-disabled="!config.allowBack || myForm.$invalid"   ng-class="{'disableButton': !config.allowBack || myForm.$invalid}"   ng-click="back();"/>
      		<input   class="buttons form-control" type="button" value="Next"     ng-disabled="!config.allowNext || myForm.$invalid"   ng-class="{'disableButton': !config.allowNext || myForm.$invalid}"   ng-click="next();"/>
          <input   class="buttons form-control" type="button" value="Finish"   ng-disabled="!config.allowFinish || myForm.$invalid" ng-class="{'disableButton': !config.allowFinish || myForm.$invalid}" ng-click="finish();"/>
          <!-- move buttons in here if you want them to be visible if allowed, hidden when not-->
        	<span ng-if="!config.allowFinish">
        	</span>
          <span ng-if="config.allowFinish">
          </span>
          <!--
          <br/>allowBack={{config.allowBack}}, allowNext={{config.allowNext}}, allowFinish={{config.allowFinish}}
          -->
        </div>
      
			<%if ("true".equalsIgnoreCase(request.getParameter("debug"))){%>
				<div ng-include="'debug.jsp'"></div>
			<%}%>
      
	</form>
