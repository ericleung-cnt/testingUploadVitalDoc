// -----------------------------------------------------------------------------
// ------------------ Page for update Crew	------------------------------------	
// -----------------------------------------------------------------------------

var g_crewList_editing_upload_result=false;
isc.DynamicForm.create({
	ID:"crewListDetailForm", dataSource:"crewListCoverDS", numCols: 6, cellBorder:0, width:700, 
	fields: [
		{name: "imoNo" }  ,
		{name: "shipName" }  ,
		{name: "regPort" } , 
		{name: "offcialNo" }  ,
		/*
			{name: "seafarerId", 		title: "Search Vessel", editorType: "comboBox", endRow: true, optionDataSource:"regMasterDS", displayField:"applNo", valueField:"applNo", endRow:true, completeOnTab:true,
				showIf:function(item, value, form, values){
					return form.getValue('version')==null;
				},
				 changed: function (form, item, value){
					 var record = item.getSelectedRecord();
					 if(record!=null){
						 var applNoValue = record.applNo;
						 var applNoSufValue = record.applNoSuf;
						 var imoNoValue = record.imoNo;
//						 seafarerDS.fetchData({'applNo':applNoValue, 'applNoSuf':applNoSufValue}, function(dsResponse, data, dsRequest){
//							 if (dsResponse.status == 0) {
//								 item.pickList.hide();
//								 var shipRecord = data[0];
//								 form.setValue('vesselId', 	shipRecord.applNo);
//							 }
//						 });
						 form.setValue('vesselId', 	applNoValue);
						 form.setValue('imoNo', 	imoNoValue);
					 }
				 },
				 pickListWidth:400,
				 layoutLeftMargin:0,
				 pickListFields: [
				                  {name:"applNo", 	width:70},
				                  {name:"applNoSuf", width:50},
				                  {name:"regName", width:"*"},
				                  {name:"regChiName", width:70}
				                  ]
				 
			},
			{name: "spacerItem", 	type:"SpacerItem", endRow:true, showIf:function(item, value, form, values){
				return form.getValue('version')==null;
				}
			}, 
	         
	         
	         {name: "vesselId", title: "Vessel Name", 	length:50}, 	
	         {name: "coverYymm", title:"Cover YYMM", 	length:6}, 	
	         {name: "imoNo"}, 	
	         {name: "commenceDate", title:"Date of Commencement", type:"date", wrapTitle:false, startRow:true}, 	
	         {name: "commencePlace", title:"Place of Commencement", wrapTitle:false, length:30}, 	
	         {name: "agreementPeriod", title:"Agreement Period (Month)", editorType: "spinner", length:2, writeStackedIcons:true, defaultValue:1, min:1, step:1, max:99}, 

	         {name: "terminateDate", title:"Date of Terminate", type:"date", startRow:true}, 	
	         {name: "terminatePlace", title:"Place of Terminate", length:30}, 	
	         
	         {name: "dgDesc", title:"DG Description", 		  colSpan:3, width:505, length:30, startRow:true}, 
	         {name: "docLocation", title:"Doucment location", colSpan:3, width:505, length:100, startRow:true}
//	         {name: "upload_btn", title:"Upload", type: "button", startRow: false, endRow: false}, 
//	         {name: "download_btn", title:"Download", type: "button", startRow: false, endRow: true}
*/
			 ]
});
	
