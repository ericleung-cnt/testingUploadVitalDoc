console.log("seaService.js");
isc.DynamicForm.create({
	ID:"seaServiceDetailDynamicForm",
	width: "100%",
	dataSource: "seaServiceDS",
	numCols: 4,
	fields: [
	         {name: "vesselName", 		title:"Vessel Name", 	required: true,	startRow: true,  width: 250, length:40,
	        	 validators:[
	        	             {type:"required"}
	        	             ]
	         },
	         {name: "flag", 			title:"Flag", 			width: 150, length:20},
//	         {name: "capacity",			title: "Capacity",   	width:250, 	length:30},
// changed on 2019.06.26 for choosing capacity from Rank DS	         
	         {name: "capacity", 	title:"Capacity", width:250, type:"text", optionDataSource:"rankDS", displayField:"engDesc", valueField:"engDesc", allowEmptyValue:true},
	         {name: "shipTypeCode", 	title: "Ship Type", 	width: 150,	type: "text",  	optionDataSource:"shipTypeDS", displayField:"stDesc", valueField:"id", allowEmptyValue:true},
	         {name: "voyageType", 		title:"Voyage Type", 	type: "radioGroup", required: true, vertical: true, startRow: true, valueMap: {"S":"Sea-Going", "R":"River-Trade"},
	        	 validators:[
	        	             {type:"required"}
	        	             ]

	         },
	         {name: "employmentDate", 	title:"Employment Date", 	type:"date", startRow: true},
	         {name: "empoymentPlace", 	title:"Employment Place", 	length:30, width: 200},
	         {name: "dischargeDate",	title:"Discharge Date", 	type:"date"},
	         {name: "dischargePlace", 	title:"Discharge Place", 	length:30,	width: 200},
	         {name: "reportDate", 		title:"Report Date", 		type:"date"},
	         {name: "dchpend", 			title: "dchpend",			width: 200},
	         {name: "diDate", 			title:"DI Date", 			type:"date"}
	       ],

});

isc.ButtonToolbar.create({
	ID:"seaServiceDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (seaServiceDetailDynamicForm.validate()) {
	        			  seaServiceDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormSeaServiceLG.refresh();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true,
	        	  click : function () {
	        		  seaServiceDetailDynamicForm.setValues({});
	        		  seaServiceDetailDynamicForm.clearErrors(true);
	        		  seaServiceDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"seaServiceDetailWindow", width: 800, height: 380, isModal: true, showModalMask: true, title: "Update Seafarer SeaService",
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	seaServiceDetailDynamicForm,
				          	seaServiceDetailForm_ToolBar
				          ]
			})

	  ]
});


isc.ListGrid.create({
	ID:"sfRecFormSeaServiceLG", height: "*", dataSource: "seaServiceDS",
	fields: [
	         { name: "seqNo", width: 120 },
	         { name: "vesselName", width: "*" },
	         { name: "flag", width: 140 },
	         { name: "capacity", width: 140 },
	         { name: "shipTypeCode", width: 140,	optionDataSource:"shipTypeDS", displayField:"stDesc", valueField:"id"},
	         { name: "employmentDate", width: 120 },
	         { name: "empoymentPlace", width: 120 },
	         { name: "dischargeDate", width: 120 },
	         { name: "dischargePlace", width: 120 }
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerSeaService(record);
		     },
	         refresh: function (){
	        	 sfRecFormSeaServiceLG.setData([]);
	        	 sfRecFormSeaServiceLG.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
         }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormSeaServiceLG_ToolBar",
	buttons: [
	      {name:"refreshBtn", title:"Refresh", autoFit: true,
	        	  click : function () {
	        		  sfRecFormSeaServiceLG.refresh();
	        	  }
	      },
	      {name:"createBtn", title:"Create", autoFit: true,
				click : function () {
					openSeafarerSeaService(null);
				}
	      },
          ]
});


isc.SectionVLayout.create({ID:"seafarerSeaServiceLayout", height:180, members: [ sfRecFormSeaServiceLG, sfRecFormSeaServiceLG_ToolBar ]});

function openSeafarerSeaService(record){
	seaServiceDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		seaServiceDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		seaServiceDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}