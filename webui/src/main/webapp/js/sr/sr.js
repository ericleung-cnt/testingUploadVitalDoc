var thickBtnHeight = 50;
var paymentStatusValueMap = {"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)"};
var dnStatusValueMap = {"3":"Issued", "4":"Autopay Arranged", "11":"Written Off", "12":"Cancelled", "16":"Refunded"};
var dnFormUtil = {
		performCopy: function(record, callback, win){
			console.log(record);
			callback(record);
			win.close();
		},
		validateApplNo: function(form){
			var applNo = form.getField("applNo").getValue();
			if (applNo == null){
				isc.warn("Must input application number first ...");
				return false;
			} else {
				return true;
			}
		}
};
var dnItemMaxSelection = 2;
var dnItemSelected = 0;

var openSrBillingPersonForm = function(windowTitle, applNo, callback){
	console.log("open sr billing person");
	console.log("applNo:" + applNo);
	var srBillingPersonList = isc.ListGrid.create({
		ID:"srBillingPersonList",
		dataSource: "demandNoteBillingPersonDS",
		showFilterEditor:true,
		//filterOnKeypress:true,
		show:function(){
			this.Super('show', arguments);
			if(windowTitle.startsWith('Company Name')){
				this.setFieldTitle('billingPerson', 'Company Name');
			}else{
				this.setFieldTitle('billingPerson', 'Billing Person');
			}
		},
		fields:[
			{ name:"billingPerson", title:"Billing Person", width:200 },
			{ name:"billingPersonType", title:"Type", width:100},
			{ name:"address1", title:"Address1", width:"*" }
		],
		rowDoubleClick: function(record, recordNum, fieldNum){
			dnFormUtil.performCopy(record, callback, srBillingPersonWindow);
		}
	});
	var srBillingPersonForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "srBilingPersonForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50,
				click: function(){
					var record = srBillingPersonList.getSelectedRecord();
					//console.log(record);
					//callback(record);
					//srBillingPersonWindow.close();
					dnFormUtil.performCopy(record, callback, srBillingPersonWindow);
				}
			},
			{ name:"close", title:"Close", width:50,
				click: function(){
					srBillingPersonWindow.close();
				}
			}
		]
	});
	var srBillingPersonWindow = isc.Window.create({
		ID: "srBillingPersonWindow",
		title: windowTitle, //"Billing Person",
		width: 800,
		height: 200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					srBillingPersonList,
					srBillingPersonForm_BtnToolbar
				]
			})
		],
		close: function() {srBillingPersonWindow.markForDestroy(); }
	});
	srBillingPersonWindow.show();
	srBillingPersonList.setData([]);
	srBillingPersonList.fetchData({"applNo":applNo}, function(){});
	return srBillingPersonWindow;
}

var openSrRPCOForm = function(windowTitle, applNo, callback){
	console.log("open RP C/O");
	console.log("applNo:" + applNo);
	var srRpCOList = isc.ListGrid.create({
		ID:"srRpCOList",
		dataSource: "demandNoteBillingPersonDS",
		showFilterEditor:true,
		//filterOnKeypress:true,
		fields:[
			{ name:"billingPerson", title:"Billing Person", width:200 },
			{ name:"billingPersonType", title:"Type", width:100},
			{ name:"address1", title:"Address1", width:"*" }
		],
		rowDoubleClick: function(record, recordNum, fieldNum){
			dnFormUtil.performCopy(record, callback, srRpCOWindow);
		}
	});
	var srRpCOForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "srRpCOForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50,
				click: function(){
					var record = srRpCOList.getSelectedRecord();
					//console.log(record);
					//callback(record);
					//srBillingPersonWindow.close();
					dnFormUtil.performCopy(record, callback, srRpCOWindow);
				}
			},
			{ name:"close", title:"Close", width:50,
				click: function(){
					srRpCOWindow.close();
				}
			}
		]
	});
	var srRpCOWindow = isc.Window.create({
		ID: "srRpCOWindow",
		title: windowTitle, //"Billing Person",
		width: 800,
		height: 200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					srRpCOList,
					srRpCOForm_BtnToolbar
				]
			})
		],
		close: function() {srRpCOWindow.markForDestroy(); }
	});
	srRpCOWindow.show();
	srRpCOList.setData([]);
	srRpCOList.fetchData({"applNo":applNo}, function(){});
	return srRpCOWindow;
}

