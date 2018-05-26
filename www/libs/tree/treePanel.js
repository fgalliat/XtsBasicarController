
/*
    _______________________            ______________________
    XML DOM Tree Component             Browsers support:
             Version 1.5                -> Internet Explorer
                                        -> Mozilla
    _____________________________       -> Opera
    Features:                           -> Firefox
     -> Server Side Independency        -> Konqueror
     -> Cross Browser Support
     -> Dynamic Loading
     -> XML Source
     -> Easy Customization





                              ______________________________
                                  Serghei Egoricev (c) 2006
                                    egoricev [at] gmail.com
*/

// CSS import

Tree = function() {}
/*
	Use double click for navigate, single click for expand
*/
Tree.useDblClicks = true; // NOT IMPLEMENTED
Tree.saveNodesStateInCookies = true;
/*
	CSS classes
*/
Tree.expandedClassName = "";
Tree.collapsedClassName = "collapsed";
Tree.selectedClassName = "selected";
Tree.plusMinusClassName = "plusminus";
Tree.treeClass = "tree";

/*
	Images
*/
Tree.collapsedImage = "./libs/tree/img/collapsed.gif";
Tree.expandedImage = "./libs/tree/img/expanded.gif";
Tree.noChildrenImage = "./libs/tree/img/treenochild.gif";

/*
	Xml Attributes
*/
Tree.xmlCaption = "caption";
Tree.xmlUrl = "url";
Tree.xmlTarget = "target";
Tree.xmlRetreiveUrl = "retreiveUrl";
Tree.xmlIcon = "icon";
Tree.xmlExpanded = "expanded";

/*
	Text for loading
*/
Tree.loadingText = "Loading ...";

/*
	Private members
*/
Tree.obj = null;
Tree.instanceCount = 0;
Tree.instancePrefix = "alder";
Tree.cookiePrefix = "alder";
Tree.dwnldQueue = new Array;
Tree.dwnldCheckTimeout = 100;

/*
	Interval handler. Ckecks for new nodes loaded.
	Adds loaded nodes to the tree.
*/
Tree.checkLoad = function ()
{
	var i, httpReq;
	for (i = 0; i<Tree.dwnldQueue.length; i++)
		if ((httpReq = Tree.dwnldQueue[i][0]).readyState == 4 /*COMPLETED*/)
		{
			var node = Tree.dwnldQueue[i][1];
			// unqueue loaded item
			Tree.dwnldQueue.splice(i, 1);
			Tree.appendLoadedNode(httpReq, node);
			if (Tree.saveNodesStateInCookies)
				Tree.openAllSaved(Tree.getId(node));
		} // if
	// will call next time, not all nodes were loaded
	if (Tree.dwnldQueue.length != 0)
		window.setTimeout(Tree.checkLoad, Tree.dwnldCheckTimeout);
}

/*
	Adds loaded node to tree.
*/
Tree.appendLoadedNode = function (httpReq, node)
{
	// create DomDocument from loaded text
	var xmlDoc = Tree.loadXml(httpReq.responseText);
	// create tree nodes from xml loaded
	var newNode = Tree.convertXml2NodeList(xmlDoc.documentElement);
	// Add loading error handling here must be added
	Tree.appendNode(node, newNode);
}

/*
	Event handler when node is clicked.
	Navigates node link, and makes node selected.
*/
Tree.NodeClick = function (event)
{
	var node = event.srcElement /*IE*/ || event.target /*DOM*/;
	// <li><a><img> - <img> is capturing the event
	while (node.tagName != "A")
		node = node.parentNode;
	node.blur();
	node = node.parentNode;
	Tree.obj = Tree.getObj(node);
	Tree.expandNode(node);
	Tree.selectNode(node);
}

/*
	Event handler when plus/minus icon is clicked.
	Desides whenever node should be expanded or collapsed.
*/
Tree.ExpandCollapseNode = function (event)
{
	var anchorClicked = event.srcElement /*IE*/ || event.target /*DOM*/;
	// <li><a><img> - <img> is capturing the event
	while (anchorClicked.tagName != "A")
		anchorClicked	= anchorClicked.parentNode;
	anchorClicked.blur();
	var node = anchorClicked.parentNode;
	// node has no children, and cannot be expanded or collapsed
	if (node.empty)
		return;
	Tree.obj = Tree.getObj(node);
	if (Tree.isNodeCollapsed(node))
		Tree.expandNode(node);
	else
		Tree.collapseNode(node);
	// cancelling the event to prevent navigation.
	if (event.preventDefault == undefined)
	{ // IE
		event.cancelBubble = true;
		event.returnValue = false;
	} // if
	else
	{ // DOM
		event.preventDefault();
		event.cancelBubble = true;
	} // else
}

