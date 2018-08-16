

// given a form element, this function parses the data elements and produces a JS object representing the form
function formToJS(sessionId, f) {
  var r={};
  
  for (var i=0; i<f.$$Controls.length; i++){
    r[f[i]['name']]=f[i]['value'];
  }
  return r;
}
		