var fetchedCrewList = isc.ListGrid.create({
	ID:"crewListDetailCrewList", 
	dataSource:"crewDS", 
	filterOnKeypress:true,
	alternateRecordStyles:true, 
	canHover:true,
	autoFitFieldWidths:true,
	minFieldWidth:50,
	showFilterEditor:true,
	filterOnKeypress:false,
	fields: [
			{name: "validationErrors" ,autoFitWidth:false,width:200, hidden:true}, 
			{name: "id", hidden:true  }, 
			{name: "imoNo", hidden:true  }, 
			{name: "referenceNo",  }, 
			{name: "crewName", width: 150 }, 
			{name: "serbNo", width: 100 }, 
			{name: "nationalityId", optionDataSource:"nationalityDS",valueField:"id",displayField:"engDesc", title: "Nationality" ,width:100},
			{name: "capacityId", optionDataSource:"rankDS",valueField:"id",displayField:"engDesc", title: "Capacity", /*dataPath:"capacity.engDesc",*/width:100},
			{name: "crewCert" ,width:100} ,
			{name: "currency" } ,
			{name: "salary", title: "Salary", format:",##0.00", type:"decimal" ,width:100} ,
			{name: "sex",  }, 
			{name: "birthDate",  }, 
			{name: "birthPlace",  }, 
			{name: "address",autoFitWidth:false,width:100 }  ,
			{name: "nokName" }  ,
			{name: "nokAddress",autoFitWidth:false,width:100 } , 
			{name: "employDate" }  ,
			{name: "employDuration" ,type:"decimal"} , 
			{name: "engageDate" ,width:100}  ,
			{name: "engagePlace" }  ,
			{name: "dischargeDate" }  ,
			{name: "dischargePlace" }  ,
			{name: "status" } ,
			{name: "nationalitybeforeMap" , hidden:true} ,
			{name: "capacityBeforeMap" ,hidden:true} ,
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
				var id = record.id;
		    	var serbNo = record.serbNo;
	        	var fetchParam = {"id":id};
				crewListCoverAddSeafarerDynamicForm.clearErrors(true);
        		crewListCoverAddSeafarerDynamicForm.setValues({});
	        	crewListCoverAddSeafarerDynamicForm.fetchData(fetchParam,function(res,data,req){
				});
				crewListCoverAddSeafarerDynamicForm.hideField("seafarerId");
		    	crewListCoverAddCrewWindow.show();
	         },
	         refresh:function(){
				 console.log("refrsh");
	        	 var imoNo = crewListDetailForm.getValue("imoNo");
	        	 crewListDetailCrewList.setData([]);
				 crewListDetailCrewList.fetchData({"imoNo":imoNo}
	
				  );
			 },	
			//  filterData: function (criteria, callback, requestProperties) {
			// 	 // fix In criteria, definition 'nationality.engDesc' refers to a related DataSource ('null') that does not exist.  Ignoring this criteria entry. 
			// 	if(criteria==null){
			// 		criteria={};
			// 	}
			// 	var newCri={};
			// 	for (let [key, value] of Object.entries(criteria)) {
			// 		var idx = key.indexOf(".");
			// 		if(idx!=-1){
			// 			newCri[key.slice(idx+1)] = value;
			// 		 }else{
			// 			 newCri[key] = value;
			// 		 }
			// 	 }
			// 	 console.log(criteria , newCri);  // the criteria of form datasource.field is be omitted, so cut it to field
			// 	this.fetchData(newCri);
			// 	this.setFilterEditorCriteria(criteria);
			// },
			 dataArrived: function (startRow, endRow) {
				this.Super('dataArrived', arguments);
			},
			getCellCSSText:function (record, rowNum, colNum){
				// console.log(record.validationErrors);
				if(record.validationErrors!=null &&record && record.validationErrors.indexOf("Error")!=-1){
					return "background-color:#FF6347;";
				}

				if(record.validationErrors!=null &&record && record.validationErrors.startsWith("Update")){
					return "background-color:#f1e740;";
				}
				if(record.validationErrors!=null && record && record.validationErrors.startsWith("New")){
					return "background-color:#98FB98;";
				}

				if(record.validationErrors!=null ){
					return "background-color:#FF6347;";
				}

		
	
	
			}
});

console.log("crew form");