/*
	Determines if specified node is selected.
*/
Tree.isNodeSelected = function (node)
{
	return (node.isSelected == true) || (Tree.obj.selectedNode == node);
}

/*
	Determines if specified node is expanded.
*/
Tree.isNodeExpanded = function (node)
{
	return (Tree.expandedClassName == node.className) || (node.expanded == true);
}

/*
	Determines if specified node is collapsed.
*/
Tree.isNodeCollapsed = function (node)
{
	return (Tree.collapsedClassName == node.className) || (node.collapsed == true);
}

/*
	Determines if node currently selected is at same
	level as node specified (has same root).
*/
Tree.isSelectedNodeAtSameLevel = function (node)
{
	if (Tree.obj.selectedNode == null) // no node currently selected
		return false;
	var i, currentNode, children = node.parentNode.childNodes; // all nodes at same level (li->ul->childNodes)
	for (i = 0; i < children.length; i++)
		if ((currentNode = children[i]) != node && Tree.isNodeSelected(currentNode))
			return true;
	return false;
}

/*
	Mark node as selected and unmark prevoiusly selected.
	Node is marked with attribute and <a> is marked with css style
	to avoid mark <li> twise with css style expanded and selected.
*/
Tree.selectNode = function (node)
{
	if (Tree.isNodeSelected(node)) // already marked
		return;
	if (Tree.obj.selectedNode != null)
	{// unmark previously selected node.
		Tree.obj.selectedNode.isSelected = false;
		// remove css style from anchor
		Tree.getNodeAnchor(Tree.obj.selectedNode).className = "";
	} // if
	// collapse selected node if at same level
	if (Tree.isSelectedNodeAtSameLevel(node))
		Tree.collapseNode(Tree.obj.selectedNode);
	// mark node as selected
	Tree.obj.selectedNode = node;
	node.isSelected = true;
	Tree.getNodeAnchor(node).className = Tree.selectedClassName;
}

/*
	Expand collapsed node. Loads children nodes if needed.
*/
Tree.expandNode = function (node, avoidSaving)
{
	if (node.empty)
		return;
	Tree.getNodeImage(node).src = Tree.expandedImage;
	node.className = Tree.expandedClassName;
	node.expanded = true;
	node.collapsed = false;
	if (Tree.areChildrenNotLoaded(node))
		Tree.loadChildren(node);
	if (Tree.saveNodesStateInCookies && !avoidSaving)
		Tree.saveOpenedNode(node);
}

/*
	Collapse expanded node.
*/
Tree.collapseNode = function (node, avoidSaving)
{
	if (node.empty)
		return;
	Tree.getNodeImage(node).src = Tree.collapsedImage;
	node.className = Tree.collapsedClassName;
	node.collapsed = true;
	node.expanded = false;
	if (Tree.saveNodesStateInCookies && !avoidSaving)
		Tree.saveClosedNode(node);
}

/*
	Returns plus/minus <img> for node specified.
*/
Tree.getNodeImage = function (node)
{
	return node.getElementsByTagName("IMG")[0];
}

/*
	Returns retreiveUrl for node specified.
*/
Tree.getNodeRetreiveUrl = function (node)
{
	return node.getElementsByTagName("A")[0].href;
}

/*
	Returns node link <a> element (<li><a><img></a><a>)
*/
Tree.getNodeAnchor = function (node)
{
	return node.getElementsByTagName("A")[1];
}

/*
	Cancel loading children nodes.
*/
Tree.CancelLoad = function (event)
{ 
	var i, node = event.srcElement /*IE*/ || event.target /*DOM*/;
	while (node.tagName != "LI")
		node = node.parentNode;
	// search node in queue
	for (i = 0; i<Tree.dwnldQueue.length; i++)
		if (Tree.dwnldQueue[i][1] == node)
		{
			// remove from queue
			Tree.dwnldQueue.splice(i, 1);
			// collapse node
			Tree.collapseNode(node);
		} // if
}

/*
	Loads text from url specified and returns it as result.
*/
Tree.loadUrl = function (url, async)
{
	// create request object
	var httpReq = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
	// prepare request
	httpReq.open("GET" /* method */, url /* url */, async == true /* async */, null /* login */, null /* password */);
	// send request
	httpReq.send(null);
	return async == true? httpReq : httpReq.responseText;
}

