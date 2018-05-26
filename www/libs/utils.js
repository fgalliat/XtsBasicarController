/** 
 Utils.js
 
 generic utils

 @ Xtase fgalliat 09/2016
*/


function getFileName(path) {
	path = path.replace(/\\/g, "/");
	var pos = path.lastIndexOf( "/" );
	return pos == -1 ? path : path.substring( pos+1 );
}

// returns ".txt"
function getFileExt(path) {
	var pos = path.lastIndexOf( "." );
	return pos == -1 ? path : path.substring( pos );
}


function myTrim(x) {
    return x.replace(/^\s+|\s+$/gm,'').replace(/^\n+|\n+$/gm,'').replace(/^\r+|\r+$/gm,'');
}

function endsWith(str, toFind) {
  return ( str.substring(str.length-toFind.length) == toFind );
}

function indexOfArry(arry, key) {
	for(var i=0; i < arry.length; i++) {
		if ( arry[i] == key ) { return i; }
	}
	return -1;
}