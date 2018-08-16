
function refreshGraph(graphName, type){
  buildChart(graphs[graphName], graphName, type);
}

function resetCanvas(chartElementName){
  $('#'+chartElementName).remove(); // this is my <canvas> element
  $('#'+chartElementName+'_container').append('<canvas id="'+chartElementName+'"><canvas>');
  var canvas = document.querySelector('#'+chartElementName);
  var ctx = canvas.getContext('2d');
  ctx.canvas.width = $('#'+chartElementName+'_container').width();
  ctx.canvas.height = $('#'+chartElementName+'_container').height()+100;
}

function buildChart(uri, chartElementName, type){
  var xhr = new XMLHttpRequest();
  var ctx = "${pageContext.request.contextPath}";
  xhr.open("GET", ctx+uri, true);
  xhr.send();
  xhr.onloadend = function () {
    var json=JSON.parse(xhr.responseText);
    var income = document.getElementById(chartElementName).getContext("2d");
    if (type=="Bar"){
	    new Chart(income).Bar(json, barOptions);
	  }else if (type=="BarNoLabels"){
	    var barOptions2=JSON.parse(JSON.stringify(barOptions));
	    barOptions2.inGraphDataShow=false;
	    new Chart(income).Bar(json, barOptions2);
	  }else if (type=="HorizontalBar"){
	    var barOptions3=JSON.parse(JSON.stringify(hbarOptions));
	    barOptions3.inGraphDataShow=true;
	    barOptions3.inGraphDataAlign="right";
	    barOptions3.inGraphDataVAlign="top";
	    barOptions3.inGraphDataPaddingX=10;
	    barOptions3.inGraphDataAlign="left"
	    
	    resetCanvas(chartElementName);
	    income = document.getElementById(chartElementName).getContext("2d");
	    
	    new Chart(income, {
                type: 'horizontalBar', 
                data: json,
                options: {"scales":{"xAxes":[{"ticks":{"beginAtZero":true}}]},legend: {display:false}}
            });
            
	  }else if (type=="BarShortLabels"){
	    var barOptions3=JSON.parse(JSON.stringify(barOptions));
	    barOptions3.inGraphDataTmpl="<\%=rename(v1).replace(' - Sales Kit','').replace(' - Theme','')\%>";
	    new Chart(income).Bar(json, barOptions3);
    }else if (type=="Pie"){
	    new Chart(income).Pie(json, pieOptions);
    }else if (type=="Line"){
      resetCanvas(chartElementName);
      income = document.getElementById(chartElementName).getContext("2d");
	    new Chart(income, {
                type: 'line', 
                data: json,
                options: {"scales":{"xAxes":[{"ticks":{"beginAtZero":true}}]},legend: {display:true}}
            });
    }
  }
}