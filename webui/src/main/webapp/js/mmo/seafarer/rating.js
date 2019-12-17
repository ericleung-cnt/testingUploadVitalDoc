console.log("rating.js");
isc.DynamicForm.create({
	ID:"ratingDetailDynamicForm", dataSource: "ratingDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	hidden:"true"}, 
	         {name: "seqNo", 		hidden:"true"},
	         
	         {name: "rating", 		title:"Rank / Rating", 		width:"*", colSpan:3, length:40},
	         {name: "salary", 		title:"Salary", width:"*", length:8,
	        	 validators:[
		        	          {type:"isInteger"}
		                      ]
	         },
	         {name: "ratingDate", 	title:"Rating Date", 		type:"date"},
	         {name: "capacityId", 	title:"Capacity.", width:250, required:true, startRow:true, optionDataSource:"rankDS", displayField:"engDesc", valueField:"id", allowEmptyValue:false,
	        	 validators:[
		        	          {type:"required"}
		                      ]
	         }
	         
	         ]
});


isc.ButtonToolbar.create({
	ID:"ratingDetailForm_ToolBar", 
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE", 
	        	  click : function () { 
	        		  if (ratingDetailDynamicForm.validate()) {
	        			  ratingDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormRatingLG.refresh();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true, 
	        	  click : function () { 
	        		  ratingDetailDynamicForm.setValues({});
	        		  ratingDetailDynamicForm.clearErrors(true);
	        		  ratingDetailWindow.hide();
	        	  }
	          }
	          
	          ]
});

// create window
isc.Window.create({
	ID:"ratingDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Rating",
	items: [ 
	     	isc.WindowVLayout.create({ 
				members: [ 
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}), 
				          	ratingDetailDynamicForm, 
				          	ratingDetailForm_ToolBar 
				          ]
			})
	        
	  ]
});

//--------  Rating Start ----------
isc.ListGrid.create({
	ID:"sfRecFormRatingLG",  height: "*", dataSource: "ratingDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:80},
//	         { name: "capacityId", title: "Capacity ID", width:150 },
// changed on 2019.06.26 to show capacity desc instead of id	         
	         { name: "capacityId", title: "Capacity", width: 300,	optionDataSource:"rankDS", displayField:"engDesc", valueField:"id"},
	         { name: "rating", title: "Rank / Rating", width:"*", minWidth:200},
	         { name: "salary", title: "Salary", width:100 }, 
	         { name: "ratingDate", title: "Rating Date", width:90}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerRating(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }
});


isc.ButtonToolbar.create({
	ID:"sfRecFormRatingLG_ToolBar", createButtonsOnInit:true,
	buttons: [
	          { name:"refreshBtn", title:"Refresh", autoFit: true, 
	        	  click : function () { 
	        		  sfRecFormRatingLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", autoFit: true, 
	        	  click : function () { 
	        		  openSeafarerRating(null);
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
isc.SectionVLayout.create({ID:"seafarerRatingLayout", height:180, members: [sfRecFormRatingLG, sfRecFormRatingLG_ToolBar ]});

function openSeafarerRating(record){
	ratingDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		ratingDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		ratingDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}