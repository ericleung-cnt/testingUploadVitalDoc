var piaENG = "";
var piaTCHI = "";
var piaSCHI = "";

//piaStatement();
//
//var labelPiaEng = isc.Label.create(
//		{
//			ID: "labelPiaEng",
//			height: 30,
//			padding: 30,
//			align: "left",
//			valign: "center",
//			wrap: false,
//			//icon: "icons/16/approved.png",
//			showEdges: false,
//			contents: "<i>" + piaENG + "</i>",
//		});
//var labelPiaTChi = isc.Label.create(
//		{
//			ID: "labelPiaTchi",
//			height: 30,
//			padding: 30,
//			align: "left",
//			valign: "center",
//			wrap: false,
//			//icon: "icons/16/approved.png",
//			showEdges: false,
//			contents: "<i>" + piaTCHI + "</i> for release",
//		});			
//var labelPiaSChi = isc.Label.create(
//		{
//			ID: "labelPiaSchi",
//			height: 30,
//			padding: 30,
//			align: "left",
//			valign: "center",
//			wrap: false,
//			//icon: "icons/16/approved.png",
//			showEdges: false,
//			contents: "<i>" + piaSCHI + "</i> for release",
//		});			

var loginWin = isc.Window.create({
		ID: "loginWindow",
		width: 500,
		height:380,
//		autoSize: true,
		autoDraw: false,
		isModal: true,
		showModalMask: true,
		items:[isc.UpdateVLayout.create({
			ID: "loginVLayout",
//			height:50,
			groupTitle: "Login",
			isGroup: true,
			members: [
				isc.DynamicForm.create({
					ID : "loginDynamicForm",
					numCols: 2,
					saveOnEnter: true,
					autoFocus: true,
					fields:[
						{name: "isExternalUser", title: "External User", type: "boolean", colSpan: 2, defaultValue:true, canFocus: false},
						{name: "userId", title: "User Name", width: 140, required:true, defaultValue:"SYSTEM", length: 10 },
						{name: "password", title: "Password", width: 140, type: "password", required:true, defaultValue:"abcd1234"},
						{name: "errorMessage", type:"header",  value:"", defaultValue:"", colSpan:2, width:200}
						],
					autoDraw:false,
				}),

				isc.ButtonsHLayout.create({
					ID : "loginButtonHLayout",
					members : [
						isc.IButton.create({
							ID:"loginButton",
							title: "Log in",
						//icon:"",
							autoDraw:false,
							align:"center"
						})
					],
					autoDraw:false,
				}),
			
			],
		}),
		isc.DynamicForm.create({
			ID: "piaDynamicForm",
//			height:50,
			numCols: 1,
			cellPadding: 5,
			fields:[
				{name: "piaENG", title:"", type:"staticText", showTitle: false },
				{name: "piaTCHI", title:"", type:"staticText", showTitle: false },
				{name: "piaSCHI", title:"", type:"staticText", showTitle: false },					
			],
			autoDraw:false,
		}),
		],
		title: "Please Login",
		headerIconDefaults: {width:16, height: 16, src: "logo2.png"},
		autoCenter : true,
		showCloseButton : false,
		showMinimizeButton : false,
		show:function(){
			piaStatement();
			this.Super("show", arguments);
//			this.setHeight(300);
		
		}
	});


var loginData = new Object();
var userFunctionList;

