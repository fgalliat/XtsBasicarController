function addWindow(id, config) {
     var divWin = creatAwin(id, config );
     divWin.style.left=300+"px";
     divWin.style.top=150+"px";
     divWin.style.zIndex=10000;
     $(".desktop")[0].appendChild( divWin );
  
     var winObj = $("#"+id);
     winObj.draggable( { handle:'.titleBar', cursor:'move', stack:'.desktop' } )

     // helper is a resize border before full window (outline mode)
     winObj.resizable(
        { //helper: "ui-resizable-helper",
          resize: function( event, ui ) {
            //winSetSize(ui.size.width,ui.size.height);
     } } );
  
  winObj.on( "click", "td.LGDXTopLeft", function() {
	    winObj.hide();
	  });
  
  return divWin;
}


function creatAwin(id, config) {

  var divWin = document.createElement("div");
  divWin.style.className = "win";
  divWin.style.position="absolute";
  divWin.style.top=50+"px";
  divWin.style.left=100+"px";
  if ( id == null ) { alert("Window needs an ID"); return null; }
  divWin.id = id;
  
  var winHtml = 
   "<TABLE border=0 cellPadding=0 cellSpacing=0 width='100%' >"+
        "<TR class='titleBar'>"+
          "<TD class=LGDXTopLeft width=10><IMG alt='' height=9 src='files/w1.gif'   width=10></TD>"+
          "<TD align=middle class=LGDXTopMid style='background-image:URL(files/titleBG.png)'><SPAN id='title_"+id +"' style='height:9px;font-size:8px; font-weight:bold;background:#FFF'>&nbsp;&nbsp;&nbsp;TITLE&nbsp;&nbsp;&nbsp;<SPAN></TD>"+
          "<TD class=LGDXTopRight width=10><IMG alt='' height=9   src='files/w2.gif'     width=10></TD></TR>"+
        "<TR>"+
          "<TD class=LGDTopLeft width=10><IMG alt='' height=1 src='files/w0.gif'  width=10></TD>"+
          "<TD class=LGDTopMid><span id='status' style='font-size:9px'></span></TD>"+
          "<TD class=LGDTopRight width=10><IMG alt='' height=9     src='files/w3.gif'   width=10></TD></TR>"+
        "<TR>"+
          "<TD class=LGDMidLeft width=10><IMG alt='' height=1  src='files/w0.gif'   width=10></TD>"+
          "<TD class=LGDMidWin>"+
            "<DIV class='content' id='"+ "content_"+id +"'>your content<br/>your content<br/>your content<br/>your content<br/>your content<br/></DIV>"+
  				"</TD>"+
          "<TD class=LGDMidRight vAlign=bottom width=10><IMG alt=''  height=10    src='files/w4.gif'   width=10></TD></TR>"+
        "<TR>"+
          "<TD class=LGDBotLeft width=10><IMG alt='' height=9   src='files/w7.gif'  width=10></TD>"+
          "<TD align=right class=LGDBotWin><IMG alt='' height=9   src='files/w5.gif' width=11></TD>"+
          "<TD class=LGDBotRight width=10><IMG alt='' height=9   src='files/w6.gif'  width=10></TD>"+
  			"</TR>"+
   "</TABLE>"
  
  divWin.innerHTML = winHtml;

return divWin;
}