var openTranscriptBillingPersonForm = function(windowTitle, callback){
	var transcriptBillingPersonList = isc.ListGrid.create({
		ID:"transcriptBillingPersonList",
		dataSource: "demandNoteBillingPersonDS",
		showFilterEditor:true,
		//filterOnKeypress:true,
		//fetchDelay:500,
		show:function(){
			this.Super('show', arguments);
			if(windowTitle.startsWith('Company Name')){
				this.setFieldTitle('billingPerson', 'Company Name');
			}else{
				this.setFieldTitle('billingPerson', 'Billing Person');
			}
		},
		fields:[
			{ name:"billingPerson", title:"Billing Person", width:200 },
			{ name:"billingPersonType", title:"Type", width:100},
			{ name:"address1", title:"Address1", width:"*" }
		],
		rowDoubleClick: function(record, recordNum, fieldNum){
			dnFormUtil.performCopy(record, callback, transcriptBillingPersonWindow);
		}
	});
	var transcriptBillingPersonForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "transcriptBilingPersonForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50,
				click: function(){
					var record = transcriptBillingPersonList.getSelectedRecord();
					//console.log(record);
					//callback(record);
					//transcriptBillingPersonWindow.close();
					dnFormUtil.performCopy(record, callback, transcriptBillingPersonWindow);
				}
			},
			{ name:"close", title:"Close", width:50,
				click: function(){
					transcriptBillingPersonWindow.close();
				}
			}
		]
	});
	var transcriptBillingPersonWindow = isc.Window.create({
		ID: "transcriptBillingPersonWindow",
		title: windowTitle, //"Billing Person",
		width: 800,
		height: 200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					transcriptBillingPersonList,
					transcriptBillingPersonForm_BtnToolbar
				]
			})
		],
		close: function() {transcriptBillingPersonWindow.markForDestroy(); }
	});
	transcriptBillingPersonWindow.show();
	transcriptBillingPersonList.setData([]);
	transcriptBillingPersonList.fetchData({}, function(){});
	return transcriptBillingPersonWindow;
}
var openShipManagerBillingPersonForm = function(windowTitle, callback){
	var shipManagerBillingPersonList = isc.ListGrid.create({
		ID:"shipManagerBillingPersonList",
		dataSource: "demandNoteBillingPersonDS",
		showFilterEditor:true,
		//filterOnKeypress:true,
		//fetchDelay:500,
		show:function(){
			this.Super('show', arguments);
			if(windowTitle.startsWith('Company Name')){
				this.setFieldTitle('billingPerson', 'Company Name');
			}else{
				this.setFieldTitle('billingPerson', 'Billing Person');
			}
		},
		fields:[
			{ name:"billingPerson", title:"Billing Person", width:200 },
			{ name:"billingPersonType", title:"Type", width:100},
			{ name:"address1", title:"Address1", width:"*" }
		],
		rowDoubleClick: function(record, recordNum, fieldNum){
			dnFormUtil.performCopy(record, callback, shipManagerBillingPersonWindow);
		}
	});
	var shipManagerBillingPersonForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "shipManagerBilingPersonForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50,
				click: function(){
					var record = shipManagerBillingPersonList.getSelectedRecord();
					//console.log(record);
					//callback(record);
					//transcriptBillingPersonWindow.close();
					dnFormUtil.performCopy(record, callback, shipManagerBillingPersonWindow);
				}
			},
			{ name:"close", title:"Close", width:50,
				click: function(){
					shipManagerBillingPersonWindow.close();
				}
			}
		]
	});
	var shipManagerBillingPersonWindow = isc.Window.create({
		ID: "shipManagerBillingPersonWindow",
		title: windowTitle, //"Billing Person",
		width: 800,
		height: 200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					shipManagerBillingPersonList,
					shipManagerBillingPersonForm_BtnToolbar
				]
			})
		],
		close: function() {shipManagerBillingPersonWindow.markForDestroy(); }
	});
	shipManagerBillingPersonWindow.show();
	shipManagerBillingPersonList.setData([]);
	shipManagerBillingPersonList.fetchData({}, function(){});
	return shipManagerBillingPersonWindow;
}
var openSrDemandNote=function(record){
	console.log("openSrDemandNote");
	var atcDueDate;
	var refreshItems = function(form,item,value) {
		if (value.match(/^\d{4}$/)) {
			item.setValue(value + "/");
		} else if (value.match(/^\d{4}\/\/$/)) {
			item.setValue(value.substring(0,5));
		}
		if (value.length >=8) {
			regMasterDS.fetchData({applNo:value},
					function(resp,data) {
				if (data.length > 0) {
					form.getField("shipName").setValue(data[0].regName);
					form.getField("offNo").setValue(data[0].offNo);
					console.log("reg date: " + data[0].regDate);
					atcDueDate = data[0].atfDueDate;
					atcDueDate.setFullYear(atcDueDate.getFullYear() - 1);
					atcDueDate.setDate(atcDueDate.getDate()-1);
					console.log("atc due: " + atcDueDate);
				}
			});
			if (record && record.type == "Regular") {
				demandNoteItemDS.fetchData({applNo:value}, function(resp,data,req){
					grid.setData(data);
					calculateTotal();
				}, {operationId:"demandNoteItemDS_unused"});
			}
		} else {
			grid.setData([]);
			calculateTotal();
		}
	};
	var generationTime = new Date();
	var form = isc.DynamicForm.create({
		numCols:4,
		fields:[
			{name:"demandNoteNo", title:"Demand Note No.", type:"staticText", colSpan:3},
			{name:"applNo", title:"Appl No.", changed:refreshItems, required:true, colSpan:3},
			{name:"offNo", title:"Official No.", type:"staticText", colSpan:3, required:true},
			{name:"shipName", title:"Ship Name", type:"staticText", colSpan:3},
			{name:"billName", title:"Billing Person", required:true, colSpan:3, width:480, characterCasing:"upper"},
			{name:"coName", title:"C/O", colSpan:3, width:480, characterCasing:"upper"},
			{name:"address1", title:"Address", width:480, colSpan:3, characterCasing:"upper"},
			{name:"address2", title:"", width:480, colSpan:3, characterCasing:"upper"},
			{name:"address3", title:"", width:480, colSpan:3, characterCasing:"upper"},
			{name:"email", title:"Email", width:200, endRow:true},
			{name:"tel", title:"Tel", width:200},	
			{name:"fax", title:"Fax", width:200, endRow:true },
			{name:"generationTime", title:"Issue Date", type:"date", dateFormatter:"dd/MM/yyyy", disabled:true, defaultValue:generationTime},
			{name:"dueDate", title:"Due Date", type:"date", dateFormatter:"dd/MM/yyyy", required:true, defaultValue:generationTime,
				changed:function(form, item, value){
					var compared = new Date();
					compared.setHours(0);
					compared.setMinutes(0);

					if(value < compared){
						isc.warn("Due Date should be a future date");
					}
				}
			},
//			{name:"atcDue", title:"ATC due date", type:"button", width:100,
//					click: function(){
//						form.getField("dueDate").setValue(atcDueDate);
//					}
//				},
			{name:"paymentStatus", title:"Payment Status", type:"staticText", valueMap:paymentStatusValueMap},
			{name:"status", title:"Status", type:"staticText", valueMap:dnStatusValueMap},
			//{name:"ebsFlag", title:"EBS Flag", type:"boolean"},
			{name:"amount", title:"Total", type:"staticText", format:"$#,###.00"},
			],
			setData:function(data) {
				console.log("SR demand note");
				this.setValues(data);
				if (data.applNo && data.applNo.length >= 8) {
					regMasterDS.fetchData({applNo:data.applNo},
							function(resp,data) {
						if (data.length > 0) {
							form.getField("shipName").setValue(data[0].regName);
							form.getField("offNo").setValue(data[0].offNo);
						}
					}
					);
					if (!data.demandNoteNo) { // get all by appl No
						demandNoteItemDS.fetchData({applNo:data.applNo}, function(resp,data,req){
							grid.setData(data);
							calculateTotal();
						});
					}
				}
				if (data.demandNoteNo) {  // existing dn, get by demand note no
					demandNoteItemDS.fetchData({dnDemandNoteNo:data.demandNoteNo}, function(resp,data,req){
						data.forEach(function (r) { r.selected = true;});
						grid.setData(data);
						calculateTotal();
						buttons.getMembers().forEach(function(m){
							var newOnly = ["50% ATF","Full ATF","Adjust ATF","Add item","Remove item(s)"];
							if (newOnly.contains(m.getTitle())) {
								m.disable();
							} else {
								m.enable();
							}
						});
						form.getField(btnCopyBillingPersonFromOwner).setDisabled(true);
						form.getField(btnCopyCOFromRP).setDisabled(true);
						form.getField(btnCopyFromLawyerTranscriptApplicant).setDisabled(true);
						form.getField(btnCopyFromShipManager).setDisabled(true);
						form.getField(btnCopyCOFromShipManager).setDisabled(true);
					}, {operationId:"demandNoteItemDS_demandNoteNo"});
				}
			}
	});
	var btnCopyBillingPersonFromOwner = isc.Button.create({
		title: "Billing Person<br>from Owner/RP",
		height:thickBtnHeight,
		width:120,
		click:function(){
			if (dnFormUtil.validateApplNo(form)){
				console.log(form.getField("applNo").getValue());
			//console.log(form.getItem("applNo").getValue());
				openSrBillingPersonForm("Copy Billing Person from Owner/DC/RP", form.getField("applNo").getValue(), function(record){
					form.getField("billName").setValue(record.billingPerson);
					form.getField("address1").setValue(record.address1);
					form.getField("address2").setValue(record.address2);
					form.getField("address3").setValue(record.address3);
					form.getField("email").setValue(record.email);
					if (record.billingPersonType=="RP"){
						form.getField("tel").setValue(record.tel);
						form.getField("fax").setValue(record.fax);
					} else {
						form.getField("tel").setValue(null);
						form.getField("fax").setValue(null);						
					}
//					if (["OWNER","DEMISE"].contains(record.billingPersonType)) {
//						repDS.fetchData({applNo:form.getValue("applNo")}, function(resp,data,req){
//							if (data && data.length == 1) {
//								form.setValue("coName", data[0].name);
//								form.setValue("email", data[0].email);
//							}
//						});
//					}
				})
			}
		}
	});
	var btnCopyCOFromRP = isc.Button.create({
		title: "C/O<br>from RP",
		height:thickBtnHeight,
		click:function(){
			if (dnFormUtil.validateApplNo(form)){
				openSrRPCOForm("Copy C/O from RP", form.getField("applNo").getValue(), function(record){
					console.log(record);
					form.getField("coName").setValue(record.billingPerson);
					form.getField("address1").setValue(record.address1);
					form.getField("address2").setValue(record.address2);
					form.getField("address3").setValue(record.address3);
					form.getField("tel").setValue(record.tel);
					form.getField("fax").setValue(record.fax);
					form.getField("email").setValue(record.email);
				})
			}
		}
	});
	var btnCopyFromLawyerTranscriptApplicant = isc.Button.create({
		title: "Billing Person<br>from Lawyer/<br>Transcript Applicant",
		height:thickBtnHeight,
		width:120,
		click:function(){
			if (dnFormUtil.validateApplNo(form)){
				openTranscriptBillingPersonForm("Copy Billing Person from Lawyer/Transcript Applicant", function(record){
					console.log(record);
					form.getField("billName").setValue(record.billingPerson);
					form.getField("address1").setValue(record.address1);
					form.getField("address2").setValue(record.address2);
					form.getField("address3").setValue(record.address3);
					form.getField("tel").setValue(record.tel);
					form.getField("email").setValue(record.email);
				})
			}
		}
	});
	var btnCopyFromShipManager = isc.Button.create({
		title: "Billing Person<br>from<br>Ship Manager",
		height:thickBtnHeight,
		width:100,
		click:function(){
			if (dnFormUtil.validateApplNo(form)){
				openShipManagerBillingPersonForm("Copy Billing Person from Ship Manager", function(record){
					console.log(record);
					form.getField("billName").setValue(record.billingPerson);
					form.getField("address1").setValue(record.address1);
					form.getField("address2").setValue(record.address2);
					form.getField("address3").setValue(record.address3);
					//form.getField("email").setValue(record.email);
				})
			}
		}
	});
	var btnCopyCOFromShipManager = isc.Button.create({
		title: "C/O from<br>Ship Manager",
		height:thickBtnHeight,
		width:100,
		click:function(){
			if (dnFormUtil.validateApplNo(form)){
				openShipManagerBillingPersonForm("Copy C/O from Ship Manager", function(record){
					console.log(record);
					form.getField("coName").setValue(record.billingPerson);
					form.getField("address1").setValue(record.address1);
					form.getField("address2").setValue(record.address2);
					form.getField("address3").setValue(record.address3);
					//form.getField("email").setValue(record.email);
				})
			}
		}
	});
	var btnPrintDemandNote = isc.Button.create({
		title: "Print again",
		height:thickBtnHeight,
		click:function(){
			console.log("print demand log");
			if (form.getField("demandNoteNo").getValue()!=null){
				ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":form.getField("demandNoteNo").getValue()}]);
			}
		}
	});
	var btnSaveAndPrintDemandNote = isc.Button.create({
		title: "Save & Print",
		height:thickBtnHeight,
		click:function(){
			console.log("save and print demand log");
			if (form.validate()) {
				if (form.getField("address1").getValue()!=null 
						&& form.getField("address1").getValue().length>50){
					isc.warn("Address1 too long to print in demand note, should less than 50");
					return;
				}
				if (form.getField("address2").getValue()!=null && 
						form.getField("address2").getValue().length>50){
					isc.warn("Address2 too long to print in demand note, should less than 50");
					return;
				}
				if (form.getField("address3").getValue()!=null && 
						form.getField("address3").getValue().length>50){
					isc.warn("Address3 too long to print in demand note, should less than 50");
					return;
				}
				var amt = form.getField("amount").getValue();
				if (amt == null || amt <= 0) {
					isc.warn("0 amount");
				} else {
					form.getData().demandNoteItems = grid.getData().findAll(function(r) { return r.selected; } );
					if (form.getData().demandNoteItems.length>4){
						isc.say("Please select not more than 4 demand note items.")
						return;
					}
					demandNoteHeaderDS.addData(form.getData(), function(resp,data,req){
						if (data) {
							form.setData(data);
							ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":data.demandNoteNo}]);
						}
					}, {operationId:"CREATE_AD_HOC_DEMAND_NOTE"});
				}
			}
		}
	});
	var buttons = isc.HLayout.create({
		height:22,
		align:"right",
		members:[]});
	var calculateTotal = function(){
		var total = 0;
		grid.getData().forEach(function(r){ if (r.selected) { total += r.amount; } });
		form.getField("amount").setValue(total);
	};

	var countDnItemSelected = function(record){
		if (record.selected){
			if (dnItemSelected>=dnItemMaxSelection){
				isc.warn("Cannot select more than " + dnItemMaxSelection + "!");
				record.selected = false;
			} else {
				dnItemSelected++;
			}
		} else {
			dnItemSelected --;
		}
	}

	var grid = isc.ListGrid.create({
		height:200,
		width:830,
		fields:[
			{name:"fcFeeCode", title:"Desc",
				optionDataSource:"feeCodeDS",
				valueField:"id",
				displayField:"engDesc",
				width:"*"},
			{name: "adhocDemandNoteText", title:"ad hoc text", width:100},
			{ name:"unitPrice", title:"Price", width:100},
			{name:"chargedUnits", title:"Unit", width:100},
			{name:"amount",  title:"Amount", width:100, format:"$#,###.00"},
			{name:"selected", title:"Selected", type:"boolean"},
			],
		rowClick: function (record, recordNum, fieldNum) {
			if (!form.getData().demandNoteNo) {
				if (fieldNum == 5) { // click selected
					var newValue = (typeof record.selected == "undefined")? true : !record.selected;
					if (newValue) {
						if (grid.getData().filter(function(r) { return r.selected; }).length >= 4) {
							isc.say("Please select not more than 4 demand note items.")
							return;
						}
					}
					record.selected = (typeof record.selected == "undefined")? true : !record.selected;
					//countDnItemSelected(record);
					grid.refreshFields();
//					var data = grid.data;
//					var i=0;
//					for(i=0; i<data.length; i++){
//						if (data[i].selected!=undefined){
//							console.log("selected:" + data[i].selected);
//						}
//					}
					calculateTotal();
					setAtcDueDate(grid.data);
				}
			}
		},
	});
	var setAtcDueDate = function(items){
		var itemSelected = 0;
		var atcItemIncluded = false;
		for (i=0; i<items.length; i++){
			if (items[i].selected==true) itemSelected ++;
			if (items[i].fcFeeCode == '01' && items[i].selected==true) atcItemIncluded = true;
		}
		if (itemSelected == 1 && atcItemIncluded) {
			form.getField("dueDate").setValue(atcDueDate);
		} else {
			form.getField("dueDate").setValue(generationTime);
		}
	};
	var winHeight = function(){
		if (record && record.type == "AdHoc"){
			return 800;
		} else {
			return 620;
		}
	}
	var winWidth = function(){
		if (record && record.type == "AdHoc"){
			return 980;
		} else {
			return 850;
		}
	}
	var winTitle = function(){
		if (record && record.type == "AdHoc"){
			return "SR AdHoc Demand Note";
		} else {
			return "SR Regular Demand Note";
		}
	}
	var feeCodes = [];
	feeCodeDS.fetchData({}, function(_1,data,_3) { feeCodes = data;})
	var win = isc.Window.create({title:winTitle(), height:winHeight(), width:winWidth(), items:[form, grid, buttons]});
console.log("it is demand note");
	if (record && record.type == "AdHoc") {
		var itemForm = isc.DynamicForm.create({
			numCols:6,
			fields:[
				{name:"fcFeeCode",title:"Description",
					optionDataSource:"feeCodeDS",
					valueField:"id",
					//displayField:"engDesc",
					formatValue:function(value, record, form, item){
						var item = feeCodes.find({id:value});
						if (item != null) {
							return item.id + " " + item.engDesc + (item.chiDesc != null ? " " + item.chiDesc : "")
						} else {
							return value;
						}
					},
					required:true,
					colSpan:6,
					width:600,
					//sortField:"engDesc",
					optionFilterContext:{"operationId":"FETCH_FOR_SR", "sortBy":"id"},
					changed: function (form, item, value) {
						form.getItem("unitPrice").setValue((item && item.getSelectedRecord()) ? item.getSelectedRecord().feePrice : null);
					},
				},
				{name:"unitPrice", 		title:"Price", 	required:true, 	type:"decimal", startRow:true,
					changed: function(form, item, value){
						if(value!=null && value!=undefined){
							if (form.getItem("chargedUnits")!=null && form.getItem("chargedUnits")!=undefined){
								var chargedUnits = form.getItem("chargedUnits").getValue();
								if (chargedUnits!=null && chargedUnits!=undefined){
									form.getItem("amount").setValue(value * form.getItem("chargedUnits").getValue());
									return;
								} 									
							} 
						} 
						form.getItem("amount").setValue(0);
					}
				},
				{name:"chargedUnits", 	title:"Unit", 	required:true, 	type:"integer",
					changed: function(form, item, value){
						if(value!=null && value!=undefined){
							if (form.getItem("unitPrice")!=null && form.getItem("unitPrice")!=undefined){
								var unitPrice = form.getItem("unitPrice").getValue();
								if (unitPrice!=null && unitPrice!=undefined){
									form.getItem("amount").setValue(value * form.getItem("unitPrice").getValue());	
									return;
								}
							}							
						}
						form.getItem("amount").setValue(0);
					}
				},
				{name:"amount",  				title:"Amount", type:"decimal",		required:true, format:"$#,###.00"},
				{name:"adhocDemandNoteText",  	title:"Text", 	type:"textArea",    rowSpan:3, colSpan:6, width:420, changed:upperTA}


				]
		});
		win.addItem(itemForm, 2);
		if (!record.demandNoteNo){
			buttons.addMember(isc.Button.create({title:"Add item", height:thickBtnHeight, click:function(){
				if (itemForm.validate()) {
					var row = itemForm.getData();
					row.selected = true;
					grid.addData(row);
					itemForm.setData({});
					calculateTotal();
				}
			}
			}), 0);
			buttons.addMember(isc.Button.create({title:"Remove item(s)", height:thickBtnHeight, click:function() {
				grid.getSelection().forEach(function(r){ grid.removeData(r);});
				calculateTotal();
			}}), 1);
			buttons.addMember(btnCopyBillingPersonFromOwner, 2);
			buttons.addMember(btnCopyCOFromRP, 3);
			buttons.addMember(btnCopyFromLawyerTranscriptApplicant, 4);
			buttons.addMember(btnCopyFromShipManager, 5);
			buttons.addMember(btnCopyCOFromShipManager, 6);
			buttons.addMember(btnSaveAndPrintDemandNote, 7);
		}
	} else {
		if (!record.demandNoteNo){
			buttons.addMember(btnCopyBillingPersonFromOwner, 0);
			buttons.addMember(btnCopyCOFromRP, 1);
			buttons.addMember(btnCopyFromLawyerTranscriptApplicant, 2);
			buttons.addMember(btnCopyFromShipManager, 3);
			buttons.addMember(btnCopyCOFromShipManager, 4);
			buttons.addMember(btnSaveAndPrintDemandNote, 5);
		}
	}
	if (record && record.demandNoteNo) {
		buttons.addMember(btnPrintDemandNote, 0);
		if (!((record.status=="3" && record.paymentStatus=="1") || (record.status=="12" && record.paymentStatus=="0"))) {
			buttons.addMember(isc.Button.create({
				title:"Cancel<br>Demand Note", 
				height:thickBtnHeight, 
				click:function(){
					if (record.paymentStatus!="0") {
						isc.warn("demand note already paid, cannot be cancel");
						return;
					}				
					mmoDNDetailCancelDNWindow.show();
					mmoDNDetailCancelDNDynamicForm.fetchData({'demandNoteNo': record.demandNoteNo}, function(dsResponse, data, dsRequest) {
						if (dsResponse.status == 0) {
							var record = (data.get && data.get(0)) ? data.get(0) : data;
							mmoDNDetailCancelDNSectionForm_ToolBar.getButton('confirmBtn').setDisabled(record.cwStatus=='C');
						}
					});
				}
			})
			);
		}
	}
	if (record && record.type) {
		var btnClear = isc.Button.create({
			title:"Clear", height:thickBtnHeight,
			click:function(){
				win.close();
				openSrDemandNote({type:record.type});
			}
		});
		buttons.addMember(btnClear);
	}
	form.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	grid.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	buttons.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	win.show() ;
	if (record) {
		form.setData(record);
	}
};

