console.log("medical.js");
isc.DynamicForm.create({
	ID:"medicalDetailDynamicForm", dataSource: "medicalDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", showIf:"false"}, 
	         {name: "seqNo", showIf:"false"},
	         
	         {name: "clinicNo", 	title:"Clinic NO.", width:250, optionDataSource:"clinicDS", displayField:"name", valueField:"id", allowEmptyValue:true
	         },
	         {name: "examDate", 	title:"Exam Date", type:"date"},
	         {name: "examPlace", 	title:"Exam Place", width:250, length:30},
	         {name: "expiryDate", 	title:"Expiry Date", type:"date"}, 
	         {name: "medicalRemark", 	title:"Medical Remark", width:"*", 	colSpan:3, length:60},
	         {name: "doctorRemark", 	title:"Doctor Remark",	width:"*", 	colSpan:3, length:60},
	         {name: "xrayRemark", 		title:"X-RAY Remark", 	width:"*", 	colSpan:3, length:60}
	         
	         
	         ]
});


isc.ButtonToolbar.create({
	ID:"medicalDetailForm_ToolBar", 
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE", 
	        	  click : function () { 
	        		  if (medicalDetailDynamicForm.validate()) {
	        			  medicalDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormMedicalLG.refresh();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true, 
	        	  click : function () { 
	        		  medicalDetailDynamicForm.setValues(null);
	        		  medicalDetailDynamicForm.clearErrors(true);
	        		  medicalDetailWindow.hide();
	        	  }
	          }
	          
	          ]
});

// create window
isc.Window.create({
	ID:"medicalDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Medical",
	items: [ 
	     	isc.WindowVLayout.create({ 
				members: [ 
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}), 
				          	medicalDetailDynamicForm, 
				          	medicalDetailForm_ToolBar 
				          ]
			})
	        
	  ]
});

//	--------  Medical Start ----------
isc.ListGrid.create({
	ID:"sfRecFormMedicalLG", height: "*", dataSource:"medicalDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:90},
	         { name: "examDate", width:90}, 
	         { name: "examPlace", width:150}, 
	         { name: "expiryDate", width:90},
	         { name: "doctorRemark", width:"*", minWidth:150}, 
	         { name: "xrayRemark", width:150}, 
	         { name: "medicalRemark", width:150}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerMedical(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }
	         
});

isc.ButtonToolbar.create({
	ID:"sfRecFormMedicalLG_ToolBar",
	buttons: [
	          {name:"refreshBtn", title:"Refresh", autoFit: true, 
	        	  click : function () { 
	        		  sfRecFormMedicalLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", autoFit: true, 
	        	  click : function () { 
//	        		  TODO
	        		  openSeafarerMedical(null);
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", autoFit: true, disabled:true, 
	        	  click : function () { 
	        		  isc.confirm(promptDeleteMessage, function(value){
//	        		  TODO
	        		  });
	        	  }
	          }
	          
	          ]
});
isc.SectionVLayout.create({ID:"seafarerMedicalLayout", height:180, members: [sfRecFormMedicalLG, sfRecFormMedicalLG_ToolBar ]});
function openSeafarerMedical(record){
	medicalDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		medicalDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		medicalDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}
//--------  Medical End ----------