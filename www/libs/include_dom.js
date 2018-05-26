/**

 Add Some TAG in Headers

*/

function include_js(script_filename, inHead) {
	 if ( inHead == null ) inHead = true;
	 //alert('include_dom '+script_filename);
    var html_doc = inHead ? 
                     document.getElementsByTagName('head').item(0) :
                     document.body/*.getElementsByTagName('body').item(0)*/
                   ;
    var js = document.createElement('script');
    js.setAttribute('language', 'javascript');
    js.setAttribute('type', 'text/javascript');
    js.setAttribute('src', script_filename);
    if ( inHead ) { html_doc.appendChild(js); }
    else { document.write("<"+"script src=\""+ script_filename +"\"></"+"script>"); }
    return false;
}
