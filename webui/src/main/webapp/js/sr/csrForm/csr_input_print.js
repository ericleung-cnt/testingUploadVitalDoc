console.log("maintain csr form");
var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Maintain CSR Form<br /></font></b></p>"
	});

var loadLastCsrFormSeq = function(form, imoNo) {
	if (imoNo && form.lastImo != imoNo) {
		form.disable();
		csrFormDS.fetchData({imoNo:imoNo}, function(resp, data, req){
			var seq = data != null ? data.formSeq : 0;
			form.getItem("lastFormSeq").setValue(seq);
			form.enable();
			form.getItem("formSeq").focusInItem();
			form.seqChanged();
		}, {operationId:"csrFormDS_fetchLastSeq"});
	}
	form.lastImo = imoNo;
};
var loadCsrForm = function(form, imoNo, formSeq) {
	csrFormDS.fetchData({imoNo:imoNo,formSeq:formSeq}, function(resp, data, req) {
		if (data.length == 1) {
			var lastFormSeq = form.getItem("lastFormSeq").getValue();
			form.setData(data[0]);
			form.getItem("lastFormSeq").setValue(lastFormSeq);

			var owners = [];
			data[0].owners.forEach(function(o) {
				o.Name = o.ownerName;
				o.Address = "";
				if (o.address1) {
					o.Address += o.address1 + " ";
				}
				if (o.address2) {
					o.Address += o.address2 + " ";
				}
				if (o.address1) {
					o.Address += o.address3 + " ";
				}
				o.Address = o.Address.trim();
			});
			form.ownerGrid.setData(data[0].owners);
			form.getItem("formApplyDate").focusInItem();
		} else {
			isc.showMessage("CSR Form Seq " + form.getItem("formSeq").getValue() + " does not exist");
			form.setCsrForm({});
		}
		form.seqChanged();
	});
};
var loadCsrFormSdData = function(form, sdData) {
	form.getItem("classSocietyId").setValue(sdData.classText);
	form.getItem("classSociety2").setValue(sdData.classText2);
	form.getItem("shipManager").setValue(sdData.shipManager);
	form.getItem("shipManagerAddress1").setValue(sdData.shipMgrAddr1);
	form.getItem("shipManagerAddress2").setValue(sdData.shipMgrAddr2);
	form.getItem("shipManagerAddress3").setValue(sdData.shipMgrAddr3);
	form.getItem("safetyActAddress1").setValue(sdData.safetyActAddr1);
	form.getItem("safetyActAddress2").setValue(sdData.safetyActAddr2);
	form.getItem("safetyActAddress3").setValue(sdData.safetyActAddr3);
	form.getItem("docAuthority").setValue(sdData.docAuthority);
	form.getItem("docAudit").setValue(sdData.docAudit);
	form.getItem("smcAuthority").setValue(sdData.smcAuthority);
	form.getItem("smcAudit").setValue(sdData.smcAudit);
	form.getItem("isscAuthority").setValue(sdData.isscAuthority);
	form.getItem("isscAudit").setValue(sdData.issccAudit);
	form.seqChanged();
};
var loadCsrFormRegMaster = function(form, imoNo) {
	regMasterDS.fetchData({imoNo:imoNo}, function(resp, data, req){
		if (!data.isEmpty()) {
			data.sortByProperty("regDate");
			var reg = data[data.length - 1];
			form.getItem("applNo").setValue(reg.applNo);
			form.getItem("registrationDate").setValue(reg.regDate);
			form.getItem("deregDate").setValue(reg.deRegTime);
			form.getItem("shipName").setValue(reg.regName);
			form.getItem("formApplyDate").setValue(new Date());
			form.getItem("daysLapsed").setValue(0);
			ownerDS.fetchData({applNo:reg.applNo}, function(ownerResp, owners, ownerReq){
				var gridData = [];
				owners.forEach(function(owner){
					var addr = "";
					if (owner.address1) {
						addr += owner.address1 + " ";
					}
					if (owner.address2) {
						addr += owner.address2 + " ";
					}
					if (owner.address3) {
						addr += owner.address3;
					}
					gridData.add({ Name:owner.name, Address:addr});
				});
				form.ownerGrid.setData(gridData);
				loadCsrFormSdData(form, {});
				form.getItem("formApplyDate").focusInItem();
				//loadCsrFormSdData(form, imoNo);
			});
		} else {
			form.setData({});
			form.ownerGrid.setData([]);
			form.getItem("imoNo").focusInItem();
		}
		form.seqChanged();
	});

};

