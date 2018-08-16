                    
	<span ng-if="q.type=='InterestRate'" ng-controller="InterestRateController">
	  <script>
	    app.controller('InterestRateController', ['$scope', '$http', '$parse', function($scope, $http) {
	     $scope.load=function(id, type, params){
	        $scope.models[id]=''; // clear it so when going back and forward it doesn't show any old data until the http reload has executed
	        $http.get('${pageContext.request.contextPath}/api'+controllerName+'/controls/'+type+"?sessionId="+$scope.sessionId+"&type="+type+"&id="+id+"&"+params).then(successCallback, errorCallback);
	        function successCallback(response){
	          if (response.data!=undefined) $scope.models[id]=response.data;
	        }
	        function errorCallback(response){}
	      }
	     }]);
	  </script>
	  <input type="text" disabled ng-model="models[q.id]" ng-init="load(q.id,q.type,q.choices)" class="form-control" ui-percentage-mask="0" ui-hide-space/>
	</span>