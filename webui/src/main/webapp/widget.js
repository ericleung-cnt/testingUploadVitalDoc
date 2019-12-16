Date.setShortDatetimeDisplayFormat("dd/MM/yyyy");
Date.setNormalDatetimeDisplayFormat("dd/MM/yyyy HH:mm");

isc.DataSource.create({
    ID: "defaultBigBooleanDS",
    fields:[
        {name:"display"},
        {name:"value"}
    ],
    clientOnly: true,
    testData: [
        	{display: "YES", value: true},
        	{display: "NO", value: false}
        ]
});

var partTypeValueMap = {
	1 : "Part I",
	2 : "Part II"
	};

isc.DateRangeItem.addProperties({
	inputFormat:"DMY",
	format:"dd/MM/yyyy",
	displayFormat:"dd/MM/yyyy",
	dateDisplayFormat:"dd/MM/yyyy",
	dateFormatter:"dd/MM/yyyy",
});
isc.MiniDateRangeItem.addProperties({
	format:"dd/MM/yyyy",
	displayFormat:"dd/MM/yyyy",
	dateDisplayFormat:"dd/MM/yyyy",
	dateFormatter:"dd/MM/yyyy",
});
isc.DateRangeDialog.addProperties({
	accept:function(){
		console.log("custom accept");
		if (this.autoValidate &&
				!this.rangeItem.validateRange()) {
			return
		}
		this.finished(this.rangeItem.returnCriterion ?
				this.rangeItem.getCriterion() :
					this.rangeItem.getValue());
	}
});

isc.ClassFactory.defineClass("IAddButton", IButton);
isc.IAddButton.addProperties({
	title: "New",
	width: 80,
	hiliteAccessKey:true,
	accessKey:"N",
	icon: "add.png"
});
isc.ClassFactory.defineClass("ISaveButton", IButton);
isc.ISaveButton.addProperties({
	title: "<u>S</u>ave",
	width: 80,
	icon: "save.png"
});
isc.ClassFactory.defineClass("IResetButton", IButton);
isc.IResetButton.addProperties({
	title: "Reset",
	width: 80,
	hiliteAccessKey:true,
	accessKey:"R",
	icon: "undo.png"
});
isc.ClassFactory.defineClass("IExportButton", IButton);
isc.IExportButton.addProperties({
	title: "Export",
	width: 80,
	hiliteAccessKey:true,
	accessKey:"O",
	icon: "demand.png",
	listGrid:null,
	click:function(){
		var listGrid = this.listGrid;
		if(listGrid!=null){
			if(listGrid.getTotalRows()>0){
				var button = this;
				this.setDisabled(true);
				setTimeout(function(){
					button.setDisabled(false);
				}, 5000);
				isExport = true;
				var dataSourceID = listGrid.dataSource;
				if (typeof dataSourceID != "string") {
					if (typeof dataSourceID.ID == "string") {
						dataSourceID = dataSourceID.ID;
					}
				}
				var fileName = dataSourceID.substring(0, dataSourceID.length-2)+"_results.xls";
				if (listGrid.isA('FilterListGrid')){
					listGrid.exportData({ ignoreTimeout:true,  exportResults:true, exportAs:"xls", exportFormat:"xls", exportFilename:fileName, operationId:"exportData"});
				} else {
					if (listGrid.getTotalRows() > 65535){
						listGrid.exportData({ignoreTimeout:true, "endRow":-1, exportAs:"ooxml", exportFilename:fileName, operationId:"exportData"});
					} else {
						listGrid.exportData({ignoreTimeout:true, "endRow":-1, exportAs:"xls", exportFilename:fileName, operationId:"exportData"});
					}
				}
				setTimeout(function(){
					isExport = false;
				}, 5000);
			}else{
				isc.warn(pleaseFetchDataMessage);
			}
		}
	}
});
isc.ClassFactory.defineClass("UpdateVLayout", VLayout);
isc.UpdateVLayout.addProperties({
	height:60,
	layoutTopMargin:10,
	layoutBottomMargin:10
});