/*
	Creates XmlDom document from xml text string.
*/
Tree.loadXml = function (xmlString)
{
	var xmlDoc;
  if (window.DOMParser) /*Mozilla*/
    xmlDoc = new DOMParser().parseFromString(xmlString, "text/xml");
  else
  {
  	if (document.implementation && document.implementation.createDocument)
	  	xmlDoc = document.implementation.createDocument("","", null); /*Konqueror*/
		else
	    xmlDoc = new ActiveXObject("Microsoft.XmlDom"); /*IE*/
	  
  	xmlDoc.async = false;
  	xmlDoc.loadXML(xmlString);
  } // else
  return xmlDoc;
}

/*
	Determines if children are loaded for node specified.
*/
Tree.areChildrenNotLoaded = function (node)
{
	return Tree.getNodeSpan(node) != null;
}

/*
	Finds loading span for node.
*/
Tree.getNodeSpan = function (node)
{
	var span = node.getElementsByTagName("SPAN");
	return (span.length > 0 && (span = span[0]).parentNode == node) ? span : null;
}

/*
	Enqueue load of children nodes for node specified.
*/
Tree.loadChildren = function (node)
{
	// get url with children
	var url = Tree.getNodeRetreiveUrl(node);
	// retreive xml text from url
	var httpReq = Tree.loadUrl(url, true);
	// enqueue node loading
	if (Tree.dwnldQueue.push(new Array (httpReq, node)) == 1)
		window.setTimeout(Tree.checkLoad, Tree.dwnldCheckTimeout);
}

/*
	Creates HTML nodes list from XML nodes.
*/
Tree.convertXml2NodeList = function (xmlElement)
{
	var ul = document.createElement("UL");
	var i, node, children = xmlElement.childNodes;
	var index = 0;
	for (i = 0; i<children.length; i++)
		if ((node = children[i]).nodeType == 1 /* ELEMENT_NODE */)
			ul.appendChild(Tree.convertXml2Node(node)).nodeIndex = index++;
	return ul;
}

/*
	Adds event handler
*/
Tree.addEvent = function (obj, fn, ev)
{
	if (ev == undefined) ev = "click"; // defaulting event to onclick
	if (obj.addEventListener)
		obj.addEventListener(ev, fn, false);
	else
		if (obj.attachEvent)
			obj.attachEvent("on"+ev, fn);
		else
			obj.onclick = fn;
}

/*
	Determines if xml node has child nodes inside.
*/
Tree.hasXmlNodeChildren = function (xmlElement)
{
	var i, children = xmlElement.childNodes;
	for (i = 0; i<children.length; i++)
		if ((node = children[i]).nodeType == 1 /* ELEMENT_NODE */)
			return true;
	return false;
}

/*
	Appends newly created node to node specified.
	Simply replace loading <span> at new node.
*/
Tree.appendNode = function (node, newNode)
{
	node.replaceChild(newNode, Tree.getNodeSpan(node));
}

/*
	Creates tree object. Loads it content from url specified.
*/
Tree.prototype.Create = function (url, obj)
{
	var div = document.createElement("DIV");
	div.id = Tree.instancePrefix + Tree.instanceCount++;
	div.className = Tree.treeClass;
	var xml = Tree.loadUrl(url, false);
	var xmlDoc = Tree.loadXml(xml);
	var newNode = Tree.convertXml2NodeList(xmlDoc.documentElement);
	div.appendChild(newNode);
	if (obj != undefined)
	{
		if (obj.appendChild) // is node
			obj.appendChild(div);
		else if (document.getElementById(obj)) // is node id
			document.getElementById(obj).appendChild(div);
	} // if
	else
		document.body.appendChild(div);
	if (Tree.saveNodesStateInCookies)
		Tree.openAllSaved(div.id);
}

