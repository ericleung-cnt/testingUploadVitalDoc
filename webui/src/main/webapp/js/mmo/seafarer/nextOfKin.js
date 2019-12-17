console.log("nextOfKin.js");
// Next of Kin
isc.DynamicForm.create({
	ID:"nextOfKinDetailDynamicForm",  numCols: 4, dataSource: "nextOfKinDS",
	fields: [
	         { name: "kinName", 	title:"Name", 			width: 200},
	         { name: "kinChiName", 	title:"Chinese Name", 	width: 200},
	         { name: "kinHkid", 	title:"HKID", 			width: 120},
	         { name: "relation", 	title:"Relationship", 	width: 120},
	         { name: "marriageCertNo", title:"Marriage Cert No.", width: 120},
	         { name: "telephone", 	title:"Telephone", 	width: 120},
	         { name: "addr1", title: "Address", colSpan: 3, width: 400, startRow: true}, 
	         { name: "addr2", title: " ", colSpan: 3, width: 400, startRow: true}, 
	         { name: "addr3", title: " ", colSpan: 3, width: 400, startRow: true}
	         ],
	         refresh: function(){
	        	 this.setValues({});
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')})
	         }
});

isc.ButtonToolbar.create({
	ID:"nextOfKinDetailForm_ToolBar", 
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE", 
	        	  click : function () { 
	        		  if (nextOfKinDetailDynamicForm.validate()) {
	        			  nextOfKinDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormNextOfKinLG.refresh();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true, 
	        	  click : function () { 
	        		  nextOfKinDetailDynamicForm.setValues({});
	        		  nextOfKinDetailDynamicForm.clearErrors(true);
	        		  nextOfKinDetailWindow.hide();
	        	  }
	          }
	          
	          ]
});

// create window
isc.Window.create({
	ID:"nextOfKinDetailWindow", width: 600, height: 300, isModal: true, showModalMask: true, title: "Update Seafarer Next Of Kin",
	items: [ 
	     	isc.WindowVLayout.create({ 
				members: [ 
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}), 
				          	nextOfKinDetailDynamicForm, 
				          	nextOfKinDetailForm_ToolBar 
				          ]
			})
	        
	  ]
});




//--------  NextOfKin Start ----------
isc.ListGrid.create({
	ID:"sfRecFormNextOfKinLG",  height: "*", dataSource: "nextOfKinDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:80},
	         { name: "kinName", 	width:150}, 
	         { name: "kinChiName", 	width:120},
	         { name: "kinHkid", 	width:120}, 
	         { name: "relation", 	width:140}, 
	         { name: "marriageCertNo", 	width:140}, 
	         { name: "telephone", 		width:180}, 
	         { name: "addr1", 			width:'*'}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerNextOfKin(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }
});


isc.ButtonToolbar.create({
	ID:"sfRecFormNextOfKinLG_ToolBar",
	buttons: [
	          { name:"refreshBtn", title:"Refresh", autoFit: true, 
	        	  click : function () { 
	        		  sfRecFormNextOfKinLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", autoFit: true, 
	        	  click : function () { 
//	        		  TODO
	        		  openSeafarerNextOfKin(null);
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", autoFit: true, disabled:true, 
	        	  click : function () { 
	        		  isc.confirm(promptDeleteMessage, function(value){
//	        			TODO
	        		  });
	        	  }
	          }
	          
          ]
});
isc.SectionVLayout.create({ID:"seafarerNextOfKinLayout", height:180, members: [sfRecFormNextOfKinLG, sfRecFormNextOfKinLG_ToolBar ]});

function openSeafarerNextOfKin(record){
	nextOfKinDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		nextOfKinDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		nextOfKinDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}