isc.ClassFactory.defineClass("ButtonsHLayout", HLayout);
isc.ButtonsHLayout.addProperties({
	width:"100%",
	height:10,
	align:"right",
	layoutMargin:5
});
isc.ClassFactory.defineClass("FilterListGrid", ListGrid);
isc.FilterListGrid.addProperties({
	showFilterEditor:true,
	filterOnKeypress:true,
	dataFetchMode: "basic",
	fetchDelay:500,
	filterButtonProperties:{click:function(){
		var listGrid = this.getParentElements().get(1);
		var sectionStack = listGrid.getParentElements()[0].getParentElements()[0];
		sectionStack.getSectionHeader(1).setTitle(newTitle);
		var form = sectionStack.sections.get(1).items[0].members[0];
		form.setValues([]);
		listGrid.setData([]);
		listGrid.filterData([], function(dsResponse, data, dsRequest){
			sectionStack.getSectionHeader(0).setTitle("Maintenance("+dsResponse.totalRows+")");
		});
		}
	},
	filterData:function(criteria, callback, requestProperties){
		var listGrid = this;
		var sectionStack = this.getParentElements()[0].getParentElements()[0];
		if(callback == null){
			this.Super("filterData", [criteria, function(dsResponse, data, dsRequest){
				sectionStack.getSectionHeader(0).setTitle("Maintenance("+dsResponse.totalRows+")");
			}, requestProperties]);
			var listgrid = this;
			setTimeout(function(){
				var data = listgrid.data.localData;
				if (data == null){
					data = listgrid.data;
				}
				sectionStack.getSectionHeader(0).setTitle("Maintenance("+data.length+")");
			}, 100);
		}else{
			this.Super("filterData", arguments);
		}
	}
});


isc.Window.create({
	ID:"ReportViewWindow",
	width: 1024,
	height: 768,
	autoDraw: false,
	isModal:true,
	title: "View Report",
	items:[
	       isc.HTMLFlow.create({
	    	   ID:"ReportHTMLPane",
	    	   showEdges:true,
	    	   width: "100%",
	    	   height: "100%",
	    	   hideUsingDisplayNone:true,
	    	   contentsType: "page"
	       })
		],
	showReport:function(url){
		ReportHTMLPane.contents = "<div class='holds-the-iframe'><iframe width='100%' height='725px' border='0' frameborder='0' src='"+url+"'></iframe></div>";
		ReportHTMLPane.markForRedraw();
		this.show();
		this.hide();
	},
	downloadReport:function(url){
//		ReportHTMLPane.contents = "<div class='holds-the-iframe'><iframe width='100%' height='725px' border='0' frameborder='0' src='"+url+"'></iframe></div>";
//		ReportHTMLPane.markForRedraw();
//		this.show();
		//this.hide();
	},
	displayReport:function(requestArguments){
		var requestParameters = {downloadResult:false, downloadToNewWindow:false };
		var requestParameters = {};
		 DMI.call({appID:"ssrsApp",
				className:"reportDMI",
				methodName:"generate",
				arguments:requestArguments,
				callback:function(rpcResponse, data, rpcRequest){
					window.open(data, "_blank",
								"toolbar=yes,scrollbars=yes,resizable=yes,top=60,left=60,width=1024,height=768");
				},
				requestParams:requestParameters
			});
	}
});

isc.ClassFactory.defineClass("TitleLabel", Label);
isc.TitleLabel.addProperties({width: "75%", height: 20, align: "left", valign: "top", wrap: false});

isc.ClassFactory.defineClass("WindowVLayout", VLayout);
isc.WindowVLayout.addProperties({layoutMargin:10, membersMargin:10});
isc.ClassFactory.defineClass("SectionVLayout", VLayout);
isc.SectionVLayout.addProperties({layoutMargin:5, membersMargin:5});

//isc.Layout.addProperties({width:"100%",	height:"100%"});
isc.HLayout.addProperties({width:"100%",	height:"100%"});
isc.Window.addProperties({canDragResize: true,autoCenter: true});
isc.Toolbar.addProperties({buttonConstructor: "IButton", height: 26});
isc.ListGrid.addProperties({alternateRecordStyles:true, selectionType:"single"});
/*


 */