//var defaultUpdateBtnAccessibility = function(){
//	var seq = form.getField("formSeq").getValue();
//	var lastSeq = form.getField("lastFormSeq").getValue();
//	var readOnly = (seq && lastSeq && seq < lastSeq);
//	return readOnly;
//}

var openCsrForm = function(record, recordNum, fieldNum){
	var csrFormEdit = isc.DynamicForm.create({
		colWidths:[120, 200, 120, 200],
		width: "100%", dataSource: "csrFormDS", saveOperationType:"add", numCols: 4,
		lastImo:"",
		ownerGrid: null,
		fields: [
			{ name:"imoNo", title:"IMO No", keyDown:
				function(item,form,keyName){
				if (["Tab", "Enter"].contains(keyName)) {
					loadLastCsrFormSeq(form, form.getItem("imoNo").getValue());
				}
				return true;
			  }, colSpan:3 },
			{ name: "revise_ind", title: "Revise CSR", type: "boolean", endRow: true,
				  change: function(form, item, indValue){
					  if (indValue==true) {
						  var answer = isc.confirm("Are you going to revise this CSR?",
								  function(value) {
						  			if (value==true) {
						  				form.getField('applNo').setDisabled(!indValue);
						  				form.getField('registrationDate').setDisabled(!indValue);
						  				form.getField('deregDate').setDisabled(!indValue);
						  				form.getField('shipName').setDisabled(!indValue);
						  				form.getField('inputDate').setDisabled(!indValue);
										form.getField('applicantName').setDisabled(!indValue);
										form.getField('applicantEmail').setDisabled(!indValue);
						  				btnUpdate.enable();
						  				btnEditOwner.enable();
						  			} else {
						  				form.getField('revise_ind').setValue(!indValue);
						  			}
					  			});
					  } else {
						  form.getField('applNo').setDisabled(true);
						  form.getField('registrationDate').setDisabled(true);
						  form.getField('deregDate').setDisabled(true);
						  form.getField('shipName').setDisabled(true);
						  form.getField('inputDate').setDisabled(true);
						  var seq = form.getField("formSeq").getValue();
						  var lastSeq = form.getField("lastFormSeq").getValue();
						  var readOnly = (seq && lastSeq && seq < lastSeq);
						  if (readOnly == true) {
							  btnUpdate.disable();
							  form.getField('applicantName').setDisabled(true);
							  form.getField('applicantEmail').setDisabled(true);
							  //btnEditOwner.disable();
						  }
					  }
				  } },
			{ name:"formSeq", keyDown:
				function(item,form,keyName){
				if (["Tab", "Enter"].contains(keyName)) {
					var imoNo = form.getItem("imoNo").getValue();
					var formSeq = form.getItem("formSeq").getValue();
					console.log("formSeq = " + formSeq);
					if (isNaN(formSeq) || formSeq == "") {
						item.setValue("");
					} else if (formSeq <= form.getItem("lastFormSeq").getValue()) {
						loadCsrForm(form, imoNo, formSeq);
					} else {
						loadCsrFormRegMaster(form, imoNo);
					}
					form.seqChanged();
				}
				return true;
			  }, },
			{ name:"lastFormSeq", title:"Last Form Seq", type:"staticText" },
			{ name:"formApplyDate", changed:function(form,item,value) {
				if (value != null) {
					form.getItem("daysLapsed").setValue(((new Date().getTime() - value.getTime() ) / 1000 / 3600 / 24).toFixed());
				}
			}, dateFormatter:"dd/MM/yyyy",},
			// days lapsed
			{ name:"daysLapsed", title:"Days Lapsed", type:"staticText" },
			{ name:"applNo", type:"text", disabled: true },
			{ name:"registrationDate", type:"date", format:"dd/MM/yyyy", disabled: true },
			{ name:"deregDate", type:"date", format:"dd/MM/yyyy", disabled: true },
			{ name:"shipName", type:"text", disabled: true },

			{ name:"formerFlag", title:"Former Flag", type:"staticText"},
			//{ name:"editOwners", title:"Edit Owners", type:"button", endRow:true},
			{ name:"Owners", title:"Owners", type:"canvas", colSpan:4, width:500 },
			{ name:"classSocietyId", title:"Class Society", type:"staticText" },
			{ name:"classSociety2", title:"2nd Class", type:"staticText" },
			{ name:"shipManager", type:"staticText" },
			{ name:"shipManagerAddress1", title:"Ship Manager Address", type:"staticText", colSpan:4 },
			{ name:"shipManagerAddress2", title:"", type:"staticText", colSpan:4 },
			{ name:"shipManagerAddress3", title:"", type:"staticText", colSpan:4 },
			{ name:"safetyActAddress1", title:"Safety Manager Address", type:"staticText", colSpan:4 },
			{ name:"safetyActAddress2", title:"", type:"staticText", colSpan:4 },
			{ name:"safetyActAddress3", title:"", type:"staticText", colSpan:4 },
			{ name:"docAuthority", type:"staticText" },
			{ name:"docAudit", type:"staticText" },
			{ name:"smcAuthority", type:"staticText" },
			{ name:"smcAudit", type:"staticText" },
			{ name:"isscAuthority", type:"staticText" },
			{ name:"isscAudit", type:"staticText" },
			{ name:"imoOwnerId"},
			{ name:"imoCompanyId"},
			{ name:"remark", colSpan:4, width:"*", characterCasing: "upper" },
			{ name:"copyRemark", title:"Copy Remark", type:"button",
				click:function(){
					openCopyRemark(function(record){
						console.log(record);
						console.log(record.remark);
						csrFormEdit.getItem("remark").setValue(record.remark);
					});
				}
			},
			{ name:"applicantName", title:"Applicant Name", type:"text", disabled:true},
			{ name:"applicantEmail", title:"Applicant Email", type:"text", disabled:true},
			{ type:"section", value:"<b>Progress</b>", width:700, itemIds:
				["formAccepted","fsqcConfirmed","portfolioReceived","srApproved",
				 "csrIssued","repInformed","csrCollected","revisedRequired",
				 "sqaUpdateRequired","formerFlagRequested","companyCopyReceived","formerFlagReminded",
				 ],
                  sectionExpanded:false},
          { name:"formAccepted" },
          { name:"fsqcConfirmed" },
          { name:"portfolioReceived" },
          // Approved on (send email)
          { name:"srApproved" },
          // Issued on (send email)
          { name:"csrIssued" },
          { name:"repInformed" },
          { name:"csrCollected" },
          { name:"revisedRequired", type:"checkbox" },
          { name:"sqaUpdateRequired", type:"checkbox" },
          { name:"formerFlagRequested", type:"checkbox" },
          { name:"companyCopyReceived", type:"checkbox" },
          { name:"formerFlagReminded", type:"checkbox" },
			{ type:"header", value:"Print",},
			{ name:"registrarName", title:"Authorized Person", optionDataSource:"registrarDS", valueField:"name", displayField:"name", type:"combo"},
	          { name:"paymentRequired", title:"Payment Required", type:"checkbox" },
			{ name:"inputDate", type:"Date", format:"dd/MM/yyyy", disabled: true },
			],
		setCsrForm: function(record) {
			 csrFormEdit.setData(record);
			 var owners = [];
			 if (record.owners) {
				 record.owners.forEach(function (o) {
					 var addr = (o.address1 ? o.address1 :"") + " " + (o.address2 ? o.address2 :"")+ " " + (o.address3 ? o.address3 :"");
					 owners.add({Name:o.ownerName, Address: addr});
					 });
			 }
			 csrFormEdit.ownerGrid.setData(owners);
			 csrFormEdit.lastImo = null;
			 csrFormEdit.getItem("imoNo").keyDown(csrFormEdit.getItem("imoNo"), csrFormEdit, "Tab");
			 csrFormEdit.getItem("formApplyDate").changed(csrFormEdit,csrFormEdit.getItem("formApplyDate"), csrFormEdit.getItem("formApplyDate").getValue());
		},
		seqChanged:function() {
			console.log("seq changed");
			var seq = this.getItem("formSeq").getValue();
			var lastSeq = this.getItem("lastFormSeq").getValue();
			var readOnly = (seq && lastSeq && seq < lastSeq);
			btnEdit.setDisabled(readOnly);
			btnUpdate.setDisabled(readOnly);
			btnEditOwner.disable();
		},
		}
		);
	 var ownerGrid = isc.ListGrid.create({
		 width:500,
		 fields:[
			 {name:"Name", width:200},
			 {name:"Address", width:"*"}
			 ]
	 });

	 csrFormEdit.ownerGrid = ownerGrid;
	 csrFormEdit.getItem("Owners").canvas.addChild(ownerGrid);
	 csrFormEdit.setCsrForm(record);

	 var btnEdit = isc.Button.create({
		 width:100,
		 title:"Edit SD Data",
		 click:function(){
			 openSdData(
					 csrFormEdit.getItem("imoNo").getValue(),
					 function(sdData){ loadCsrFormSdData(csrFormEdit, sdData); });
		 }
	 });

	 var btnEditOwner = isc.Button.create({
		 title:"Edit Owner",
		 click:function(){
			 console.log("edit owner");
			 console.log("imo:" & csrFormEdit.getItem("imoNo").getValue);
			 openCsrOwner(csrFormEdit.getItem("imoNo").getValue(), csrFormEdit.getItem("formSeq").getValue());
		 }
	 });

	 var btnUpdate = isc.Button.create({
		 width:100,
		 title:"Update", click : function (form) {
			 console.log("update button click");
			 if (csrFormEdit.validate()) {
				 tb.disable();
				 var data = csrFormEdit.getData();
				 var payment =csrFormEdit.getField("paymentRequired").getValueAsBoolean();
				 data.owners = []; // Can't convert value of type java.lang.String to target type org.mardep.ssrs.domain.sr.CsrFormOwner
				 csrFormDS.updateData(data, function(resp, data){
					 data["paymentRequired"] = payment;
					 csrFormEdit.setCsrForm(data);
					 ReportViewWindow.displayReport(["CSRForm", data]);
					 tb.enable();
				 }) ;
				 csrFormEdit.getField('applNo').setDisabled(true);
				 csrFormEdit.getField('registrationDate').setDisabled(true);
				 csrFormEdit.getField('deregDate').setDisabled(true);
				 csrFormEdit.getField('shipName').setDisabled(true);
				 csrFormEdit.getField('inputDate').setDisabled(true);
				 csrFormEdit.getField('applicantName').setDisabled(true);
				 csrFormEdit.getField('applicantEmail').setDisabled(true);
				 btnEditOwner.disable();
			 }
		 },
	 });

	 var email = function() {
		 var data = csrFormEdit.getData();
		 data.owners = [];
		 csrFormDS.addData(data, function(resp, data, req) {
			 isc.say("Email sent");
		 }, {operationType:"add",operationId:this.operationId});
	 };
	 var btnMissingDoc = isc.Button.create({
		 width:300,
		 title:"Email Owner to submit CSR missing document",
		 click: email,
		 operationId:"emailSubmitCSRMissingDoc"});
	 var btnCollect = isc.Button.create({
		 width:300,
		 title:"Email Owner to collect generated CSR Document",
		 click: email,
		 operationId:"emailOwnerCollectCSR"});
	 var btnPfl = isc.Button.create({
		 width:250,
		 title:"Email Owner to submit CSR Profolio",
		 click: email,operationId:"emailSubmitCSRProfolio"});
 	 var tb = isc.VLayout.create(
			 {
				 width:"*",
				 members:
					 [
					  isc.HLayout.create({ width:"*", height:22, members:[btnEdit, btnEditOwner, btnUpdate, btnMissingDoc] }),
					  isc.HLayout.create({ width:"*", height:22, members:[btnCollect,btnPfl]}),
					  isc.HLayout.create({ width:"*", height:22,
						  members:[isc.Button.create({ width:100,title:"Close", click: function() {localWin.close();}})]
					  }),
					  ]
			 }
			 );

	 csrFormEdit.buttons = tb;
	 var localWin = isc.Window.create({
			width: 780, height: 900, isModal: false, showModalMask: true, title: "CSR Form",
			items: [
			     	isc.VLayout.create({
						width: "100%",	height: "100%", padding: 10,
						members: [ csrFormEdit, tb],
					})
			  ],
			close: function(){ localWin.markForDestroy(); },
		});
	 localWin.show();
	 return csrFormEdit;
};


