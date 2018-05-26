<?

/** Service : 
     tape_decode
    Args :
     content STR
    Returns
     STR preprocessed source content -or- null
*/

 var content = HttpRequest.queryString.getVal("content");
 
 /*
 try {
   // the problem was that classes were compiled with JDK 1.8
   // var src = Packages.com.xtase.virtualtapemanager.x07.basic.preprocessor.PreProcessor.decode( content );
 } catch(ex) {
     src= "Could not preprocess source code ... ("+ex+")";
 }
 */

// java.lang.System.out.println("====================");
// java.lang.System.out.println(content);

 var src= "Could not preprocess source code ...";
 
// java.lang.System.out.println(src);

 document.write( ""+src+" "+new Date().toString() );

?>