isc.ClassFactory.defineClass("ReportDynamicForm", DynamicForm);
isc.ReportDynamicForm.addProperties({
		margin:10 //, requiredTitlePrefix: "*"
	});

isc.ClassFactory.defineClass("ReportToolbar", Toolbar);
isc.ReportToolbar.addProperties({
	margin:10, height: 46, buttonConstructor: "IButton"
});

isc.ClassFactory.defineClass("ReportSectionStack", SectionStack);
isc.ReportSectionStack.addProperties({
	visibilityMode: "multiple"
});


isc.ClassFactory.defineClass("ButtonToolbar", Toolbar);
isc.ButtonToolbar.addProperties({align:"right", valign:"bottom", membersMargin: 10,	height:30,	layoutRightMargin: 25, height: 30, align:"right", buttonConstructor: "IButton", createButtonsOnInit:true});
isc.IButton.addProperties({
	onControl:undefined,
 	draw:function(){
		this.Super("draw", arguments);
		this.setDisabled(false);//only for trigger.
//		if(this.onControl!=undefined){
//			var onControlArray = this.onControl.split("|");
//			for(var i = 0; i < onControlArray.length; i++) {
//				var isOn = userFunctionList.contains(onControlArray[i]);
//				this.setDisabled(!isOn);
//				if(isOn==true){
//					break;
//				}
//			}
//		}
	},
	setDisabled:function(){
		this.Super("setDisabled", arguments);

		if(this.onControl!=undefined && arguments[0]==false){
			var onControlArray = this.onControl.split("|");
			for(var i = 0; i < onControlArray.length; i++) {
				var isOn = userFunctionList.contains(onControlArray[i]);
				this.Super("setDisabled", !isOn);
				if(isOn==true){
					break;
				}
			}
		}
	}
});

isc.SectionStack.addProperties({visibilityMode: "multiple",	width: "100%",	animateSections: true, layoutMargin: -1, layoutStartMargin: 20,	membersMargin: 10, overflow : "hidden"});
isc.Canvas.addProperties({autoDraw : false});
isc.DynamicForm.addProperties({
	wrapItemTitles:false,
	requiredTitlePrefix: "<font color='red'><b>",
	requiredTitleSuffix: " </b></font>",
	requiredRightTitlePrefix: "<font color='red'><b>",
	requiredRightTitleSuffix: " </b></font>",
	titleSuffix: " ",
	rightTitleSuffix: " ",
	layoutMargin:10
});
isc.DynamicForm.create = function isc_c_Class_create(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13) {
    var form = this.createRaw();
    if (form != null) {
    	form = form.completeCreation(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13)
    }
    var nextItem = function(item,form,keyName){
    	if (!(item.length && item.length > 255) &&
    			["Enter"].contains(keyName) && "textArea" != item.type &&
    			(["text", "radioGroup", "date", "select"].contains(item.type) || item.textBoxStyle)) {
    		var index = form.items.indexOf(item);
    		var focusSet = false;
			for (var i = 1; form.items.length > index + i ; i++) {
				if (form.items[ form.items.indexOf(item) + i].visible) {
					if (form.items[ form.items.indexOf(item) + i].focusInItem) {
						form.items[ form.items.indexOf(item) + i].focusInItem();
						focusSet = true;
					}
					break;
				}
			}
			if (!focusSet) {
    			form.focusInNextTabElement();
			}
    	}
    	return true;
    };
    form.items.forEach(function (item) { item.keyUp = nextItem;});
    return form;
}


