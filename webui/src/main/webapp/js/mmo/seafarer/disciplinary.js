console.log("disciplinary.js");
isc.DynamicForm.create({
	ID:"discDetailDynamicForm", dataSource: "disciplinaryDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	showIf:"false"},
	         {name: "seqNo", 		showIf:"false"},

	         {name: "recordDate", 	title:"Record Date", 		type:"date", endRow:true},
	         {name: "caseRefNo", 	title:"Case Ref NO.", 	colSpan:3, width:"*", length:40},
	         {name: "offenceDesc", 	title:"Description", 	colSpan:3, width:"*", length:60},
	         {name: "penalty", 		title:"Penalty", 		colSpan:3, width:"*", length:40}


	         ]
});


isc.ButtonToolbar.create({
	ID:"discDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (discDetailDynamicForm.validate()) {
	        			  discDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormDiscLG.refresh();
					        		  discDetailDynamicForm.setValues(null);
					        		  discDetailDynamicForm.clearErrors(true);
					        		  discDetailWindow.hide();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true,
	        	  click : function () {
	        		  discDetailDynamicForm.setValues(null);
	        		  discDetailDynamicForm.clearErrors(true);
	        		  discDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"discDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Disciplinary",
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	discDetailDynamicForm,
				          	discDetailForm_ToolBar
				          ]
			})

	  ]
});

isc.ListGrid.create({
	ID:"sfRecFormDiscLG",  height: "*", dataSource: "disciplinaryDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:90 },
	         { name: "recordDate",	width:90},
	         { name: "caseRefNo", width:150},
	         { name: "offenceDesc", width:"*", minWidth:200},
	         { name: "penalty", width:150}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerDisc(record);
		     },
	         refresh: function (){
	        	 discDetailDynamicForm.setData([]);
	        	 sfRecFormDiscLG.fetchData({seafarerId:sfRecFormDetail.getValue("id")});
	         }

});
isc.ButtonToolbar.create({
	ID:"sfRecFormDiscLG_ToolBar",
	buttons: [
	          { name:"refreshBtn", title:"Refresh", autoFit: true,
	        	  click : function () {
	        		  sfRecFormDiscLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", autoFit: true,
	        	  click : function () {
	        		  openSeafarerDisc(null);
	        	  }
	          },
	          ]
});

isc.SectionVLayout.create({ID:"seafarerDiscLayout", height:180, members: [sfRecFormDiscLG, sfRecFormDiscLG_ToolBar ]});


function openSeafarerDisc(record){
	discDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		discDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {});
	}else{
		//Create
		discDetailDynamicForm.getItem("seafarerId").setValue(sfRecFormDetail.getValue('id'));
	}
}