var searchSection =
	isc.ListGrid.create({
		dataSource : "csrFormDS",
		showFilterEditor:true,
		fields: [
			{ name:"imoNo", width:120 },
			{ name:"formSeq", width:120, align:"left" },
			{ name:"applNo", width:120 },
			{ name:"formApplyDate", width:120 },
			{ name:"Days Lapsed", width:120, formatCellValue:function(value, record, rowNum, colNum) {
				return record.formApplyDate? ((new Date().getTime() - record.formApplyDate.getTime() ) / 1000 / 3600 / 24).toFixed() : "";
			} },
			{ name:"registrationDate", width:120 },
			{ name:"shipName", width:120 },
			{ name:"classSocietyId", width:120 },
			{ name:"classSociety2", width:60 },
			{ name:"registrarName", width:120 },
			{ name:"shipManager", width:120 },
			{ name:"shipManagerAddress1", width:120 },
			{ name:"shipManagerAddress2", width:120 },
			{ name:"shipManagerAddress3", width:120 },
			{ name:"safetyActAddress1", width:120 },
			{ name:"safetyActAddress2", width:120 },
			{ name:"safetyActAddress3", width:120 },
			{ name:"docAuthority", width:60 },
			{ name:"docAudit", width:60 },
			{ name:"smcAuthority", width:60 },
			{ name:"smcAudit", width:60 },
			{ name:"isscAuthority", width:60 },
			{ name:"isscAudit", width:60  },
			{ name:"deregDate", width:120 },
			{ name:"csrIssueDate", width:120 },
			{ name:"remark", width:120 },
			{ name:"csrRemarks", width:120 },
		 ],
		 rowDoubleClick:openCsrForm
});
searchSection.fetchData();