//function formatInputDate(dateStr){
//	if (dateStr == null || validateInputDate(null, null, dateStr, null) == false){
//		return dateStr;
//	}
//	if (dateStr.charAt(1) == '-' || dateStr.charAt(1) == '/' || dateStr.charAt(1) == ' '){
//		dateStr = '0' + dateStr;
//	}
//	if (dateStr.charAt(4) == '-' || dateStr.charAt(4) == '/' || dateStr.charAt(4) == ' '){
//		dateStr = dateStr.substring(0,3) + '0' + dateStr.substring(3);
//	}
//
//	dateStr = dateStr.replace(/-/g, '');
//	dateStr = dateStr.replace(/\//g, '');
//	dateStr = dateStr.replace(/ /g, '');
//
//	var y = Number(dateStr.substring(4,8));
//	var m = Number(dateStr.substring(2,4));
//	var d = Number(dateStr.substring(0,2));
//
//	return new Date(y, m-1, d);
//}

isc.DateItem.addProperties({
	useTextField:true,
	dateFormatter:"dd/MM/yyyy",
	inputFormat:"DMY",
	useMask: false/*,
	validators: [{condition: validateInputDate}],
	blur: function (form, item){
		item.setValue(formatInputDate(item.getDisplayValue()));
		item.validate();
	}*/
});




isc.RPCManager.addClassProperties({
    loginRequired:function (transactionNum, rpcRequest, rpcResponse){
    	loginDynamicForm.setValue("userId", "");
    	loginDynamicForm.setValue("password", "");
//    	loginDynamicForm.addFieldErrors("password", "Session timeout", true);
    	loginDynamicForm.setValue("relogin", true);
    	loginWindow.show();
    }
});

function createTreeGrid() {
	var content = isc.VLayout.create({  width: "75%", padding: 10, });
	var treeGrid = isc.TreeGrid.create({
		width: "25%",
		height: "100%",
		showOpenIcons: false,
		showDropIcons: true,
		closedIconSuffix: "",
		animateFolders: true,
		animateFolderSpeed: 500,
		showResizeBar: true,
		canSort: false,
		showHeaderMenuButton: false,
		fields: [ {name: "Functions", formatCellValue: "record.categoryName"} ],

		nodeClick : function (ssrsCategory, node, recordNum) {
			if (ssrsCategory.isSelected(node)) {
				if (node.js) {
					while (content.getMember(0)) {
						var m = content.getMember(0);
						m.destroy();
					}
					content.addMember(isc.ViewLoader.create({viewURL:node.js}));
				} else {
				}
			}
		},
	});
	return isc.HLayout.create({ top: 95, members: [ treeGrid, content ] });
}

