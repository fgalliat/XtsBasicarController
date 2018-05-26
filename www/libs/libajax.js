/**

 TODO : a form version to handle cross-domain limitation

*/

function xescape(str) {
 str = escape(str);

 str = str.replace(/\%u2019/g, escape('\'') );  
 str = str.replace(/\%u/g, escape('\\u') );  //--> will be beurk but wont fails
  
 str = str.replace(/\+/g, '%2B'); 
return str;
}

function now() { return new Date().getMilliseconds(); }

function ajax_call(mode, url, /*name,*/ args, handler) {
    //var mode = "POST";
    //var url = "/examples/multiply.php";

	if (args == null) { args = []; }
	
	
    if (window.XMLHttpRequest) { var ajax = new XMLHttpRequest(); }
    else if (window.ActiveXObject) { var ajax = new ActiveXObject("Microsoft.XMLHTTP") }

    var data = null;

    if (mode == "GET") {
        if (url.indexOf("?") == -1) { url = url + "?"; }
        else { url = url + "&"; }
        //url = url + "libajax_function=" + xescape(name);
        //for ( var i = 0; i < args.length-1; i++) { url = url + "&libajax_args[]=" + escape(args[i]); var data = null; }
        for ( var i = 0; i < args.length; i++) { 
          url = url + args[i][0] +"=" + xescape(args[i][1])+"&"; 
        }
        url+="__key="+now();
        data = null;
      
      //alert("GET:"+url);
    }

    else
    {
//        var data = "libajax_function=" + xescape(name);
//        for ( var i = 0; i < args.length-1; i++) { data = data + "&libajax_args[]=" + escape(args[i]); }

        data = "";
        for ( var i = 0; i < args.length; i++) { 
          data = data + args[i][0] +"=" + xescape(args[i][1])+"&"; 
        }
        data+="__key="+now();
      
      
      //alert("POST:"+data);
      
    }

    ajax.open(mode, url, true);
    if(mode == "POST") { ajax.setRequestHeader("Content-type","application/x-www-form-urlencoded"); }

    ajax.onreadystatechange = function() {
        if (ajax.readyState == 4) {
            work = ajax.responseText.lastIndexOf("\n");
            ndata = work == -1 ? ""+ajax.responseText : ajax.responseText.substring(0, work);
            var rawData = ""+ndata;
            if (handler != null) handler( rawData, url, data );
        }
    }

    ajax.send(data);
}

// ==================================================================

/*
function ajax_multiply()
{
    libajax_call("multiply", ajax_multiply.arguments);
}
    function multiply_init(answer) {
        document.getElementById("c").value =  answer;
    }

    function multiply() {
        var a = document.getElementById("a").value;
        var b = document.getElementById("b").value;
        ajax_multiply(a,b,multiply_init);
    }
    */
