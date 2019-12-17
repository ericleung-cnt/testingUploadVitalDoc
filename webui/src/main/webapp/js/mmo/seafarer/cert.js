console.log("cert.js");
isc.DynamicForm.create({
	ID:"certDetailDynamicForm", dataSource: "certDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	hidden:"true"},
	         {name: "seqNo", 		hidden:"true"},

	         {name: "certType", title:"Cert Type",
	        	 optionDataSource:"certificateDS", displayField:"name", valueField:"name", allowEmptyValue:true,
	        	 width:200, length:40},
	         {name: "certNo", 			title:"Cert NO.", 		width:200, length:30},
	         {name: "issueDate", 		title:"Issue Date", 	type:"date"},
	         {name: "issueAuthority", 	title:"Issue Authority",
	        	 optionDataSource:"authorityDS", displayField:"name", valueField:"name", allowEmptyValue:true,
	        	 width:200}

	         ]
});


isc.ButtonToolbar.create({
	ID:"certDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (certDetailDynamicForm.validate()) {
	        			  certDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormCertLG.refresh();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true,
	        	  click : function () {
	        		  certDetailDynamicForm.setValues({});
	        		  certDetailDynamicForm.clearErrors(true);
	        		  certDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"certDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Cert",
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	certDetailDynamicForm,
				          	certDetailForm_ToolBar
				          ]
			})

	  ]
});

//--------  Cert Start ----------
isc.ListGrid.create({
	ID:"sfRecFormCertLG", height: "*", dataSource: "certDS",
	fields: [
	         {name: "seqNo", width:90},
	         {name: "certType", width:150},
	         {name: "certNo", 	width:150},
	         {name: "issueDate", width:90},
	         {name: "issueAuthority", width:"*"}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerCert(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormCertLG_ToolBar",
	buttons: [
	          {name:"refreshBtn", title:"Refresh", autoFit: true,
	        	  click : function () {
	        		   sfRecFormCertLG.refresh();
	        	  	}
	          },
	          {name:"createBtn", title:"Create", autoFit: true,
					click : function () {
						openSeafarerCert(null);
					}
		      },
	          ]
});
isc.SectionVLayout.create({ID:"seafarerCertLayout", height:180, members: [sfRecFormCertLG, sfRecFormCertLG_ToolBar ]});

function openSeafarerCert(record){
	certDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		certDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		certDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}
//--------  Cert End ----------