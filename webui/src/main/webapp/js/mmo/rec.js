console.log("rec.js");
isc.TitleLabel.create({
	ID:"seafarerRegMainSearchResultListLGSummary", contents: "<p> Total no. of search item: <b> 0 </b> </p>"
});


isc.SearchForm.create({
	ID:"seafarerRegMainSearchForm", dataSource : "seafarerDS", numCols: 6,
	saveOnEnter:true,
	submit:function(){
		seafarerSearchFormToolBar.getButton('searchBtn').click();
	},
	fields: [
	         { name: "id", title: "HKID", type: "text"},
	         { name: "serbNo", title: "SERB", type: "text"},
	         { name: "partType", editorType:"SelectItem"},

	         { name: "surname", title: "Surname (English)", type: "text"},
	         { name: "firstName", title: "First Name (English)", type: "text"},
	         { name: "chiName", title: "Chinese Name", type: "text", endRow: false},
	         { name: "sex"},
	         { name: "nationalityId", title: "Nationality", type: "SelectItem", endRow: false,
	        	 optionDataSource:"nationalityDS",
	        	 displayField:"engDesc", valueField:"id"
	         },
	         { name:"searchBtn", type:"button", title:"Search", autoFit: true, disabled: false,
	        	 click : function () {
	        		 // sfRecWindow.show();
	        		 seafarerRegMainSearchResultListLG.setData([]);
	        		 var criteria = seafarerRegMainSearchForm.getValuesAsCriteria(false);
	        		 seafarerRegMainSearchResultListLG.fetchData(criteria, function(dsResponse, data, dsRequest){
	        			 var c = "<p> Total no. of search item: <b> "+ dsResponse.totalRows +" </b> </p>";
	        			 seafarerRegMainSearchResultListLGSummary.setContents(c);
	        		 });

	        	 }, startRow:false, endRow:false
	         },
	         { name: "newRecBtn", type:"button", title: "New Seafarer Record", autoFit: true, disabled: false, onControl:"RENEW_SEAFARER_REGISTRATION",
	        	 click : function () {
	        		 openSfRecDetail(null);
	        	 }, startRow:false, endRow:false
	         },

	         ]
});


var seafarerSearchResultList =isc.ListGrid.create({
	ID:"seafarerRegMainSearchResultListLG", dataSource : "seafarerDS", showFilterEditor:true, filterOnKeypress:true,
	fields: [
	         { name: "id", 			width:120},
	         { name: "serbNo", 		width:100},
	         { name: "serbDate",		width:100},
	         { name: "partType",		width:100},
	         { name: "surname", 		width:120},
	         { name: "firstName",	width:140},
	         { name: "chiName",		width:120},
	         { name: "sex",			width:100},
	         { name: "telephone", 	width:250},
	         { name: "nationalityId", width:"*", optionDataSource:nationalityDS, valueField:"id", displayField:"engDesc"}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSfRecDetail(record);
	         }
});

var appBtns = isc.ButtonsHLayout.create({
	members :
 	 [
		 isc.IExportButton.create({ listGrid: seafarerSearchResultList }),
	 ],
});

isc.HLayout.create({
	ID: "mainLayout",
	members: [
		isc.VLayout.create({
		    members: [
		              isc.TitleLabel.create({ID:"sectionTitle", contents: "<p><b><font size=2px>Seafarer Registration Maintenance - Record [Ver 0.1.1]</font></b></p>"}),
		              isc.SectionStack.create({
		          		sections: [
		          			{title: "Search", expanded: true, resizeable: false, items: [ seafarerRegMainSearchForm]},
		          			{title: "Result", expanded: true, items: [ seafarerRegMainSearchResultListLG, seafarerRegMainSearchResultListLGSummary, appBtns ]}
		          		]
		              })

		             ]
		})
	  ]
});