function simpleSrReport(name, id, fields, customValidate) {
	var label = isc.Label.create({
		height : 20,
		wrap : false,
		contents : "<p><b><font size=2px>"+name+"<br /></font></b></p>"
	});
	if (!fields) {
		fields = [
			       {name : "reportDate", title : "Report Date", type : "date", defaultValue : new Date(), required : true, dateFormatter:"dd/MM/yyyy"},
			       {type : "button", title : "Generate Report", width:150, click:
			    	   function() {ReportViewWindow.displayReport([id, this.form.getData()]);}
			       }
			       ];
	} else {
		fields.add({type : "button", title : "Generate Report", width:150, click:
	    	   function() {
				if (customValidate) {
					if (!customValidate(this.form)) {
						return;
					}
				}
				if (this.form.validate()) {
					ReportViewWindow.displayReport([id, this.form.getData()]);
				}

			}
	       });
	}
	var form = isc.DynamicForm.create({ fields : fields, });
	form.printButton = form.getFields()[form.getFields().length - 1];
	vlayout = isc.VLayout.create({ members : [label,form] });
	vlayout.form = form;
	return vlayout;
}


view = createTreeGrid();
viewLoader.setView(view);

var demandNoteSub = [{categoryName:"Demand Note", js:"js/sr/demandNote/regular.js",},
	{categoryName:"Delete / Adjust Demand Note Item", js:"js/sr/demandNote/item_del.js",},
	];
