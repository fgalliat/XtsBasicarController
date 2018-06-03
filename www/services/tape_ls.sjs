<?

/** Service : 
     tape_ls
    Args :
     path STR
    Returns
     STR -XML-
*/


 var path = HttpRequest.queryString.getVal("path");
 if ( path == null ) {
     path = "web:/data";
 }
 
 var displayedPath = ""+path;
 var usedPath = ""+path;
 
 if ( usedPath.indexOf("web:") == 0 ) {
   displayedPath = displayedPath.substring( "web:".length );
   
   usedPath = usedPath.substring( "web:".length );
   usedPath = WebServer.getDocumentRoot().getFile( usedPath );
 }
 
 var pathObj = new java.io.File( usedPath );
 
 if ( !pathObj.exists() || !pathObj.isDirectory() ) {
   document.writeln("-Oups-");
 } else {
 
  var content = "";
  var contentOfDir = pathObj.listFiles();
  
  contentOfDir.sort(
    function(a,b) {
      if ( a.isDirectory() ) {
        if ( b.isDirectory() ) {
          return a.getName().toLowerCase().compareTo( b.getName().toLowerCase() );
        } else {
          return -1
        }
      } else {
        if ( b.isDirectory() ) { return 1; }
        else { return a.getName().toLowerCase().compareTo( b.getName().toLowerCase() ); }
      }

    }
  );
  
  var content = "";  
  for(var i=0; i < contentOfDir.length; i++) {
    var entryObj = contentOfDir[i];
    var isDir    = entryObj.isDirectory();
    
    var entry = entryObj.getName() + ( isDir ? "/" : "" );

    if ( !isDir ) {
      // url = "javascript:"+ fctNameFileHdl +"(&quot;"+ (path+""+entry) +"&quot;)";

      if ( entry.lastIndexOf(".BAS") == entry.length-4 ) {
          java.lang.System.out.println("BASIC FILE");
          if ( i < contentOfDir.length-1 ) {
              var nextEntry = contentOfDir[i+1].getName();
              /*
              java.lang.System.out.println("NEXT IS : "+nextEntry);
              java.lang.System.out.println("NEXT IS (1): "+nextEntry.substring(0, nextEntry.lastIndexOf(".")) );
              java.lang.System.out.println("NEXT IS (2): "+entry.substring(0, entry.lastIndexOf(".")) );

              java.lang.System.out.println("NEXT IS (3): "+ (nextEntry.lastIndexOf(".TXT") == nextEntry.length-4) );

              java.lang.System.out.println("NEXT IS (3.1): "+ (nextEntry.lastIndexOf(".TXT") ) );
              java.lang.System.out.println("NEXT IS (3.2): "+ ((""+nextEntry).length-4) );

              java.lang.System.out.println("NEXT IS (4): "+ (nextEntry.substring(0, nextEntry.lastIndexOf(".")) == entry.substring(0, entry.lastIndexOf("."))) );
              */

              if ( nextEntry.lastIndexOf(".TXT") == (""+nextEntry).length-4 &&
                   nextEntry.substring(0, nextEntry.lastIndexOf(".")) == entry.substring(0, entry.lastIndexOf("."))
                 ) {
                     java.lang.System.out.println("SKIPED");
                     continue;
                 }
          }
      }

      content += ""+entry+"\n";
    }
               
  }
  
  document.write( content );
 }
?>