isc.Window.create({
	ID:"crewListCoverAddCrewWindow",
	width: 800, isModal: true, showModalMask: true, height: 500, title: "Crew List of Agreement",
	items: [ 
	        isc.VLayout.create({   
	        	padding: 10, 
	        	members: [ 
					isc.Label.create({
						width: "75%", height: 20,	align: "left", valign: "top", wrap: false,
						contents: "<p><b><font size=2px>Add Seafarer</font></b></p>"
					}),
					isc.DynamicForm.create({
						ID:"crewListCoverAddSeafarerDynamicForm",
						width: "100%",
						height:"*",
						dataSource:"crewDS",
						numCols: 4,
						cellBorder:0,
						wrapTitle :false,
						fields: [
							{name: "seafarerId", 		title: "Search HKID", type: "text", wrapTitle:false,type:"ComboBoxItem",  width: 200 ,hidden:true
							, optionDataSource:"seafarerSearchDS"
							, valueField:"id"
							, displayField:"id"
							, cachePickListResults: true
							, useClientFiltering: true
							,endRow:true
							, pickListFields:[
								{name:"id", width:70},
								{name:"serbNo", width:70},
								{name:"surname", width:70},
								{name:"firstName", width:"*"},
								{name:"nationalityId", hidden:false},
								{name:"birthDate", hidden:false},
								{name:"birthPlace", hidden:false},
								{name:"rankId", hidden:false},
								{name:"address1", hidden:false},
								{name:"address2", hidden:false},
								{name:"address3", hidden:false},
							]
							, pickListProperties: {
								showFilterEditor:true
								, alternateRecordStyles:true
							}	
							, pickListWidth:750,
								changed: function (form, item, value){
									var record = item.getSelectedRecord();
									console.log(record);
									if(record!=null){
										crewListCoverAddSeafarerDynamicForm.clearErrors(true);
										crewListCoverAddSeafarerDynamicForm.setValues({});
										var seafarerRecord = {};
										var promise=[]

										promise.push(new Promise((resolve, reject) => {
											seafarerDS.fetchData({id:record.id},function(res,dataArr,req){
												if(res.status != 0){
													reject();
												}
												if(dataArr.length>0){
													var data =dataArr[0] 
													seafarerRecord.crewName= data.firstName +  ' '+ data.surname ;
													seafarerRecord.serbNo= data.serbNo;
													seafarerRecord.nationalityId=data.nationalityId;
													seafarerRecord.birthDate=data.birthDate;
													seafarerRecord.birthPlace=data.birthPlace;
													seafarerRecord.capacityId=data.rankId;
													seafarerRecord.status=data.status;
													var address="" ;
													if(data.address1){
														address += data.address1 +" "
													}
													if(data.address2){
														address += data.address2 +" "
													}
													if(data.address3){
														address += data.address3 ;
													}
													seafarerRecord.address=address;
												}
												resolve();
	
											})
										}));
										promise.push(new Promise((resolve, reject) => {
											nextOfKinDS.fetchData({seafarerId:record.id},function(res,dataArr,req){
												if(res.status != 0){
													reject();
												}
												if(dataArr.length>0){
													var data =dataArr[0] 
													seafarerRecord.nokName= data.kinName;
													seafarerRecord.nokAddress= data.relation;
												}
												resolve();
											})
										}));
										promise.push(new Promise((resolve, reject) => {
											ratingDS.fetchData({seafarerId:record.id},function(res,dataArr,req){
												console.log(dataArr)
												if(res.status != 0){
													reject();
												}
												if(dataArr.length>0){
													var data =dataArr[0] 
													seafarerRecord.capacityId= data.capacityId;
												}
												resolve();
											})
										}));
										promise.push(new Promise((resolve, reject) => {
											certDS.fetchData({seafarerId:record.id},function(res,dataArr,req){
												console.log(dataArr)
												if(res.status != 0){
													reject();
												}
												if(dataArr.length>0){
													var data =dataArr[0] 
													seafarerRecord.crewCert= data.certType;
												}
												resolve();
											})
										}));

										Promise.all(promise)
										.then((rs) => {
											console.log("promises all resolve");
											console.log(seafarerRecord);
											crewListCoverAddSeafarerDynamicForm.setValues(seafarerRecord)
										})
										.catch((e) => {
											isc.say(e);
											console.err(e)
										});
										

										// form.setValue('seafarerName', 	seafarerRecord.surname+","+seafarerRecord.firstName);
											// form.setValue('serbNo', 		seafarerRecord.serbNo);
											// form.setValue('nationalityId', seafarerRecord.nationalityId);
											// form.setValue('birthDate', 	seafarerRecord.birthDate);
											// form.setValue('birthPlace', 	seafarerRecord.birthPlace);
											// form.setValue('address1', 		seafarerRecord.address1);
											// form.setValue('address2', 		seafarerRecord.address2);
											// form.setValue('address3', 		seafarerRecord.address3);
									}
						        },
						        	 
						         },
						         {name: "spacerItem", 	type:"SpacerItem", endRow:true}, 
						        //  {name: "vesselId", 	showIf:"false"}, 
						         {name: "imoNo", 	showIf:"false"}, 
								 {name: "version", 	showIf:"false"}, 
								 {name: "id",showIf:"false" }, 
						         
						         {name: "crewName", 	 },
						         {name: "serbNo", 		 		},
						         {name: "referenceNo", 		 		},
						         {name: "nationalityId", 	title: "Nationality", 	type: "SelectItem", required:true, optionDataSource:"nationalityDS", displayField:"engDesc", valueField:"id"},
						         {name: "status", 	required:true, endRow: true, canEdit:true},
								 {name: "crewCert", 		 			},
								 {name: "capacityId", 		title: "Capacity", 			type: "SelectItem", required:true, endRow: true, optionDataSource:"rankDS", displayField:"engDesc", valueField:"id"},

						         {name: "birthDate", 		
						        	//  title: "Birth Date", 	
						        	 type:"date", 
						        	 required:true,
						        	 validators:[
						        		 // alternate method for checking birthday
//						        	        { type:"custom", 
//						        	        	afterDate:new Date(),
//						        	        	condition:"value.getTime()>validator.afterDate.getTime()",						        	        	
//						        	            errorMessage:"Birth Date cannot be set to future date"
//						        	        }
						        	 	{ type:"dateRange",
						        	 		min:new Date("01/01/1900"),
						        	 		max:new Date(),
						        	 		errorMessage:"Birth Date cannot be set to future date"
						        	 	}
						        	 ]						        	 
						         },
						         {name: "birthPlace", 		},
								 {name: "employDate", 		 	type:"date"},
						         {name: "employDuration", 		},
						         {name: "engageDate", 		 	type:"date", required:true,startRow:true},
						         {name: "engagePlace", 		 	},


						         {name: "dischargeDate", 	startRow:true,
						        	//  title: "Discharge Date", 	
						        	 type:"date",
						        	 validators:[
						        		 { type:"custom",
						        			 condition: function(item, validator, value, record){
						        				 if (value && value instanceof Date){
													 return (value.getTime()>record.engageDate.getTime());
												 } return true;
						        			 },
						        			 errorMessage: "Discharge Date cannot be eariler than Engage Date"
						        		 }
						        	 ],
						         },
								 {name: "dischargePlace", 	 	},
								 {name: "nokName", 		 		startRow:true},
						         {name: "nokAddress", 					},
						        
								 {name: "currency", title: "Currency", length:5,addUnknownValues:true,  editorType:"comboBox",
								 valueMap:["HKD","CNY","USD","BGP","JPY"]
								//  valueMap:{"1.0":"HKD", "7.8":"USD"},
						        	//  changed: function (form, item, value){
						        	// 	 console.log("currency changed");
						        	// 	 var rSalary = form.getField("rSalary").getValue();
						        	// 	 if (rSalary!=null){
						        	// 		 form.getField("salary").setValue(parseFloat(value) * rSalary);
						        	// 	 }
						        	//  }
						         },
						        //  {name: "rSalary", 			title: "R Salary", 		type:"integer", decimalPrecision:8, decimalPad:0,
						        // 	 validators:[
						        // 	             {type:"integerRange", min:0, max:99999999}
						        // 	           ],
						        // 	 changed: function (form, item, value){
						        // 		 if (form.getField("currency")!=null){
						        // 			 form.getField("salary").setValue(value * parseFloat(form.getField("currency").getValue()));						        			 
						        // 		 }
						        // 	 }         
						        //  },
						         {name: "salary", format:",##0.00", type:"decimal", required:true,	//	type:"integer", decimalPrecision:8, decimalPad:0, required:true,
//						        	 validators:[
//						        	             {type:"integerRange", min:0, max:99999999}
//						        	           ]
						         },						         
						         {name: "address", 		 type:"textArea",		 colSpan:3, endRow:true, width:405},
						        //  {name: "crewListCover" ,hidden:true},
						        //  {name: "address2", 		title: " ", 			length:40, colSpan:3, endRow:true, width:405},
						        //  {name: "address3", 		title: " ", 			length:40, colSpan:3, endRow:true, width:405}
						         
						         ]
					}),
					
					
					isc.ButtonToolbar.create({
						ID:"crewListCoverAddSeafarerDynamicForm_ToolBar",
						buttons: [
						          {name:"addCrewSaveBtn", title:"Save", autoFit: true,
						        	  click : function () { 
						        		  console.log("save crew");
						        		  if(crewListCoverAddSeafarerDynamicForm.validate()){
						        			  var requestParam = {"operationType":"update"};
						        			  if(crewListCoverAddSeafarerDynamicForm.getValue('version')==null){
												  requestParam = {"operationType":"add"};
											  }
											  crewListCoverAddSeafarerDynamicForm.setValue("imoNo",crewListDetailForm.getValue("imoNo"));
											  console.log(crewListCoverAddSeafarerDynamicForm.getValues())
						        			  crewListCoverAddSeafarerDynamicForm.saveData(function(dsResponse, data, dsRequest) {
													if (dsResponse.status == 0) {
														isc.say(saveSuccessfulMessage);
														if(g_crewList_editing_upload_result){
															var found =crewListDetailCrewList.getData().find(o=>o.id==data.id);
															var validationErrors= found.validationErrors;
															Object.assign(found,data,{validationErrors:validationErrors});
															crewListDetailCrewList.markForRedraw();
														}else{
															crewListDetailCrewList.refresh();
														}
//														var demandNoteNo = mmoDNDetailDynamicForm.getValue('demandNoteNo');
//														mmoDNDetailDynamicForm.fetchData({"applNo":"0", "applNoSuf":"M", "demandNoteNo":demandNoteNo});
//														mmoDNDetailItemListGrid.setData([]);
//														mmoDNDetailItemListGrid.fetchData({"dnDemandNoteNo":demandNoteNo});
//
//														mmoDNAddFeeDynamicForm_ToolBar.getButton('addFeeSaveBtn').setDisabled(true);
													}
												}, requestParam);
						        		  }
						        	  }
						          },
						          {name:"closeBtn", title:"Close", autoFit: true,
						        	  click : function () { 
//						        		  mmoDNAddFeeDynamicForm_ToolBar.getButton('addFeeSaveBtn').setDisabled(false);
						        		  crewListCoverAddSeafarerDynamicForm.clearErrors(true);
						        		  crewListCoverAddSeafarerDynamicForm.setValues({});
						        		  crewListCoverAddCrewWindow.hide();
						        	  }
						          }, 
						          
						          ]
					})
					
					
					
					]
	        })
	        ]
});