var openSdData = function(imo, callback){
	var sdForm = isc.DynamicForm.create({
		numCols: 2,
		colWidths:[100, 250],
		societies:[],
		shipManagers:[],
		width: "100%", dataSource: "sdDataDS", saveOperationType:"add",
			fields: [
				{ name:"imoNo", width:"*",
					keyDown:
						function(item,form,keyName){
						if (["Tab", "Enter"].contains(keyName)) {
							form.disable();
							tb.disable();
							sdDataDS.fetchData({imoNo:item.getValue()}, function(resp, data, req){
								if (data.length && data.length > 0) {
									form.setData(data[0]);
								} else {
									form.setData({imoNo:item.getValue()});
								}
								form.enable();
								tb.enable();
								form.getItem("classText").focusInItem();
							});
						}
						},
				},
				{ name:"classText", width:"*",  editorType:"comboBox"},
				{ name:"classText2", width:"*",  editorType:"comboBox"},
				{ name:"shipManager", width:"*",  editorType:"comboBox",
					keyDown:function(item,form,keyName){
						if (["Tab", "Enter"].contains(keyName)) {
							var mgr = form.shipManagers.find({shipMgrName:item.getValue()});
							if (mgr != null) {
								form.getItem("shipMgrAddr1").setValue(mgr.addr1);
								form.getItem("shipMgrAddr2").setValue(mgr.addr2);
								form.getItem("shipMgrAddr3").setValue(mgr.addr3);
							}
						}
						},
				},
				{ name:"shipMgrAddr1", width:"*", characterCasing: "upper"},
				{ name:"shipMgrAddr2", width:"*", characterCasing: "upper"},
				{ name:"shipMgrAddr3", width:"*", characterCasing: "upper"},
				{ name:"safetyActAddr1", width:"*", characterCasing: "upper"},
				{ name:"safetyActAddr2", width:"*", characterCasing: "upper"},
				{ name:"safetyActAddr3", width:"*", characterCasing: "upper"},
				{ name:"docAuthority", width:"*",  title:"DOC Authority", editorType:"comboBox"},
				{ name:"docAudit", width:"*",  title:"DOC Audit",  editorType:"comboBox"},
				{ name:"smcAuthority", width:"*",  title:"SMC Authority", editorType:"comboBox"},
				{ name:"smcAudit", width:"*",  title:"SMC Audit", editorType:"comboBox"},
				{ name:"isscAuthority", width:"*",  title:"ISSC Authority", editorType:"comboBox"},
				{ name:"isscAudit", width:"*",  title:"ISSC Audit", editorType:"comboBox"},
			],
		});
	 classSocietyDS.fetchData(null, function(resp, data, req){
		 sdForm.societies = data;
		 var vmap = {};
		 data.forEach(function (c) { vmap[c.id] = c.id + " - " + c.name; });
		 ["classText","classText2","docAuthority","docAudit","smcAuthority","smcAudit","isscAuthority","isscAudit"].forEach(function(n){
			 sdForm.getItem(n).setValueMap(vmap);
		 });
	 });
	 shipManagerDS.fetchData(null, function(resp, data, req){
		 sdForm.shipManagers = data;
		 var vmap = {};
		 sdForm.shipManagers.forEach(function(sm){ vmap[sm.shipMgrName] = sm.shipMgrName; });
		 vmap[sdForm.getItem("shipManager").getValue()] = sdForm.getItem("shipManager").getValue();
		 sdForm.getItem("shipManager").setValueMap(vmap);
	 });

	 var tb = isc.ButtonToolbar.create({ buttons: [{ title:"Update", autoFit: true,
		 click : function () {
			 if (sdForm.validate()) {
				 tb.disable();
				 sdDataDS.updateData(sdForm.getData(), function(resp, data){
					 sdForm.setData(data);
					 if (callback) {
						 callback(data);
					 }
					 tb.enable();
				 }) ;
			 }
		 }, },
		 { title:"Close", autoFit:true, click: function() {
			 localWin.close();
		 }},
		 ]});
	 if (imo && imo != "") {
		 sdForm.getItem("imoNo").setValue(imo);
		 sdForm.getItem("imoNo").keyDown(sdForm.getItem("imoNo"), sdForm, "Tab");
		 sdForm.getItem("imoNo").disable();
	 }
	 var modal = (callback) ? true : false;
	 var localWin = isc.Window.create({
			width: 420, height: 600, isModal: modal, showModalMask: true, title: "SD Data",
			items: [
			     	isc.VLayout.create({
						width: "100%",	height: "100%", padding: 10,
						members: [ sdForm, tb,],
					})
			  ],
			close: function(){ localWin.markForDestroy(); },
		});
	 localWin.show();
	 return sdForm;
};

var openOwnerEnq = function(record){
	 var enqForm = isc.DynamicForm.create({
		 colWidths:[100,400],
			width: "100%", dataSource: "ownerEnquiryDS", saveOperationType:"add", numCols: 2,
			fields: [
			         {name:"id", visible:false, defaultValue:0},
			         {name:"shipName", width:"*",},
			         {name:"shipCname", width:"*",},
			         {name:"ownerName", width:"*",},
			         {name:"contactPerson", width:"*",},
			         {name:"tel", width:"*",},
			         {name:"fax", width:"*",},
			         {name:"email", width:"*",},
			         {name:"replyMedia", width:"*", valueMap:{"By Phone":"By Phone", "By Fax":"By Fax", "By Email":"By Email"}},
			         {name:"replyDate", dateFormatter:"dd/MM/yyyy", width:"*",defaultValue:new Date()},
			         {name:"reply", width:"*",},
			],
		});
	 enqForm.setData(record);

	 var updateRecord = function() {
		 if (enqForm.validate()) {
			 tb.disable();
			 ownerEnquiryDS.updateData(enqForm.getData(), function(resp, data){
				 enqForm.setData(data);
				 tb.enable();
			 });
		 }
	 }

	 var tb = isc.ButtonToolbar.create({
			buttons: [
			          { title:"Save", autoFit: true, click : updateRecord, },
			          ]});
	 var localWin = isc.Window.create({
			width: 558, height: 420, isModal: false, showModalMask: true, title: "Owner Enquiry Log",
			items: [
			     	isc.VLayout.create({
						width: "100%",	height: "100%", padding: 10,
						members: [ enqForm, tb,],
					})
			  ],
			close: function(){ localWin.markForDestroy(); },
		});
	 localWin.show();
	 return enqForm;
};

