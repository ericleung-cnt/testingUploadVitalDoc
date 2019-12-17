console.log("ship name appl prog");
var thickBtnHeight = 50;

var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Ship Registration Maintenance<br /></font></b></p>"
	});

var searchSection =
	isc.ListGrid.create({
		dataSource : "regMasterDS",
		showFilterEditor:true,
		fields: [
			{ name:"applNo", width:80, align:"center" },
			//{ name:"applNoSuf", width:120 },
			//{ name:"engMaker", width:120 },
			{ name:"regName", title:"Ship Name", width:180 },
			{ name:"regChiName", title:"Chi Ship Name", width:180 },
			{ name:"applNoSuf", title:"P/F", width:50, align:"center" },
			{ name:"regStatus", title:"R/A/D/W", width:80, align:"center" },
			//{ name:"regRegnType", width:120 },
			{ name:"regDate", format:"dd/MM/yyyy", type:"date", width:80, align:"center" },
			{ name:"deRegTime", format:"dd/MM/yyyy", width:80, align:"center" },
			{ name:"offNo", width:80, align:"center" },
			{ name:"imoNo", width:80, align:"center" },
			{ name:"provExpDate", format:"dd/MM/yyyy", width:180 },
			//{ name:"deRegTime", format:"dd/MM/yyyy", width:180 },
			{ name:"atfDueDate", format:"dd/MM/yyyy", width:180 },
			{ name:"buildDate", width:120 },
			{ name:"buildYear", width:120 },
			//{ name:"offNo", width:120 },
			{ name:"offResvDate", format:"dd/MM/yyyy", width:180 },
			{ name:"callSign", width:120 },
			{ name:"csReleaseDate", format:"dd/MM/yyyy,", width:180 },
			{ name:"firstRegDate", format:"dd/MM/yyyy", type:"date", width:180 },
//			{ name:"regName", width:120 },
//			{ name:"regChiName", width:120 },
			{ name:"intTot", width:120 },
			{ name:"surveyShipType", width:120 },
			{ name:"intUnit", width:120 },
			{ name:"material", width:120 },
			{ name:"noOfShafts", width:120 },
			{ name:"howProp", width:120 },
			{ name:"estSpeed", width:120 },
			{ name:"grossTon", width:120 },
			{ name:"regNetTon", width:120 },
			{ name:"transitInd", width:120 },
//			{ name:"imoNo", width:120 },
			{ name:"length", width:120 },
			{ name:"breadth", width:120 },
			{ name:"depth", width:120 },
			{ name:"dimUnit", width:120 },
			{ name:"engDesc1", width:120 },
			{ name:"engDesc2", width:120 },
			{ name:"engModel1", width:120 },
			{ name:"engModel2", width:120 },
			{ name:"engPower", width:120 },
			{ name:"engSetNum", width:120 },
			{ name:"genAtfInvoice", width:120 },
			{ name:"remark", width:120 },
			{ name:"agtAgentCode", width:120 },
			{ name:"ccCountryCode", width:120 },
			{ name:"operationTypeCode", width:120 },
			{ name:"rcReasonCode", width:120 },
			{ name:"rcReasonType", width:120 },
			{ name:"shipTypeCode", width:120 },
			{ name:"shipSubtypeCode", width:120 },
			{ name:"licenseNo", width:120 },
			{ name:"representativeName", width:120 },

			{ name:"detainStatus" },
				{ name:"detainDesc" },
				{ name:"imoOwnerId" },
				{ name:"deratedEnginePower" },
				{ name:"trackCode" },
				{ name:"certIssueDate", format:"dd/MM/yyyy", type:"date" },
				{ name:"detainDate", format:"dd/MM/yyyy", type:"date" },
				{ name:"protocolDate", format:"dd/MM/yyyy", type:"date" },
				{ name:"atfYearCount" },
				{ name:"provRegDate", format:"dd/MM/yyyy", type:"date" },
		         ],
		         rowDoubleClick:function(record){ openRegMaster(record, null, 0); },
		         selectionChanged:function(record, state){
		        	 console.log("selection changed" + record + state);
		         },
});
searchSection.fetchData();

