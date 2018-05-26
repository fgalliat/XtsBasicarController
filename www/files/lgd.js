if (document.images) {

var imglist = new Array ("/g/m1b.gif","/g/m2b.gif","/g/m3b.gif","/g/m4b.gif","/g/m1_1b.gif","/g/m4_2b.gif","/g/m4_3b.gif","/g/m4_4b.gif","/g/m3_1b.gif","/g/m3_2b.gif","/g/m3_3b.gif","/g/m3_5b.gif","/g/m3_6b.gif","/g/m3_7b.gif","/g/m2_1b.gif","/g/m2_2b.gif","/g/m2_3b.gif","/g/m2_5b.gif","/g/m2_6b.gif","/g/m2_7b.gif","/g/m2_8b.gif","/g/m2_9b.gif","/g/m2_10b.gif","/g/m2_11b.gif");
var imgs = new Array(); var count;

for (count=0; count<imglist.length; count++)
 {imgs[count]=new Image(); imgs[count].src=imglist[count];}
 }

 function hilite(name,m)
 {if (document.images)
 {imgswap(name, imgs[m])}
 }

 function imgswap(i1,i2)
 {if (document.images)
 {var temp = i1.src; i1.src=i2.src; i2.src=temp;}

}

window.onerror = null;
 var bName = navigator.appName;
 var bVer = parseInt(navigator.appVersion);
 var NS4 = (bName == "Netscape" && bVer >= 4);
 var IE4 = (bName == "Microsoft Internet Explorer" && bVer >= 4);
 var NS3 = (bName == "Netscape" && bVer < 4);
 var IE3 = (bName == "Microsoft Internet Explorer" && bVer < 4);
 var menuActive = 0
 var menuOn = 0
 var onLayer
 var timeOn = null// LAYER SWITCHING CODE
if (NS4 || IE4) {
 if (navigator.appName == "Netscape") {
 layerStyleRef="layer.";
 layerRef="document.layers";
 styleSwitch="";
 }else{
 layerStyleRef="layer.style.";
 layerRef="document.all";
 styleSwitch=".style";
 }
}
 
// SHOW MENU
function showLayer(layerName){
if (NS4 || IE4) {
 if (timeOn != null) {
 clearTimeout(timeOn)
 hideLayer(onLayer)
 }
 if (NS4 || IE4) {
 eval(layerRef+'["'+layerName+'"]'+styleSwitch+'.visibility="visible"');
 } 
 onLayer = layerName
 }
}// HIDE MENU
function hideLayer(layerName){
 if (menuActive == 0) {
 if (NS4 || IE4) {
 eval(layerRef+'["'+layerName+'"]'+styleSwitch+'.visibility="hidden"');
 }
 }
}// TIMER FOR BUTTON MOUSE OUT
function btnTimer() {
 timeOn = setTimeout("btnOut()",6000)
}// BUTTON MOUSE OUT
function btnOut(layerName) {
 if (menuActive == 0) {
 hideLayer(onLayer)
 }
}// MENU MOUSE OVER 
function menuOver(itemName) {
 clearTimeout(timeOn)
 menuActive = 1
}// MENU MOUSE OUT 
function menuOut(itemName) {
 menuActive = 0 
 timeOn = setTimeout("hideLayer(onLayer)", 400)
 }//
 
function hideAll() {
	if (NS4 || IE4) {
		eval(layerRef+'["Desk"]'+styleSwitch+'.visibility="hidden"');
		eval(layerRef+'["File"]'+styleSwitch+'.visibility="hidden"');
		eval(layerRef+'["View"]'+styleSwitch+'.visibility="hidden"');
		eval(layerRef+'["Options"]'+styleSwitch+'.visibility="hidden"');
	}
}
