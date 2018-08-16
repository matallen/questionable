<!DOCTYPE html>
<html>
<head>
    <title></title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://surveyjs.azureedge.net/0.12.23/survey.jquery.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/bootstrap@3.3.7/dist/css/bootstrap.min.css">

</head>
<body>
    		<div id="surveyContainer"></div>

<script type="text/javascript">
  
  		Survey.Survey.cssType = "bootstrap";
			
			//var surveyJSON = {pages:[{elements:[{type:"checkbox",choices:["Yes","No"],isRequired:true,name:"q1",title:"Do you have a pool on your property?"},{type:"radiogroup",choices:[{value:"small",text:"Less than 15 gallons"},{value:"medium",text:"15 to 25 gallons"},{value:"large",text:"More than 25 gallons"}],name:"q2",title:"How large is your pool?",visible:false,visibleIf:"{q1}='Yes'"}],name:"Insurance Claim"}]}
			
			function sendDataToServer(survey) {
			    survey.sendResult('7b901dcf-2f54-4234-ac04-77ec7a2e60d6');
			}
			
			var survey;// = new Survey.Model(surveyJSON);
			
			//$("#surveyContainer").Survey({
			//    model: survey,
			//    onComplete: sendDataToServer
			//});
			
			
			
			
    var url="/api/pages/surveyjs/start";
		$( document ).ready(function() {
			var xhr = new XMLHttpRequest();
			var ctx = "${pageContext.request.contextPath}";
	    xhr.onreadystatechange = function() { 
      if (xhr.readyState == 4 && xhr.status == 200)
		    var json=JSON.parse(xhr.responseText);
		    console.log("json response = "+json);
				//parseResponse(json,process);
				if (json!==undefined){
				
				  var surveyJSON=json;
				  survey = new Survey.Model(surveyJSON);
				 	$("#surveyContainer").Survey({
			    	model: survey,
			  	  onComplete: sendDataToServer
					});
//				  document.getElementById("questions").innerHTML=parseResponse(json);
//						  // knockout js bind
//						  
//							viewModel = new ViewModel(koJson);
//							ko.applyBindings(viewModel);
				}
    	}
			xhr.open("GET", ctx+url, true);
			xhr.send(null);
		});
  
</script>

</body>
</html>