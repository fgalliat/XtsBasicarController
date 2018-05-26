<?

/** Service : 
     cat
    Args :
     filename STR
    Returns
     STR fileContent -or- null
*/


 var filename = HttpRequest.queryString.getVal("filename");
 
 // java.lang.System.out.println("QUERY "+filename);

 if ( filename.indexOf("web:") == 0 ) {
   filename = filename.substring( "web:".length );
   // java.lang.System.out.println("GOT a "+filename);
   filename = WebServer.getDocumentRoot().getFile( filename );
 }

 // java.lang.System.out.println("GOT b "+filename);
 
 var content = Packages.IO.cat( filename );
 
 document.write( content );

?>