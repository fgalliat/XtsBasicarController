<?

/** Service : 
     tape_decode
    Args :
     content STR
    Returns
     STR preprocessed source content -or- null
*/

 var content = HttpRequest.queryString.getVal("content");
 
 document.writeln( "GOT SOURCE...." );
 
 // the problem was that classes were compiled with JDK 1.8
 var src = Packages.com.xtase.virtualtapemanager.x07.basic.preprocessor.PreProcessor.decode( content );

 document.writeln( "DECODED SOURCE...." );

 Packages.com.xtase.virtualtapemanager.x07.filesystem.X07TapeFileSystem.write( "CASO:", src, false );

 document.writeln( "TRANSCRIPTED TO WAV...." );
 
 document.writeln( "SENT...." );

?>