/*
	Creates HTML tree node (<li>) from xml element.
*/
Tree.convertXml2Node = function (xmlElement)
{
	var li = document.createElement("LI");
	var a1 = document.createElement("A");
	var a2 = document.createElement("A");
	var i1 = document.createElement("IMG");
	var i2 = document.createElement("IMG");
	var hasChildNodes = Tree.hasXmlNodeChildren(xmlElement);
	var retreiveUrl = xmlElement.getAttribute(Tree.xmlRetreiveUrl);
	
	// plus/minus icon
	i1.className = Tree.plusMinusClassName;
	a1.appendChild(i1);
	Tree.addEvent(a1, Tree.ExpandCollapseNode);
	
	// plus/minus link
	a1.href = retreiveUrl != null && retreiveUrl.length != 0 ? retreiveUrl : "about:blank";
	li.appendChild(a1);
	
	// node icon
	i2.src = xmlElement.getAttribute(Tree.xmlIcon);
	a2.appendChild(i2);
	
	// node link
	a2.href = xmlElement.getAttribute(Tree.xmlUrl);
	a2.target = xmlElement.getAttribute(Tree.xmlTarget);
	a2.title = xmlElement.getAttribute(Tree.xmlCaption);
	a2.appendChild(document.createTextNode(xmlElement.getAttribute(Tree.xmlCaption)));
	Tree.addEvent(a2, Tree.NodeClick);

	li.appendChild(a2);
	
	// loading span
	if (!hasChildNodes && retreiveUrl != null && retreiveUrl.length != 0)
	{
		var span = document.createElement("SPAN");
		span.innerHTML = Tree.loadingText;
		Tree.addEvent(span, Tree.CancelLoad);
		li.appendChild(span);
	} // if
	
	// add children
	if (hasChildNodes)
		li.appendChild(Tree.convertXml2NodeList(xmlElement));
	if (hasChildNodes || retreiveUrl != null && retreiveUrl.length != 0)
	{
		if (xmlElement.getAttribute(Tree.xmlExpanded))
			Tree.expandNode(li, true);
		else
			Tree.collapseNode(li, true);
	} // if
	else
	{
		i1.src = Tree.noChildrenImage; // no children
		li.empty = true;
	} // else

	return li;
}

/*
	Retreives current tree object.
*/
Tree.getObj = function (node)
{
	var obj = node;
	while (obj != null && obj.tagName != "DIV")
		obj = obj.parentNode;
	return obj;
}

Tree.getId = function (node)
{
	var obj = Tree.getObj(node);
	if (obj)
		return obj.id;
	return "";
}

/*
	Retreives unique id for tree node.
*/
Tree.getNodeId = function (node)
{
	var id = "";
	var obj = node;
	while (obj != null && obj.tagName != "DIV")
	{
		if (obj.tagName == "LI" && obj.nodeIndex != null)
			id = "_" + obj.nodeIndex + id;
		obj = obj.parentNode;
	} // while
//	if (obj != null && obj.tagName == "DIV")
//		id = obj.id + "_" + id;
	return id;
}

/*
	Saves node as opened for reload.
*/
Tree.saveOpenedNode = function (node)
{
	var treeId = Tree.getId(node);
	var state = Tree.getAllNodesSavedState(treeId);
	var nodeState = Tree.getNodeId(node) + ",";
	if (state.indexOf(nodeState) == -1)
	{
		state += nodeState;
		Tree.setAllNodesSavedState(treeId, state);
	} // if
}

/*
	Saves node as closed for reload.
*/
Tree.saveClosedNode = function (node)
{
	var treeId = Tree.getId(node);
	var state = Tree.getAllNodesSavedState(treeId);
	state = state.replace(new RegExp(Tree.getNodeId(node) + ",", "g"), "");
	Tree.setAllNodesSavedState(treeId, state);
}

Tree.getAllNodesSavedState = function (treeId)
{
	var state = Tree.getCookie(Tree.cookiePrefix + "_" + treeId);
	return state == null ? "" : state;
}

Tree.setAllNodesSavedState = function (treeId, state)
{
	Tree.setCookie(Tree.cookiePrefix + "_" + treeId, state);
}

/*
	Enques list of all opened nodes
*/
Tree.openAllSaved = function(treeId)
{
	var nodes = Tree.getAllNodesSavedState(treeId).split(",");
	var i;
	for (i=0; i<nodes.length; i++)
	{
		var node = Tree.getNodeById(treeId, nodes[i]);
		if (node && Tree.isNodeCollapsed(node))
			Tree.expandNode(node);
	} // for
}

Tree.getNodeById = function(treeId, nodeId)
{
	var node = document.getElementById(treeId);
	if (!node)
		return null;
	var path = nodeId.split("_");
	var i;
	for (i=1; i<path.length; i++)
	{
		if (node != null)
		{
			node = node.firstChild;
			while (node != null && node.tagName != "UL")
				node = node.nextSibling;
		} // if
		if (node != null)
			node = node.childNodes[path[i]];
		else
			break;
	} // for
	return node;
}

Tree.setCookie = function(sName, sValue)
{
  document.cookie = sName + "=" + escape(sValue) + ";";
}

Tree.getCookie = function(sName)
{
  var a = document.cookie.split("; ");
  for (var i=0; i < a.length; i++)
  {
    var aa = a[i].split("=");
    if (sName == aa[0]) 
      return unescape(aa[1]);
  } // for
  return null;
}