var btns = isc.ButtonsHLayout.create({
	members : [
		isc.IAddButton.create({ click:"openCsrForm({})"}),
		isc.IExportButton.create({ listGrid: searchSection }),
	]
});

var contentLayout =
	isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
    members: [ sectionTitle, searchSection, btns ]
});

// start of opemCsrOwner 2019.07.08
var openCsrOwner = function(imoNo, seq){
	var csrOwnerList = isc.ListGrid.create({
		ID:"csrOwnerList",
		dataSource: "csrOwnerDS",
		fields:[
			{ name:"ownerType", title:"Type", width:50 },
			{ name:"ownerName", title:"Name", width:100 },
			{ name:"address1", title:"Address1", width:200},
			{ name:"address2", title:"Address2", width:200},
			{ name:"address3", title:"Address3", width:200}
		],
		rowClick : function(record, recordNum, fieldNum){
			csrOwnerForm.editRecord(record);
		}
	});
	var csrOwnerForm = isc.DynamicForm.create({
		ID:"csrOwnerForm",
		dataSource:"csrOwnerDS",
		width:"*",
		fields:[
			{ name:"ownerName", title:"Name", type:"text", width:100},
			{ name:"ownerType", title:"Type", type:"staticText"},
			{ name:"address1", title:"Address", type:"text", width:400 },
			{ name:"address2", title:"",  type:"text", width:400 },
			{ name:"address3", title:"",  type:"text", width:400 }
		]
	});
	var csrOwnerForm_BtnToolbar = isc.ButtonToolbar.create({
		ID:"csrOwnerForm_BtnToolbar",
		buttons: [
			{ name:"save", title:"Save", width:50,
				click:function(){
					csrOwnerForm.saveData(function(dsResponse, data, dsRequest) {
						if (dsResponse.status == 0) {
							isc.say(saveSuccessfulMessage);
							csrOwnerList.refresh();
						}
					}, {operationType:"update"});
				}
			},
			{ name:"close", title:"Close", width:50,
				click:function(){
					ownerWindow.close();
				}
			}
		]
	});
	var ownerWindow = isc.Window.create({
		ID:"csrOwnerWindow",
		title:"CSR Owner Revise",
		width:800,
		height:500,
		isModal:true,
		items:[
			isc.VLayout.create({
				members:[
					csrOwnerList,
					csrOwnerForm,
					csrOwnerForm_BtnToolbar
				]
			})
		],
		close: function(){ ownerWindow.markForDestroy(); },
	});
	ownerWindow.show();
	console.log("set data");
	csrOwnerList.setData([]);
	console.log("fetchData");
	csrOwnerList.fetchData({"imoNo":imoNo, "formSeq":seq.toString()});
	return ownerWindow;
}
// end of openCsrOwner 2019.07.08