var openSrDemandNote=function(record) {
	 var form = isc.DynamicForm.create({
		 colWidths:[100,400],
			width: "100%", dataSource: "regMasterDS", saveOperationType:"add", numCols: 2,
			fields: [
			         {name:"applNo" },
			         {name:"invoiceNo" },
			         {name:"ebs"},
			         {name:"due"},
			         {name:"total"},
			         {name:"offNo" },
			         {name:"shipName" },
			         {name:"billingPerson" },
			         {name:"co" },
			         {name:"address" },
			         // print inv, adjust atf
			         // fee desc, amount, user id, gen. date, select
			         // full atf, 50% atf, print
			         // (adhoc) edit fee, edit price, units, amount, gen date
			],
		});
	 form.setData(record);
	 var tb = isc.ButtonToolbar.create({
			buttons: [
			          { title:"Save", autoFit: true, click : null, },
			          ]});
	 var localWin = isc.Window.create({
			width: 558, height: 420, isModal: false, showModalMask: true, title: "Ship Registration Demand Note",
			items: [
			     	isc.VLayout.create({
						width: "100%",	height: "100%", padding: 10,
						members: [ form, tb,],
					})
			  ],
			close: function(){ localWin.markForDestroy(); },
		});
	 localWin.show();
	 return form;
};

