var rptSrc = "jasper_report_demo/mmo/Avg_Wages_Summary_HK_Reg_Seafarer.pdf";
var chk_para_ind = window.location.search;
var color_code_ind = -1;
 
	if (chk_para_ind > "") {
			chk_para_ind = chk_para_ind.substring(1, chk_para_ind.length);
			color_code_ind = chk_para_ind.split("&")[0].split("=")[1];
			
	} else {
			color_code_ind = 5; // def.

	}


var sectionTitle = 	
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Average Monthly Wages by Rank/Rating</font></b></p>"
	});

	
	
var searchForm = 
	isc.ReportDynamicForm.create({
		width: 320, numCols: 6,	
		ID:"RPT_MMO_009_Form",
		fields: [
					{name: "reportDate", title: "Report Date", defaultValue : new Date(), required:true, type: "date", displayFormat:"dd/MM/yyyy"},
					{name: "rankingRating", title: "Ranking / Rating", valueMap:{"O":"Officer", "R":"Rating"}, defaultToFirstOption:true,allowEmptyValue:false, endRow:true}	,				
					{name: "spacerItem", 	type:"SpacerItem", endRow:true}, 
					{name:"1USD", title:"1 USD =", type:"staticText", endRow:true},
					// {name: "USD/HKD", title: "HKD", type:"decimal", defaultValue:7.77}	,				
					// {name: "USD/RMB", title: "RMB", type:"decimal", defaultValue:6.46}	,			
					// {name: "USD/BGP", title: "BGP", type:"decimal", defaultValue:0.72}	,				
				]
	});
	
	var  APW_SearchResultListLG=   isc.ListGrid.create({
		ID: "RPT_MMO_DollorListLG",
		// autoFetchData :true,
		canEdit:true,
		editEvent:"click",
		autoSaveEdits:false,
		canRemoveRecords:false,
		warnOnRemoval:true,            

		useClientFiltering: true,
		showAllRecords: true,
		// showFilterEditor: true,
		filterOnKeypress: false,
	//    showRowNumbers: true,
	   headerHeight:30,
		alternateRecordStyles:true,
		width:"200",
		height:"200",

		initialSort: [

		],
		fields: [
	 
			{ name: "DollorCode", title:"Currency",  width: 80 ,},
			// { name: "fsa_score",  width: 80 },
			{ name: "Exchange", title:"Rate", width: "*", type:"decimal" , format :"#,##0.000" },
			// { name: "sol_guid_created", title:"Solution Created", width: "*" ,type:"boolean",width: 80, canEdit:false},
			// { name: "sol_guid", title:"Solution id", width: "*" ,hidden:true},
			// { name: "createdDate", hidden:true },
		],


	});
	var APW_add_ships_btn= isc.IAddButton.create({
				ID:"MMO_add_dollor_btn",
				title: 'Add new Dollor',
				height:30,
				autoFit:true,
				// autoDraw:false,
				// hidden:true,
				// onControl: '',
				click: function() {
					RPT_MMO_DollorListLG.startEditingNew();
				}
	})


	RPT_MMO_DollorListLG.setData([{DollorCode:"HKD",Exchange:7.77},
	{DollorCode:"USD",Exchange:1},
	{DollorCode:"RMB",Exchange:6.48},
	{DollorCode:"GBP",Exchange:0.73}])

var searchFormToolBar = 
	isc.ReportToolbar.create({
		buttons: [
			{name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () { 
				  if(RPT_MMO_009_Form.validate()){
					  RPT_MMO_DollorListLG.saveAllEdits();
					  CurrencyObj = {};
					  RPT_MMO_DollorListLG.getData().forEach(o => {
						  CurrencyObj[o.DollorCode] = o.Exchange;
					  })
					  var values = Object.assign(RPT_MMO_009_Form.getValues(), { Currency: CurrencyObj })

					  var requestArguments = ["RPT_MMO_009", values];
					  ReportViewWindow.displayReport(requestArguments);
					}
			  }
			}
		]
	});	

	
	var searchSectionContent = 
	isc.ReportSectionStack.create({
		sections: [
			{ title: "Report", expanded: true, resizeable: false, 

				items: [ 
					

					isc.VLayout.create({ membersMargin: 10,
						 members: 
						[isc.HLayout.create({ height: 30, membersMargin: 10, members: [searchForm, searchFormToolBar] }), RPT_MMO_DollorListLG,MMO_add_dollor_btn] })
			
			]
			}

		]

});		




var contentLayout = 
	isc.VLayout.create({ 
	width: "75%", 
	padding: 10, 
    members: [ sectionTitle, searchSectionContent ]


});

isc.HLayout.create({
	ID: "mainLayout", 
	width: "100%",
	height: "100%", 
	members: [ contentLayout ]
});
