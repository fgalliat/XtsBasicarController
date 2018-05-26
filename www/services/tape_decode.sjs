<?

/** Service : 
     tape_decode
    Args :
     content STR
    Returns
     STR preprocessed source content -or- null
*/

 var content = HttpRequest.queryString.getVal("content");
 
 try {
   src = Packages.com.xtase.xtsubasic.PreProcessor.decodeSafe( content );
 } catch(ex) {
   // ex.printStackTrace();
   src= "Could not preprocess source code ... ("+ex+")";
 }

 document.write( ""+src );

?>