isc.ButtonToolbar.create({
	ID:"crewListDetailToolBar",
	buttons: [
        {name:"saveBtn", title:"Save", autoFit: true, onControl:"MMO_CREATE|MMO_UPDATE",
        	click : function () { 
        		if(crewListDetailForm.validate()){
        			var requestParam = {"operationType":"update"};
      			  	if(crewListDetailForm.getValue('version')==null){
      			  		requestParam = {"operationType":"add"};
      			  	}
      			  
      			  	crewListDetailForm.saveData(function(dsResponse, data, dsRequest) {
      			  		if (dsResponse.status == 0) {
      			  			isc.say(saveSuccessfulMessage);
//      			  			mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(false);
//      			  			mmoDNDetailSectionForm_ToolBar.getButton('cancelDNBtn').setDisabled(false);
      			  			crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(false);
      			  		}
      			  	}, requestParam);
        		}
	        }
	    },
	    {name:"addSeafarerBtn", title:"Add Seafarer", autoFit: true, onControl:"MMO_CREATE|MMO_UPDATE",
	    	click : function () {
	    		// var vesselIdValue = crewListDetailForm.getValue('vesselId');
	    		// var coverYymmValue = crewListDetailForm.getValue('coverYymm');
	    		// crewListCoverAddSeafarerDynamicForm.setValue("vesselId", vesselIdValue);
				// crewListCoverAddSeafarerDynamicForm.setValue("coverYymm", coverYymmValue);
				crewListCoverAddSeafarerDynamicForm.clearErrors(true);
				crewListCoverAddSeafarerDynamicForm.setValues({});
				crewListCoverAddSeafarerDynamicForm.showField("seafarerId");
				crewListCoverAddCrewWindow.show();
	    }},
        {name:"cancelBtn", title:"Close", autoFit: true,
        	click : function () {
        		crewListDetailForm.clearErrors(true);
        		crewListDetailForm.setValues({});
        		crewListDetailCrewList.setData([]);
        		crewListDetailWindow.close();
	        	}
	    }
	   ]
});	


