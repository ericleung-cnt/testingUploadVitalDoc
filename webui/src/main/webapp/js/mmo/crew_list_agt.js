// -----------------------------------------------------------------------------
// ------------------ Page for searching CrewListCover -------------------------	
// -----------------------------------------------------------------------------

isc.HLayout.create({
	ID:"crewListSearchHLayout",
	height:30, layoutMargin:10,
	members: [
				isc.SearchForm.create({
					ID:"crewListSearchForm", numCols: 4,  width:200, dataSource:"crewDS",
					saveOnEnter:true,
					submit:function(){
						crewListSearchFormToolBar.getButton('searchBtn').click();
					},
					fields: [
				         {name: "imoNo", 		title: "Vessel Name", type: "text", wrapTitle:false,type:"ComboBoxItem",  width: 200 
						 , optionDataSource:"crewListCoverDS"
						 , valueField:"imoNo"
						 , displayField:"shipName"
						 , cachePickListResults: true
						 , useClientFiltering: true
						 , pickListFields:[
							 {name:"shipName",},
							 {name:"imoNo", },
							 {name:"offcialNo", },
							 {name:"regPort", },
						 ]
						 , pickListProperties: {
							 showFilterEditor:true
							 , alternateRecordStyles:true
						 }	
						 , pickListWidth:750
					     },
				         {name: "imoNo", 	type: "text", wrapTitle:false},
				         {name: "crewName", type: "text", wrapTitle:false},
				         {name: "serbNo",   type: "text"}, 
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
	alternateRecordStyles:true, 
	dataSource:"crewDS",
	showFilterEditor:true,
	filterOnKeypress:true,
	alternateRecordStyles:true, 
	canHover:true,
	autoFitFieldWidths:true,
	minFieldWidth:50,
	fields: [
        //  {name: "vesselId", title: "Vessel Name", width:100}, 
        //  {name: "imoNo",  width:100}, 
        //  {name: "coverYymm", width:100}, 
        //  {name: "commenceDate", width:110}, 
        //  {name: "commencePlace", width:100}, 
        //  {name: "terminateDate", width:100}, 
        //  {name: "terminatePlace", width:100}, 
        //  {name: "agreementPeriod", title: "Agreement Period(Month)", width:160}, 
        //  {name: "dgDesc", width:150}, 
        //  {name: "docLocation",  width:"*"} 
//         {name: "official_no", title: "Official No"},
//         {name: "reg_port", title: "Port of Registry", type: "text", wrap: true }, 
			{name: "imoNo", width: 80 }, 
			{name: "referenceNo", width:50 }, 
			{name: "crewName", width: 150 }, 
			{name: "serbNo", width: 150 }, 
			{name: "nationalityId", title: "Nationality", optionDataSource:"nationalityDS", valueField:"id", displayField:"engDesc"},
			{name: "birthDate"},
			{name: "capacityId", title: "Capacity", optionDataSource:"rankDS", valueField:"id", displayField:"engDesc"},
			{name: "currency" } ,
			{name: "salary", title: "Salary", format:",##0.00", type:"decimal" } 
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
			dataSource: "crewDS",
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
							if(dsResponse.status<0){
								isc.say ("Error occurs <br>" + dsResponse);
							}else{
								console.log(dsResponse.data)
								openCrewListDetail(dsResponse.data);
								console.log("respose",dsResponse)
								isc.say("Upload Successful! please correct errors if any")
							}
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