if (!loginWindow.DEMAND_NOTE_READ_ONLY()) {
	demandNoteSub.addAll([	{categoryName:"Regular Demand Note", callback:function(){ openSrDemandNote({type:"Regular"}, null, 0);},},
	{categoryName:"AdHoc Demand Note", callback:function(){ openSrDemandNote({type:"AdHoc"}, null, 0);},},]);
}
var srTreeRoot = {sub:[{categoryName:"Ship Registration Maintenance",
	sub:[{categoryName:"Ship Name Reservation", js:"js/sr/shipReg/ship_name_ckrs.js", },
			{categoryName:"Ship Registration Maintenance", js:"js/sr/shipReg/ship_name_appl_prog.js",},
			{categoryName:"Amend Transaction Details", js:"js/sr/shipReg/transaction_amend.js",},
			{categoryName:"Owner Enquiry Records", js:"js/sr/shipReg/owner_enq_rec.js",},
			{categoryName:"Print Transcript - Batch Mode", js:"js/sr/reports/transcript_print_batch_mode.js",},
			]
		},
		{categoryName:"CSR Form Maintenance",
			sub:[{categoryName:"CSR Form Maintenance ", js:"js/sr/csrForm/csr_input_print.js", },
			{categoryName:"Maintain SD Data", js:"js/sr/csrForm/csr_sd_mnt.js", },
			]
		},
		{categoryName:"SR Demand Note", sub:demandNoteSub },
		,

		]};