var btns = isc.ButtonsHLayout.create({
	members : [
		//isc.IAddButton.create({ height:thickBtnHeight, click:"openRegMaster({}, null, 0)"}),
		isc.IButton.create({ title:"New<br>Ship Reg<br>Applicaiton",
			height:thickBtnHeight,
			icon: "add.png",
			click:function(){
				openRegMaster({}, null, 0);
			}
		}),
		//
		isc.IButton.create({ title:"Receive<br>De-Reg<br>Application", 
			disabled:true,
			height:thickBtnHeight,
			
			/* 1. Only if reg status is "R"
			 * - Receive De-Reg Application, Change Registration Details can start corresponding workflow 
			 * 2. Only if reg status is "R" and suffix is "F" (full-reg)
			 * - Receive Re-Reg Application can start workflow
			 */
            			click: function(){
			if (!searchSection.getSelection().isEmpty() && "R" == searchSection.getSelection()[0].regStatus) {
				isc.confirm("Please confirm to de-reg " + searchSection.getSelection()[0].applNo, function(value) {
					if (value) {
						console.log("dereg only 'R'");
						console.log(searchSection.getSelection()[0]);
						regMasterDS.updateData(null, function(resp,data,req){
							isc.say("De-registration received: " + searchSection.getSelection()[0].applNo);
							searchSection.invalidateCache();
						}, {operationId:"deReg", data:{applNo:searchSection.getSelection()[0].applNo}});
					}
				});
			} else {
				isc.say("Please select a Registered record");
			}
		}}),
		isc.IButton.create({ title:"Receive<br>Re-Reg<br>Application", 
			disabled:true,
			height:thickBtnHeight,
			click: function(){
				if (!searchSection.getSelection().isEmpty() && "R" == searchSection.getSelection()[0].regStatus && "F" == searchSection.getSelection()[0].applNoSuf) {
				isc.confirm("Please confirm to re-reg " + searchSection.getSelection()[0].applNo, function(value) {
					if (value) {
						console.log("dereg only 'R'");
						console.log(searchSection.getSelection()[0]);
						regMasterDS.updateData(null, function(resp,data,req){
							isc.say("Re-registration received: " + searchSection.getSelection()[0].applNo);
							searchSection.invalidateCache();
						}, {operationId:"reReg", data:{applNo:searchSection.getSelection()[0].applNo}});
					}
				});
			} else {
				isc.say("Please select a Full Registered record");
			}
		}}),
		isc.IButton.create({ title:"Change<br>Registration<br>Details", 
			disabled:true,
			height:thickBtnHeight,
			click: function(){
				if (!searchSection.getSelection().isEmpty() && "R" == searchSection.getSelection()[0].regStatus) {
				isc.confirm("Please confirm to change registration details " + searchSection.getSelection()[0].applNo, function(value) {
					if (value) {
						console.log("dereg only 'R'");
						console.log(searchSection.getSelection()[0]);
						regMasterDS.updateData(null, function(resp,data,req){
							isc.say("Change registration details received: " + searchSection.getSelection()[0].applNo);
							searchSection.invalidateCache();
						}, {operationId:"changeReg", data:{applNo:searchSection.getSelection()[0].applNo}});
					}
				});
			} else {
				isc.say("Please select a Registered record");
			}
		}}),
		isc.IButton.create({ title:"Find Owner", 
			height:thickBtnHeight,
			click: function(){
			var fields = [
							 {name:"name", title:"Owner"},
							 {type:"button", title:"Find", startRow:false, endRow:false, click:function (form, item) {
								 console.log("find" + form + " " + item);
								 ownerDS.fetchData(null,
										 function(resp,data,req){
									 var val = [];
									 for (var i = 0; i < data.length; i++) {
										 if (!val.contains(data[i].applNo)) {
											 val.add(data[i].applNo);
										 }
									 }
									 form.parentElement.parentElement.markForDestroy();
									 searchSection.setFilterEditorCriteria({fieldName:"applNo", operator:"inSet", value:val});
									 searchSection.filterByEditor();
								 }, {data:{name:form.getItem("name").getValue()}});
							 }},
							 {type:"button", title:"Close", startRow:false, endRow:false, click:function (form, item) {
								 form.parentElement.parentElement.markForDestroy();
							 }},
            ];
			isc.Window.create({
				title:"Find Owner",
				height:150,
				width:400,
				items:[isc.DynamicForm.create({
					numCols:4,
					fields:fields})
				       ]
			}).show();
		}}),
		isc.IExportButton.create({ listGrid: searchSection, height:thickBtnHeight }),
	]
});

var contentLayout =
	isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
    members: [ sectionTitle, searchSection, btns ]
});

