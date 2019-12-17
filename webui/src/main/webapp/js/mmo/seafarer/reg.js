console.log("reg.js");
//--------  Registration Start ----------
isc.DynamicForm.create({
	ID:"renewRegFormDetail", dataSource: "regDS", numCols: 2,	
	fields: [
	         { name: "seafarerId", showIf:"false"}, 
	         { name: "seqNo", showIf:"false"}, 
	         { name: "regDate", 	title: "Record Date", type:"date"}, 
	         { name: "regExpiry", 	title: "Expiry Date", type:"date"},
	         { name: "regCancel", 	title: "Cancel Date", type:"date"}
	         ]
});
	
isc.ButtonToolbar.create({
	ID:"renewRegForm_ToolBar", 
	membersMargin: 10,
	buttons: [
	          {name:"updateBtn", title:"Update", autoFit: true, onControl:"MMO_UPDATE", 
	        	  click : function () { 
	        		  if (renewRegFormDetail.validate()) {
							renewRegFormDetail.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRegListGrid.refresh();
								}
							}, {operationType:"update", operationId:"UPDATE_REG"});
						}
	        	  }
	          },
	          {name:"renewBtn", title:"Renew", autoFit: true, 
	        	  click : function () { 
	        		  if (renewRegFormDetail.validate()) {
	        			  renewRegFormDetail.saveData(function(dsResponse, data, dsRequest) {
	        				  if (dsResponse.status == 0) {
	        					  isc.say(saveSuccessfulMessage);
	        					  renewRegFormDetail.setValue(data);
	        					  sfRegListGrid.refresh();
	        				  }
	        			  }, {operationType:"add", operationId:"RENEW_REG"});
	        		  }
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true, 
	        	  click : function () { 
	        		  renewRegFormDetail.setValues({});
	        		  renewRegWindow.hide();
	        		  
	        	  }
	          }
	          
	          ]
});


// renew window
isc.Window.create({
	ID:"renewRegWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Renew Seafarer Registration",
	items: [ 
	     	isc.WindowVLayout.create({ 
				members: [ 
				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}), 
				          	renewRegFormDetail, 
				          	renewRegForm_ToolBar 
				          ]
			})
	        
	  ]
});

//---------------------------------------------

// Registration List
isc.ListGrid.create({
	ID:"sfRegListGrid", height: "*", dataSource: "regDS",
	fields: [
	         {name: "seafarerId", width: 120 },
	         {name: "seqNo", width: 120 },
	         {name: "regDate", width: 120 }, 
	         {name: "regExpiry", width: 120 },
	         {name: "regCancel", width: "*" }
	         ],
	         selectionUpdated:function(record, recordList){
	        	 if(record!=null){
	        		 var deleteBtn = sfRecFormRegLG_ToolBar.getButton('deleteBtn');
	        		 if(deleteBtn!=undefined){
	        			 deleteBtn.setDisabled(false);
	        		 }
	        	 }
	         },
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerReg(record);
	         },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')}, function(dsResponse, data, dsRequest) {
   				  if (dsResponse.status == 0) {
   					var deleteBtn = sfRecFormRegLG_ToolBar.getButton('deleteBtn');
	        		 if(deleteBtn!=undefined){
	        			 deleteBtn.setDisabled(true);
	        		 }
				  }
			  });
	         }
	         
});

isc.ButtonToolbar.create({
	ID:"sfRecFormRegLG_ToolBar", 
	buttons: [
	          {name:"refreshBtn", title:"Refresh", autoFit: true, 
	        	  click : function () { 
	        		  sfRegListGrid.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", autoFit: true, 
	        	  click : function () { 
	        		  openSeafarerReg(null);
	        		  
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", autoFit: true, disabled:true, 
	        	  click : function () { 
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  console.log('Confirm:'+value);
	        		  });
	        	  }
	          }
	      ]
});


isc.SectionVLayout.create({ID:"seafarerRegLayout", height:180, members: [sfRegListGrid, sfRecFormRegLG_ToolBar ]}); 
function openSeafarerReg(record){
	renewRegWindow.show();
	if(record!=null){
		// Update
		renewRegForm_ToolBar.getButton('updateBtn').show();
		renewRegForm_ToolBar.getButton('renewBtn').hide();
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		renewRegFormDetail.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		renewRegForm_ToolBar.getButton('updateBtn').hide();
		renewRegForm_ToolBar.getButton('renewBtn').show();
		renewRegFormDetail.setValues({});
		renewRegFormDetail.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}

//	--------  Registration End ----------