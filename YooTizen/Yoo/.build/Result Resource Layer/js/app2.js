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


function onerror(err){
	alert("ONERROR: err "+err.name+" msg "+err.message);
}

function disconnect() {
	try {
		if (SASocket !== null) {
			SASocket.close();
			SASocket = null;
			//TODO: Render
		}
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function onsuccess(agents) {
	 try {
		 if (agents.length > 0) {
			 SAAgent = agents[0];
			
			 SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			 SAAgent.findPeerAgents();
		 } else {
			 alert("Not found SAAgent!!");
		 }
	 } catch(err) {
		 console.log("exception [" + err.name + "] msg[" + err.message + "]");
	 }
}

function connect() {
	 if (SASocket) {
		 alert('Already connected!');
	 	return false;
	 }try {
		 webapis.sa.requestSAAgent(onsuccess, function (err) {
			 console.log("err [" + err.name + "] msg[" + err.message + "]");
		 });
	 } catch(err) {
		 console.log("exception [" + err.name + "] msg[" + err.message + "]");
	 }
}

var agentCallback = {
 onconnect : function(socket) {
	 SASocket = socket;
	 alert("HelloAccessory Connection established with RemotePeer");
	 //TODO: Render
	 SASocket.setSocketStatusListener(function(reason){
		 console.log("Service connection lost, Reason : [" + reason + "]");
		 disconnect();
	 });
 },
 onerror : onerror
};

var peerAgentFindCallback = {
	 onpeeragentfound : function(peerAgent) {
		 try {
			 if (peerAgent.appName === ProviderAppName) {
				 SAAgent.setServiceConnectionListener(agentCallback);
				 SAAgent.requestServiceConnection(peerAgent);
			 } else {
				 alert("Not expected app!! : " + peerAgent.appName);
			 }
		 } catch(err) {
			 console.log("exception [" + err.name + "] msg[" + err.message + "]");
		 }
	 },
	 onerror : onerror
};

function createList(channelId, string){
	console.log("YOOOOO");
	var list = document.getElementById("list");
	//todo: delete all items in list
	var li = document.createElement("li");
	var a = document.createElement("a");
	
	a.href = "#";
	li.appendChild(a);
	a.appendChild(document.createTextNode(string));
	li.onclick = "sendYo(this.innerHTML)";
	list.appendChild(li);
}

function sendYo(name){
	alert("sending yo to ..."+name);
	var username = name;
	try{
		SASocket.sendData(OUTCHANNELID, username);
	}catch(err){
		alert("Failed in sendYo");
	}
}

function fetch() {
	try {
		SASocket.setDataReceiveListener(createList);
		SASocket.sendData(OUTCHANNELID, "Hello Accessory!"); //TODO
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

