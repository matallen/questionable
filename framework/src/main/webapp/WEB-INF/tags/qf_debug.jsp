<input type="button" id="btnDebug" onclick="showHide()" value="Show Debug"/>
			
			<script>
				function showHide(){
					var e=document.getElementById("debug");
					var b=document.getElementById("btnDebug");
			    if (e.style.display === "none") {
			        e.style.display = "block";
			        b.value="Hide Debug";
			    }else{
			        e.style.display = "none";
			        b.value="Show Debug";
			    }
				}
			</script>
  		<div id="debug" style="display: none">
		  	<table>
		  		<tr>
		        <td class="half">
		          <div class="container">
		            <div class="header">Debug</div>
		            <div class="questions">
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
		</div>