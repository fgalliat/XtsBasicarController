<?

/** Service : 
     save
    Args :
     filename STR
     filecontent STR
    Returns
     STR "OK" -or- "NOK"
*/


 var filename = HttpRequest.queryString.getVal("filename");
 var filecontent = HttpRequest.queryString.getVal("filecontent");
 
 if ( filename == null || ""+filename == "undefined" ) {
   document.write( "NOK "+"undefined filename" );
 } else if ( filecontent == null || ""+filecontent == "undefined" ) {
   document.write( "NOK "+"undefined filecontent" );
 } else {
	 if ( filename.indexOf("web:") == 0 ) {
	   filename = filename.substring( "web:".length );
	   filename = WebServer.getDocumentRoot().getFile( filename );
	 }
	 
	 try {
	   Packages.IO.write( filename, filecontent );
	   document.write( "OK" );
	 } catch(ex) {
	   document.write( "NOK "+ex );
	 }
 }

?>