var SAAgent = null;
var SASocket = null;
var INCHANNELID = 234;
var OUTCHANNELID = 123;
var ProviderAppName = "Yoo";


( function () {
	window.addEventListener( 'tizenhwkey', function( ev ) {
		if( ev.keyName == "back" ) {
			var page = document.getElementsByClassName( 'ui-page-active' )[0],
				pageid = page ? page.id : "";
			if( pageid === "main" ) {
				try {
					//if(SASocket != null){SASocket.close(); SASocket = null;}
					tizen.application.getCurrentApplication().exit();
				} catch (ignore) {
				}
			} else {
				window.history.back();
			}
		}
	} );
} () );



function createList(channelId, string){
	console.log("YOOOOO");
	var list = document.getElementById("list");
	//todo: delete all items in list
	var li = document.createElement("li");
	var a = document.createElement("a");
	
	a.href = "#";
	li.appendChild(a);
	a.appendChild(document.createTextNode(string));
	li.onclick = "sendYo()";
	list.appendChild(li);
}

function onerror(err){
	alert("ONERROR: err "+err.name+" msg "+err.message);
}

function ondevicestatus(type, status){
   if (status == "ATTACHED"){
      alert("Attached remote peer device. : " + type);
      SAAgent.findPeerAgents();
   }else if (status == "DETACHED"){
	  //SASocket.close();
      alert("Detached remote peer device. : " + type);
   }
}

var connectionCallback = {
   /* Remote peer agent requests a service connection */
   onrequest: function(peerAgent){
	  alert("connectionCallback.onrequest");
      if (peerAgent.appName == "Yoo"){
         SAAgent.acceptServiceConnectionRequest(peerAgent);
      }else{
    	 alert("connectionCallback"+peerAgent.appName);
         SAAgent.rejectServiceConnectionRequest(peerAgent);
      }
   },
   /* Connection between provider and consumer is established */
   onconnect: function(socket){
	   alert("connectionCallback.onconnect...aight dawg");
	   alert(socket.peerAgent.appName);
//	   if (socket.peerAgent.appName != "Yoo"){
//		   alert("closing at 1");
//		   socket.close();
//	   }else {
	  alert("app name is correct");
	  SASocket = socket;
	  SASocket.setDataReceiveListener();
	  SASocket.setSocketStatusListener(function(reason){
		  alert("Service Connction lost, Reason: "+reason);
		  
		  try {
			  if (SASocket != null) {
    			  SASocket.close();
    			  SASocket = null;
			  }
		  } catch(err) {
			  console.log("exception [" + err.name + "] msg[" + err.message + "]");
		  }
		  
	  });
//      }
   }
};

function onpeeragentfound(peerAgent){
	alert("EnteringOnPeerAgentFound");
   if (peerAgent.appName == "Yoo") {
	  alert("correct name of 'Yoo'");
      SAAgent.requestServiceConnection(peerAgent);
   }
   else{
	   alert("onpeeragentfound: "+peerAgent.appName);
   }
}

function onpeeragentupdated(peerAgent, status){
   if (status == "AVAILABLE") {
      SAAgent.requestServiceConnection(peerAgent);
   }
   else if (status == "UNAVAILABLE") {
      alert("Uninstalled application package of peerAgent on remote device.");
   }
}

var peerAgentFindCallback = {
   onpeeragentfound: onpeeragentfound,
   onpeeragentupdated: onpeeragentupdated
};

function onSuccess(agents) {
   SAAgent = agents[0];
   
   SAAgent.setServiceConnectionListener(connectionCallback);
   SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
   webapis.sa.setDeviceStatusListener(ondevicestatus);
   SAAgent.findPeerAgents();
   
//   for (var i = 0; i < agents.length; i++){
//      alert(i + ". " + agents[i].name);
//      /* Process the SA agents */
//   }
}

function requestAgent(){
	if(SASocket){return false;}
	try{
		alert("Requesting Agent");
		webapis.sa.requestSAAgent(onSuccess, onerror);
	}
	catch(err){
		alert("failed in requestAgent");
	}
	
}

function sendYo(){
	alert("sending yo...");
	var username = this.innerHTML;
	try{
		SASocket.sendData(OUTCHANNELID, username);
	}catch(err){
		alert("Failed in sendYo");
	}
}
