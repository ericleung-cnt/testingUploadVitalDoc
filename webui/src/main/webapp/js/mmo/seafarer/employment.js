console.log("employment.js");
isc.DynamicForm.create({
	ID:"employmentDetailDynamicForm", dataSource: "employmentDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	showIf:"false"},
	         {name: "seqNo", 		showIf:"false"},

	         {name: "companyName", 		title:"Company Name", 	width:"*", colSpan:3, length:50},
	         {name: "listingDate", 		title:"Listing Date", 	type:"date"},
	         {name: "cancelDate", 		title:"Cancel Date", 	type:"date"}
	         ]
});


isc.ButtonToolbar.create({
	ID:"employmentDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (employmentDetailDynamicForm.validate()) {
	        			  employmentDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormEmploymentLG.refresh();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true,
	        	  click : function () {
	        		  employmentDetailDynamicForm.setValues(null);
	        		  employmentDetailDynamicForm.clearErrors(true);
	        		  employmentDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"employmentDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Employment",
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	employmentDetailDynamicForm,
				          	employmentDetailForm_ToolBar
				          ]
			})

	  ]
});

//--------  Start ----------
isc.ListGrid.create({
	ID:"sfRecFormEmploymentLG", height: "*", dataSource: "employmentDS",
	fields: [
	         { name: "seqNo", width: 90 },
	         { name: "companyName", width: "*" },
	         { name: "listingDate", width: 120 },
	         { name: "cancelDate", width: 120 }
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerEmployment(record);
		     },
	         refresh: function (){
	        	 sfRecFormEmploymentLG.setData([]);
	        	 sfRecFormEmploymentLG.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
         }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormEmploymentLG_ToolBar",
	buttons: [
	      {name:"refreshBtn", title:"Refresh", autoFit: true,
	        	  click : function () {
	        		  sfRecFormEmploymentLG.refresh();
	        	  }
	      },
	      {name:"createBtn", title:"Create", autoFit: true,
				click : function () {
					openSeafarerEmployment(null);
				}
	      },
          ]
});

isc.SectionVLayout.create({ID:"seafarerEmploymentLayout", height:180, members: [sfRecFormEmploymentLG, sfRecFormEmploymentLG_ToolBar ]});

function openSeafarerEmployment(record){
	employmentDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		employmentDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		employmentDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}