var appBtns = isc.ButtonsHLayout.create({
	members :
 	 [
		isc.IExportButton.create({ listGrid: fetchedCrewList,click:function(){
			if(g_crewList_editing_upload_result){
				fetchedCrewList.exportClientData({ ignoreTimeout: true, "endRow": -1, exportAs: "xls", exportFilename: "crewList_upload_result", })
			}else{
				this.Super('click', arguments);
			}
		} }),
	 ],
});

isc.Window.create({
	ID:"crewListDetailWindow",
	width: 1000, isModal: true, showModalMask: true,
	height: 700, 
	title: "Crew List of Agreement Detail",
	show:function(){
		this.Super("show", arguments);
		var isReadOnly = loginWindow.CREW_LIST_OF_AGREEMENT_READ_ONLY();
		if(isReadOnly){
			crewListDetailToolBar.getButton('saveBtn').setDisabled(true);
			crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(true);
		}
	},
	items: [ 
		isc.HLayout.create(
			{members: [ 
			      isc.VLayout.create({
			    	  members:[
//			    	           isc.TitleLabel.create({contents: "<p><b><font size=2px>Crew List of Agreement Maintenance</font></b></p>"}),
			    	           isc.SectionStack.create({ID:"crewListDetaiSectionContent", membersMargin: 10,
									sections: [
										{title: "Vessel Information",   expanded: true , resizeable: false, items: [ crewListDetailForm, crewListDetailToolBar ]}, 
										{name:"seafarerInfoSection", title: "Seafarer Information", expanded: true , resizeable: false, items: [ crewListDetailCrewList, appBtns]}
										]
			    	           		})
			    	           	
			    	           ]
			      })
				 ]
		})  
       ]
});

