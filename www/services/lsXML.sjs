<?

/** Service : 
     lsXML
    Args :
     path STR
     fctNameFileHdl STR OPTIONAL name of function to launch when click on a file
    Returns
     STR -XML-
*/


 var path = HttpRequest.queryString.getVal("path");
 var fctNameFileHdl = HttpRequest.queryString.getVal("fctNameFileHdl");
 if ( fctNameFileHdl == null ) {
   fctNameFileHdl = "select"
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
  
  
  for(var i=0; i < contentOfDir.length; i++) {
    var entryObj = contentOfDir[i];
    var isDir    = entryObj.isDirectory();
    
    var entry = entryObj.getName() + ( isDir ? "/" : "" );
    var icon = !isDir ? "file" : "folder"; // or "symlink"
    var url = "javascript:void(0);";
    if ( !isDir ) {
      //url = "javascript:"+ fctNameFileHdl +"(&quot;"+ (usedPath+"/"+entry) +"&quot;)";
      url = "javascript:"+ fctNameFileHdl +"(&quot;"+ (path+""+entry) +"&quot;)";
    }
    var exptAttribute = "";
    
    //var subTree = !isDir ? "" : "retreiveUrl=\"./services/lsXML.sjs?path="+ (usedPath+"/"+entry) +"&amp;fctNameFileHdl="+ fctNameFileHdl +"\"";
    var subTree = !isDir ? "" : "retreiveUrl=\"./services/lsXML.sjs?path="+ (path+""+entry) +"&amp;fctNameFileHdl="+ fctNameFileHdl +"\"";
    content += (" <node caption=\""+entry+"\" url=\""+ url +"\" target=\"_self\" "+ subTree +
               " icon=\"./libs/tree/img/"+ icon +".gif\" "+  exptAttribute +" />\n");
               
  }
  
  document.write( "<tree>\n" );
  document.write( content );
  document.write( "</tree>\n" );
 }
?>