var openReserveApp=function(record, taskId) {
	var _update = function(form, target)
	{
		var vm = {"":""};
		["1","2","3"].forEach(function(f){
			_f = target + f;
			if (form.getItem(_f).getValue()) {
				vm[form.getItem(_f).getValue()]=form.getItem(_f).getValue();
			}
		});
		form.getItem(target).setValueMap(vm);
		if (!vm[form.getItem(target).getValue()]) {
			form.getItem(target).setValue(null);
		}
	};
	var updateName = function(form,item,value) { _update(form, "name"); };
	var updateCName = function(form,item,value) { _update(form, "chName"); };

	var reserveForm = isc.DynamicForm.create({
		colWidths:[100,200,100,200],
		width: "100%", dataSource: "preReserveDS", numCols: 4,
		fields: [
	         { name: "id", visible:false},
	         { name: "name1", changed:updateName, characterCasing: "upper"},
	         { name: "chName1", changed:updateCName},
	         { name: "name2", changed:updateName, characterCasing: "upper"},
	         { name: "chName2", changed:updateCName},
	         { name: "name3", changed:updateName, characterCasing: "upper"},
	         { name: "chName3", changed:updateCName},
	         { name: "name", title: "Reserve Name", type:"select", characterCasing: "upper"},
	         { name: "chName", title: "Reserve Chi Name", type:"select"},

	         { name: "applicant", title:"Applicant", colSpan:4, width:"*"},
	         { name: "addr1", title:"Address 1", colSpan:4, width:"*"},
	         { name: "addr2", title:"Address 2", colSpan:4, width:"*"},
	         { name: "addr3", title:"Address 3", colSpan:4, width:"*"},
	         { name: "tel", title:"Tel",colSpan:4},
	         { name: "fax", title:"Fax",colSpan:4},
	         { name: "email", title:"Email",colSpan:4},
	         { name: "owner", title:"Owner Name",colSpan:4, width:"*"},
	         { name: "entryTime", title: "Entry Date", dateFormatter:"dd/MM/yyyy", colSpan:4, type:"staticText"},
		],
	});

	reserveForm.setData(record);
	updateName(reserveForm);
	updateCName(reserveForm);
	var updateRecord = function(id, taskId) {
		if (reserveForm.validate()) {
			tb.disable();
			var data = reserveForm.getData();
			data.taskId = taskId;
			preReserveDS.updateData(data, function(resp, data){
				if (id == "preReserveDS_update") {
					reserveForm.setData(data);
					localWin.setTitle("Ship Name Reservation: " + (data.id ? data.id : "NEW"));
					tb.enable();
				} else {
					localWin.close();
					if (preReservedAppList) {
						preReservedAppList.fetchData({_:new Date()});
					}
					if (preReservedList) {
						preReservedList.fetchData({_:new Date()});
					}
				}
			}, {operationId:id, data:data}) ;
		}
	}

	var tb = isc.ButtonToolbar.create({
		buttons: [
		          {
		        	  icon:"search.png",
		        	  title:"Check",
		        	  autoFit: true,
		        	  click : function () {
		        		  if (reserveForm.validate()) {
		        			  tb.disable();
		        			  var formData = reserveForm.getData();
		        			  preReserveDS.fetchData({fetchType:"checkNames",
		        				  name1: formData.name1,
		        				  name2: formData.name2,
		        				  name3: formData.name3,
		        				  chName1: formData.chName1,
		        				  chName2: formData.chName2,
		        				  chName3: formData.chName3,
		        			  }, function(resp, data){
		        				  tb.enable();
		        				  for (var field in data) {
		        					  var name1 = reserveForm.getItem(field);
		        					  var message = data[field][0];
		        					  switch (message){
		        					  case "OFFENSIVE":
		        						  message = "Offensive word " + data[field][1];
		        						  break;
		        					  case "RESERVED":
		        						  message = "Name reserved by " + data[field][1] + " until " + DateUtil.format(new Date(Number.parseInt(data[field][2])), "dd/MM/yyyy");
		        						  break;
		        					  case "REGISTERED":
		        						  message = "Name is registered " + data[field][1];
		        						  break;
		        					  default:
		        						  break;
		        					  }
		        					  var v = {action:function(){},condition:"return false",defaultErrorMessage:message,requireServer:false,type:"checkNames"}
		        					  name1.validators.add(v);
		        					  name1.validate();
		        					  name1.validators.remove(v);
		        				  }
		        			  });
		        		  }

		        	  },
		          },
		          { icon:"edit.png",title:"Update", autoFit: true,
		        	  click : function () { updateRecord("preReserveDS_update");},
		          },
		          { icon:"add.png",title:"Reserve", autoFit: true,
		        	  click : function () { updateRecord("preReserveDS_reserve", taskId);},
		        	  showIf: function(){ return reserveForm.getData().id > 0; },
		          },
		          { icon:"remove.png",title:"Reject", autoFit: true,
		        	  click : function () {
		        		  reserveForm.getItem("name").setValue(null);
		        		  reserveForm.getItem("chName").setValue(null);
		        		  updateRecord("preReserveDS_reject", taskId);
		        	  },
		        	  showIf: function(){ return reserveForm.getData().id > 0; },
		          },
		          ]});
	var winTitle = "Ship Name Reservation: " + (record.id ? record.id : "NEW");
	var localWin = isc.Window.create({
		width: 632, height: 425, isModal: false, title: winTitle,
		items: [
		        isc.VLayout.create({
		        	width: "100%",	height: "100%", padding: 10,
		        	members: [ reserveForm, tb,],
		        })
		        ],
		        close: function(){ localWin.markForDestroy(); },
	});
	localWin.show();
	return reserveForm;
};
function refreshInbox() {
	if (typeof inbox == "undefined") return;
	if (!inbox.refreshData) return;
	inbox.refreshData();
};
function getTransaction(callback, properties) {
	var txForm = isc.DynamicForm.create({
		fields:[
		{name:"details", length:"720", title:"Details", width:400},
		{name:"changeDate", type:"date", title:"Change Date", required:true, defaultValue:new Date()},
		{name:"changeHour", type:"integer",length:4, title:"Change Hour (HHmm)", required:true},
		{type:"button", title:"OK", click:function() {
			if (this.form.validate()) {
				callback(this.form.getData());
				win.close();
			}
			},
			colSpan:2, align:"right", width:120,
		},
			]});
	if (properties && properties.changeDate) {
		if (properties.changeDate.getHours && properties.changeDate.getMinutes) {
			txForm.getField("changeDate").setValue(properties.changeDate);
			var changeHour = ((properties.changeDate.getHours() < 10) ? "0" :"" ) + properties.changeDate.getHours();
			changeHour += ((properties.changeDate.getMinutes() < 10) ? "0" :"" ) + properties.changeDate.getMinutes(); 
			txForm.getField("changeHour").setValue(changeHour);
		}
	}
	if (properties && properties.details) {
		txForm.getField("details").setValue(properties.details);
	}
	var win = isc.Window.create({
		title:"Transaction",
		width:600, height:220, items:[txForm]});
	win.show();
};

