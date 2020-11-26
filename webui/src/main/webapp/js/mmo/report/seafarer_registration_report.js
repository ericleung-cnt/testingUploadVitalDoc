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
		contents: "<p><b><font size=2px>Seafarer Registration Report</font></b></p>"
	});
	
var searchForm = 
	isc.ReportDynamicForm.create({
		ID:"RPT_MMO_001_Form",
		width: 320, numCols: 4,	
		fields: [
					{name: "serbNo", title: "SERB NO.", required:true, lenght:8, width:70, characterCasing:"upper"},
					{name: "seafarerId", title: "Seafarer ID", lenght:20, width:100}					
				]
	});
	

var searchFormToolBar = 
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () {
				if(RPT_MMO_001_Form.validate()){
					var requestArguments = ["RPT_MMO_001", RPT_MMO_001_Form.getValues()]; 
					ReportViewWindow.displayReport(requestArguments);
				}  
			  }
			}
		]
	});	

	
var searchSectionContent = 
	isc.ReportSectionStack.create({
		sections: [
			{title: "Report", expanded: true, resizeable: false, 
				items: [ isc.HLayout.create({membersMargin:10, members:[searchForm, searchFormToolBar]}) ]
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