if (loginWindow.SR_REPORT_ENABLED()) {
	srTreeRoot.sub.add({categoryName:"SR Reports",
		sub:[
		     {categoryName:"Registration Type of Ships registered in H.K.", js:"js/sr/reports/RPT_SR_014_RM0020_RegistrationTypeOfShipsRegisteredInHK.js"},
		     {categoryName:"Breakdown of No. & Grt of Ships by Type", js:"js/sr/reports/RPT_SR_015_RM0040_BreakdownOfNoAndGrtOfShipsByType.js"},
		{categoryName:"Discounted Annual Tonnage Charges Report", js:"js/sr/reports/RPT-SR-012_AD0100_DiscountedAnnualTonnageCharges.js"},
		{categoryName:"Summary of Ships by Ship Type", js:"js/sr/reports/RPT-SR-013_RM0010_ShipsByShipType.js"},
		{categoryName:"Total No. and Tonnage of Ships in the Register", js:"js/sr/reports/RPT-SR-016_RM0030_TotalAndTonnageShipsRegister.js"},
		{categoryName:"Categories of Owners of Ships", js:"js/sr/reports/RPT-SR-017_RM0060_OwnerCatOfShips.js"},
		{categoryName:"Registers Opened, Closed and Change Ownership", js:"js/sr/reports/RPT-SR-018_RM0110_RegistersOpenedClosedChangeOwnership.js"},
		{categoryName:"Summary of Ships in the Pipeline", js:"js/sr/reports/RPT-SR-019_RM0120_ShipsInPipeline.js"},
		{categoryName:"De-Registered Ships Report", js:"js/sr/reports/RPT-SR-020_RW0010_De-RegisteredShips.js"},
		{categoryName:"Registered Ships Report", js:"js/sr/reports/RPT-SR-021_RW0100_RegisteredShip.js"},
		{categoryName:"Annual Report for Ships Registered / De-Registered", js:"js/sr/reports/RPT-SR-022_RY0100_AnnualReportShipsRegisteredDe-Registered.js"},
		{categoryName:"Annual Report for Ships Registered / De-Registered Detail", js:"js/sr/reports/RPT-SR-022_DETAIL_RY0100_AnnualReportShipsRegisteredDe-Registered.js"},
		{categoryName:"Tonnage Distribution of Ships", js:"js/sr/reports/RPT-SR-023_RA0030_TonnageDistributionShips.js"},
		{categoryName:"Ranking of Group Owners / Companies", js:"js/sr/reports/RPT-SR-024_RA0110_RankingGroupOwnersCompanies.js"},
		{categoryName:"Mortgage Transactions", js:"js/sr/reports/MortgageTransactionsReport.js"},
		{categoryName:"De-registration Reasons", js:"js/sr/reports/DeregReasons.js"},
		{categoryName:"Detailed List Of Ships Registerd by Ship Name", js:"js/sr/reports/detailedListOfShipsRegisteredByShipName.js"},
		{categoryName:"Detailed List Of Ships Registerd by Ship Type", js:"js/sr/reports/detailedListOfShipsRegisteredByShipType.js"},
		{categoryName:"List of Representatives", js:"js/sr/reports/RPT_SR_ListOfRepresentatives.js"},
		{categoryName:"Acknowledgement for Collection of Document (Delivery only)", js:"js/sr/reports/NewReg_ACD.js"},
		{categoryName:"Acknowledgement for Collection of Document (General Case)", js:"js/sr/reports/ProToFull_ACD.js", onClick:function(){isc.say("----");}},
		{categoryName:"Excel Ship Reg and Ship De-Reg Report", js:"js/sr/reports/RPT_SR_Excel.js"},
		]
	});
}

if (loginWindow.RD_REPORT_ENABLED()) {
	srTreeRoot.sub.add({categoryName:"Regional Desks",
		sub:[
			{categoryName:"Transcript Application ", js:"js/regionaldesk/transcript_application.js",},
			{categoryName:"CoR Granting Office (Summary)", js:"js/sr/reports/RPT-RD-001_CORGrantingOfficeSummary.js"},
			{categoryName:"Transcript Granting Office (Summary)", js:"js/sr/reports/RPT-RD-002_TranscriptGrantingOfficeSummary.js"},
		]
	});
}

var srTree = isc.Tree.create({
    modelType: "children",
    nameProperty: "categoryName",
    childrenProperty: "sub",
		root:srTreeRoot});

view.getMember(0).setData(srTree);