function validateInputDate(item, validator, value, record){
	if (validator == null){
		validator = {};
		return;
	}
	validator.errorMessage = "";
	var dateStr = null;
	if (item != null){
		dateStr = item.getDisplayValue();
	}
	if (dateStr == null){
		dateStr = value;
	}
	isc.logEcho("--->"+dateStr);
	if (dateStr == null || dateStr==''){
		return true;
	}
	if (dateStr.indexOf('.') >= 0){
		validator.errorMessage = "Incorrect Format";
		return false;
	}
	if (dateStr.charAt(1) == '-' || dateStr.charAt(1) == '/' || dateStr.charAt(1) == ' '){
		dateStr = '0' + dateStr;
	}
	if (dateStr.charAt(4) == '-' || dateStr.charAt(4) == '/' || dateStr.charAt(4) == ' '){
		dateStr = dateStr.substring(0,3) + '0' + dateStr.substring(3);
	}
	if (dateStr.length > 8){
		var delimiter1 = dateStr.charAt(2);
		var delimiter2 = dateStr.charAt(5);
		if (delimiter1 != delimiter2){
			validator.errorMessage = "Incorrect Format";
			return false;
		}
	}
	dateStr = dateStr.replace(/-/g, '');
	dateStr = dateStr.replace(/\//g, '');
	dateStr = dateStr.replace(/ /g, '');
	if (dateStr.length == 8){
		var y = dateStr.substring(4);
		var m = dateStr.substring(2,4);
		if (Number(m) > 12 || Number(m) < 1){
			validator.errorMessage = "Invalid Date";
			return false;
		}
		var d = dateStr.substring(0,2);
		birthDate = new Date (y, m-1, d);
		if (birthDate.getMonth()+1 != Number(m) || birthDate.getDate() != d){
			validator.errorMessage = "Invalid Date";
			return false;
		}
		return true;
	} else {
		validator.errorMessage = "Incorrect Format";
		return false;
	}
};

function formatInputDate(dateStr){
	if (dateStr == null || validateInputDate(null, null, dateStr, null) == false){
		return dateStr;
	}
	if (dateStr.charAt(1) == '-' || dateStr.charAt(1) == '/' || dateStr.charAt(1) == ' '){
		dateStr = '0' + dateStr;
	}
	if (dateStr.charAt(4) == '-' || dateStr.charAt(4) == '/' || dateStr.charAt(4) == ' '){
		dateStr = dateStr.substring(0,3) + '0' + dateStr.substring(3);
	}

	dateStr = dateStr.replace(/-/g, '');
	dateStr = dateStr.replace(/\//g, '');
	dateStr = dateStr.replace(/ /g, '');

	var y = Number(dateStr.substring(4,8));
	var m = Number(dateStr.substring(2,4));
	var d = Number(dateStr.substring(0,2));

	return new Date(y, m-1, d);
}
