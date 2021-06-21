// -----------------------------------------------------------------------------
// ------------------ Page for searching CrewListCover -------------------------	
// -----------------------------------------------------------------------------

isc.HLayout.create({
	ID:"crewListSearchHLayout",
	height:30, layoutMargin:10,
	members: [
				isc.SearchForm.create({
					ID:"crewListSearchForm", numCols: 4,  width:200, dataSource:"crewListCoverDS",
					saveOnEnter:true,
					submit:function(){
						crewListSearchFormToolBar.getButton('searchBtn').click();
					},
					fields: [
				         {name: "vesselId", 		title: "Vessel Name", type: "text", wrapTitle:false}, 
				         {name: "imoNo", 			type: "text", wrapTitle:false}/*, 
				         {name: "official_no", 		title: "Official No", type: "text", endRow: true}, //TODO
				         {name: "agreementPeriod", 	title: "Agreement Period (Month)", type: "text", endRow: true}*/
				        ]
				}),
				isc.ButtonToolbar.create({
					ID:"crewListSearchFormToolBar",
					width:180,
					buttons: [
				        {name:"searchBtn", title:"Search", autoFit: true,
				        	click : function () { 
				        		crewListSearchResultLG.setData([]);
								  var criteria = crewListSearchForm.getValuesAsCriteria(false);
								  crewListSearchResultLG.fetchData(criteria, function(dsResponse, data, dsRequest){
									  
								  });  
					        	}
					    }, 
				        {name: "newCrewListBtn", title: "New Crew List", autoFit: true, onControl:"MMO_CREATE",
				        	click : function () { 
				        		openCrewListDetail(null);
				        	 }
						},
					   ]
				}),

	          ]
});



isc.ListGrid.create({
	ID:"crewListSearchResultLG", 
	alternateRecordStyles:true, dataSource:"crewListCoverDS",
	showFilterEditor:true,
	filterOnKeypress:true,
	fields: [
         {name: "vesselId", title: "Vessel Name", width:100}, 
         {name: "imoNo",  width:100}, 
         {name: "coverYymm", width:100}, 
         {name: "commenceDate", width:110}, 
         {name: "commencePlace", width:100}, 
         {name: "terminateDate", width:100}, 
         {name: "terminatePlace", width:100}, 
         {name: "agreementPeriod", title: "Agreement Period(Month)", width:160}, 
         {name: "dgDesc", width:150}, 
         {name: "docLocation",  width:"*"} 
//         {name: "official_no", title: "Official No"},
//         {name: "reg_port", title: "Port of Registry", type: "text", wrap: true }, 
	    ], 
	    rowDoubleClick:function(record, recordNum, fieldNum){
	    	openCrewListDetail(record);
	    }
});

isc.HLayout.create({
	ID: "crewListUploadHLayout",
	height: 30, 
	// layoutMargin: 10,
	members: [
		isc.SearchForm.create({
			ID: "crewListUploadForm",
			numCols: 4,
			width: 50,
			dataSource: "crewListCoverDS",
			fields: [
				{ type: "blob", name: "excelData", showTitle: false, canEdit: true, accept: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			]
		}),

		isc.ButtonToolbar.create({
			ID: "crewListUploadFormToolBar",
			width: 50,
			buttons: [
				{
					name: "upload", title: "Upload", startRow: false, autoFit: true, onControl: "MMO_CREATE",
					click: function () {
						var requestParam = {
							"operationType": "update",
							"operationId": "UPLOAD_EXCEL",
							"willHandleError":true
						};
						// !TODO: ADD upload function
						if(crewListUploadForm.getValue("excelData")==null){
							console.log("Please Select A file");
							return;
						}
						crewListUploadForm.saveData(function (dsResponse, data, dsRequest) {
							if(dsResponse.status==0){

							}
							console.log("respose",dsResponse)
							console.log("data",data);
						},requestParam)
					}
				},
			]
		}),
	]
});


isc.HLayout.create({
	ID: "crewListMainLayout", 
	show:function(){
		this.Super("show", arguments);
		var isReadOnly = loginWindow.CREW_LIST_OF_AGREEMENT_READ_ONLY();
		if(isReadOnly){
			crewListSearchFormToolBar.getButton('newCrewListBtn').setDisabled(true);
		}
	},
	members: [ 
		isc.VLayout.create({ 
	    members: [ 
	          isc.TitleLabel.create({height:25, contents: "<p><b><font size=2px>Crew List of Agreement Maintenance</font></b></p>"}), 
	          isc.SectionStack.create({layoutTopMargin: 0,
	        		sections: [
	        	          {title: "Search", expanded: true, resizeable: false, items: [ crewListSearchHLayout ]},
						  {title: "Result", expanded: true, items: [ crewListSearchResultLG ]},
						  
	        		     ]

				}), 
				crewListUploadHLayout


		]
		})         
	 ]
});