// start of openCopyRemark 2019.07.09
var openCopyRemark = function(callback) {
	console.log("open copy remark");
	var docRemarkList = isc.ListGrid.create({
		ID:"docRemarkList",
		dataSource: "documentRemarkDS",
		fields:[
			{ name:"remark", title:"remark"}
		]
	});
	var docRemark_BtnToolbar = isc.ButtonToolbar.create({
		ID:"docRemark_BtnToolbar",
		buttons: [
			{ name:"copy", title:"Copy", width:50,
				click:function(){
//					csrOwnerForm.saveData(function(dsResponse, data, dsRequest) {
//						if (dsResponse.status == 0) {
//							isc.say(saveSuccessfulMessage);
//							csrOwnerList.refresh();
//						}
//					}, {operationType:"update"});
					var record = docRemarkList.getSelectedRecord();
					console.log(record);
					callback(record);
				}
			},
			{ name:"close", title:"Close", width:50,
				click:function(){
					copyRemarkWindow.close();
				}
			}
		]
	});
	var copyRemarkWindow = isc.Window.create({
		ID:"docRemarkWindow",
		title:"CSR Document Remark",
		width:800,
		height:500,
		isModal:true,
		items:[
			isc.VLayout.create({
				members:[
					docRemarkList,
					docRemark_BtnToolbar
				]
			})
		],
		close: function(){ copyRemarkWindow.markForDestroy(); },
	});
	copyRemarkWindow.show();
	docRemarkList.fetchData({"remarkGroup":"CSR"});
	return copyRemarkWindow;	
}
// end of openCopyRemark 2019.07.09