function openCrewListDetail(record){
	console.log("openCrewListDetail", record);
	crewListDetailWindow.show();
	g_crewList_editing_upload_result=false;
	if(Array.isArray(record) ){
		crewListDetailForm.clearErrors(true);
		crewListDetailForm.setValues({});
		crewListDetailCrewList.setData([]);
		g_crewList_editing_upload_result=true;
		crewListDetailWindow.setTitle("Crew List of Agreement Detail (Editing upload excel result")
		if(record.length>0){
			var imoNo = record[0].imoNo;
			crewListDetailForm.fetchData({"imoNo":imoNo,},
			function (dsResponse, data, dsRequest) {
				crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(false);
				crewListDetailForm.getField('imoNo').setDisabled(true);
				crewListDetailCrewList.setData(record);
			});
			//		crewListDetaiSectionContent.showSection('seafarerInfoSection');
			crewListDetailCrewList.showField("validationErrors");
			crewListDetailCrewList.markForRedraw();
			crewListDetailCrewList.setDisabled(false);
		}
	}
	else if(record!=null){
		// Update
		crewListDetailForm.clearErrors(true);
		crewListDetailForm.setValues({});
		crewListDetailCrewList.setData([]);
		var imoNo = record.imoNo;
		var serbNo = record.serbNo;
		var crewid = record.crewId;
		crewListDetailForm.fetchData({"imoNo":imoNo,},
				function (dsResponse, data, dsRequest) {
					crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(false);
					crewListDetailForm.getField('imoNo').setDisabled(true);
					crewListDetailCrewList.refresh();
					//		crewListDetaiSectionContent.showSection('seafarerInfoSection');
					crewListDetailCrewList.setDisabled(false);
					if (crewid) {
						crewListDetailCrewList.rowDoubleClick({ "id": crewid });
					}
				});
	}else{
		// Create
		crewListDetailForm.clearErrors(true);
		crewListDetailForm.setValues({});
		crewListDetailCrewList.setData([]);
		crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(true);
		crewListDetailForm.getField('imoNo').setDisabled(false);
		// crewListDetailForm.getField('coverYymm').setDisabled(false);
//		crewListDetaiSectionContent.hideSection('seafarerInfoSection');
		crewListDetailCrewList.setDisabled(true);
	}
}