function loginCallback(dsResponse, data, dsRequest){
	loginButton.setDisabled(false);
	var status = data.status;
	if(status == -8){ // Login successful
		loginWindow.hide();
		loginData = data;
		userFunctionList = data.userFuncList;
		isc.ViewLoader.create({
			width:"100%",
			height:"100%",
			ID:"defaultViewLoader",
			loadingMessage :"Loading ...&nbsp;${loadingImage}",
			autoDraw:true,
			viewURL:"default.js",
			viewLoaded:function(view){
				loginData.tabs.forEach(function(x){ 
					tab = JSON.parse(x);
					mainTabset.addTab({
						id:tab.id,
						title:tab.title,
//						icon:tab.icon,
//						iconSize:tab.iconSize,
						pane:isc.ViewLoader.create({
							width:"100%",
							height:"100%",
							loadingMessage :"Loading ...&nbsp;${loadingImage}",
						    viewURL:tab.viewURL,
						}),
					}); 
				});

//				var isPolling = isc.RPCResponse.POLLING;
//				if((loginGroupList !=undefined && loginGroupList!="") && ( isPolling === undefined || isPolling==0) ){
//					// no need to polling if user has no group;
//					isc.Log.logInfo("first polling....."+isc.RPCResponse.POLLING);
//					this.fetchData();
					
					//login popup for delegate user start
//            	    var requestArguments =  [null,null];
//            	    DMI.call({appID:"secsApp", 
//            			className:"userDelegateInfoDMI", 
//            			methodName:"findUserDelegateNum", 
//						arguments:requestArguments,  
//            			callback:function(rpcResponse, data, rpcRequest){
//            				delegateNum = 0;
//            				delegateNum = parseInt(data.toString(),10);
//            				if (delegateNum>0){
//            					delegateViewWindowCopy.show();
//            				}
//            				else {
//            				    delegateViewWindowCopy.hide();
//            				}		            				
//            			},
//						requestParams:null});
//
//            	    DMI.call({appID:"secsApp", 
//            			className:"userDelegateInfoDMI", 
//            			methodName:"getDelegateInfo", 
//						arguments:requestArguments,  
//            			callback:function(rpcResponse, data, rpcRequest){
//		            			if (delegateNum >0) {
//		            				roleResult = data.toString();            	
//		            				if (extract(roleResult,"<Role>","</Role>")!="") {
//		            					roleTextArea.setValue(extract(roleResult,"<Role>","</Role>"));
//		            				}
//		            				if (extract(roleResult,"<Group>","</Group>")!="") {
//		            					groupTextArea.setValue(extract(roleResult,"<Group>","</Group>"));
//		            				}		      
//		            			}
//            			},
//						requestParams:null});
//            	    
//					//login popup for delegate user end							
//				}else{
//					isc.Log.logInfo("client is polling....."+isc.RPCResponse.POLLING);
//				}
//				if (checkDefaultPassword){
//					isc.say("Please change password.<br>All functions are disabled until password is changed.")
//				}
//			},
//			fetchData:function(){
//				isc.RPCResponse.POLLING=1;
//				applicationInboxDS.fetchData({}, function(dsResponse, data, dsRequest){
//					if(isc.RPCResponse.STATUS_SUCCESS == dsResponse.status){
//						if(data!=null && data.getLength()>0){
////							inboxSearchResultTabSet.fetchHeaderCount();
//							inboxSearchResultTabSet.updateByServerPush(data);
//							applicationInboxDS.updateCaches({data:data, operationType: "update"});
//							popupApplicationWindow.setData(data);
//						}
//						defaultViewLoader.fetchData();
//					}else if(-101 == dsResponse.status){
//						isc.Log.logInfo("STATUS_LOGIN_REQUIRED.....");
//						isc.RPCResponse.POLLING=0;
//					}
//				}, {operationId : "POLLING_APPLICATION", showPrompt:false, timeout:1720000, willHandleError:true});
			},
		});
		
	} else { // Login failed
		loginWindow.show();
		loginDynamicForm.setValue('errorMessage', data.message);
//		if (status == -2) {
			//loginDynamicForm.addFieldErrors("password", data.message, true);
//		} else {
			//loginDynamicForm.addFieldErrors("userId", data.message, true);
//		}
	};
}

function piaStatement(){
	DMI.call({
		appID:"fsqcApp", 
		className:"securityDMI", 
		methodName:"getPIA", 
		callback: function(dsResponse, data, dsRequest){
			piaDynamicForm.getField("piaENG").setValue(data.piaENG);
			piaDynamicForm.getField("piaTCHI").setValue(data.piaTCHI);
			piaDynamicForm.getField("piaSCHI").setValue(data.piaSCHI);
		}
	});
}

function login(){
	loginDynamicForm.setValue('errorMessage', '');
	if(loginDynamicForm.validate()){
		loginButton.setDisabled(true);
		DMI.call("fsqcApp", "securityDMI", "login", 
				loginDynamicForm.getValue("userId"), 
				loginDynamicForm.getValue("password"), 
				loginDynamicForm.getValue("isExternalUser"),
				loginCallback);
	}
}

loginDynamicForm.submit = login;
loginButton.click = login;
