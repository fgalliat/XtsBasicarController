/** 
 Services.js
 
 service invoker

 @ Xtase fgalliat 09/2016
*/



function getServiceURL(serviceName) {
	//var url = "./services/"+serviceName+SERVER_MODE; // SERVER_MODE from config.js
	var url = "/services/"+serviceName+SERVER_MODE;//+"?rnd="+Math.random(); // SERVER_MODE from config.js
	return url;
}


/**
  modeGetPost = "POST" / "GET" 
  retFunction(rawData, url, data)
*/
function invokeService(serviceName, modeGetPost, params, retFunction) {
	var url = getServiceURL(serviceName);
	if ( params == null ) { params = []; }
	params.push( [ 'rnd', Math.random() ] );
	ajax_call(modeGetPost, url, params, retFunction);
}