console.log("sr.js");
function simpleSrReport(name, id, fields, customValidate) {

	var label = isc.Label.create({
		height : 20,
		wrap : false,
		contents : "<p><b><font size=2px>"+name+"<br /></font></b></p>"
	});
	if (!fields) {
		fields = [
			       {name : "reportDate", title : "Report Date", type : "date", defaultValue : new Date(), required : true, dateFormatter:"dd/MM/yyyy"},
			       {type : "button", title : "Generate Report", click:
			    	   function() {ReportViewWindow.displayReport([id, this.form.getData()]);}
			       }
			       ];
	} else {
		fields.add({type : "button", title : "Generate Report", 
			click:
	    	   function() {
				if (customValidate) {
					if (!customValidate(this.form)) {
						return;
					}
				}
				if (this.form.validate()) {
					var show123 = true;
					var backup = this.form.getData();
					var tempDemandNo = "";
					if (form.getField("printBy").getValue() == "RD") {
						var dataSource = "";
						if(form.getField("applNo").getValue() == "2019/438"){
							dataSource = "showDNDetailDS";
							}else{
								dataSource = "showDNDetailDS2";
							}
						isc.DynamicForm.create({
							ID : "showDNDetailItemListGrid",
								dataSource : "demandNoteHeaderDS",
								
							fields: [
								{name:"demandNoteNo", title:"Demand Note No.", type:"staticText", colSpan:3},
								{name:"applNo", title:"Appl No.", required:true, colSpan:3, type:"staticText", defaultValue:""+form.getField("applNo").getValue()+""},
								{name:"offNo", title:"Official No.", type:"staticText", colSpan:3, required:true},
								{name:"shipName", title:"Ship Name", type:"staticText", colSpan:3},
								{name:"billName", title:"Billing Person", required:true, colSpan:3, width:300, type:"staticText"},
								{name:"coName", title:"C/O", colSpan:3, width:350, type:"staticText"},
								{name:"address1", title:"Address", width:300, colSpan:3, type:"staticText"},
								{name:"address2", title:"", width:300, colSpan:3, type:"staticText"},
								{name:"address3", title:"", width:300, colSpan:3, type:"staticText"},
								{name:"generationTime", title:"Issue Date",  type:"staticText"},
								{name:"dueDate", title:"Due Date", type:"date", type:"staticText"},
								{name:"paymentStatus", title:"Payment Status", type:"staticText",valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}},
								//{name:"ebsFlag", title:"EBS Flag", type:"boolean"},
								{name:"amount", title:"Total", type:"staticText"},
								],
								
//						            searchDemandNoteNo:function(applNo){
//						        	 console.log("searchDemandNoteNo start");
//						        	 demandNoteHeaderDS.fetchData({"applNo":applNo},
//							   					function (dsResponse, data, dsRequest) {
//						        		 console.log("data"+data);
//						        		 console.log("dsResponse"+dsResponse);
//						        		 console.log("dsRequest"+dsRequest);
//						        		 console.log("record"+record);
//						        		 var list = data;
//						        		 if(list != null){
//						        		 var size = list.length;
//						        		 }
//						        		 for(var i = 0;i<size;i++){
//						        			 var dNo = list[i].demandNoteNo;
//						        			 var feeCode = list[i].fcFeeCode;
//						        			 if(feeCode == "14"){
//						        				 tempDemandNo = dNo;
//						        			 }else{
//						        				 tempDemandNo ='104642' ;
//						        			 }
//						        			 
//						        		 }
//	
//						        		
//							   			  console.log(data);
//
//							   			},{'operationId':'SEARCH_DEMAND_NOTE_NO'});
//							   	  },

						         refresh:function(applNo){
						   			  console.log("applNo"+applNo);
						        	 var demandNoteNo = "000";
						        	 demandNoteItemDS.fetchData({"applNo":applNo},
							   					function (dsResponse, data, dsRequest) {
						        		 console.log("data"+data[0]);
						        		 console.log("dsResponse"+dsResponse);
						        		 console.log("dsRequest"+dsRequest);
//						        		 console.log("record"+record);
						        		 var list = data;
						        		 if(list != null){
						        		 var size = list.length;
						        		 }
						        		 if(data!=null){
						        		 loopDemand:for(var i = 0;i<size;i++){
						        			 var dNo = list[i].dnDemandNoteNo;
						        			 console.log("dNo"+dNo);
						        			 var feeCode = list[i].fcFeeCode;
						        			 if(feeCode == "14"){
						        				 demandNoteNo = dNo;
						        				 show123 = true;
						        				 break loopDemand;
						        			 }else{
						        				show123 = false;
						        		
						        			 }
						        			 
						        		 }
						        		 }
						        		 if(!show123){
						        			 vlayout2.hide();
											vlayout2.destroy();
											 isc.say("No Available Demand Note have been found for the record");
											
										}

							   			
						        	 
						        	 
						        	 console.log("demandNoteNo"+demandNoteNo);
						        	 if(show123){
						        	 showDNDetailItemListGrid.fetchData({"demandNoteNo":demandNoteNo},
							   					function (dsResponse, data, dsRequest) {
						        		 console.log("demandNoteNo"+demandNoteNo);
						        		 if(data.paymentStatus != "1"){
												but004.setDisabled(true);
												}
							   				finDNDetailWindow.setTitle("Demand Note Detail (No: " + demandNoteNo + " )");


							   				finDNDetailItemListGrid.fetchData({"dnDemandNoteNo":demandNoteNo});
							   				finDNDetailReceiptListGrid.fetchData({"demandNoteNo":demandNoteNo},
							   											function (dsResponse, data, dsRequest){
							   						if(data.length>0){
							   							//only has receipts
							   							finDNDetailRefundListGrid.fetchData({"demandNoteNo":demandNoteNo},
							   									function (dsResponse, data, dsRequest){
							   										if(data.length==0){
							   											finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(false);
							   										}
							   									}
							   							);

							   						}

							   					}, {"operationId":"FIND_VALUE_RECEIPT_BY_NO"}
							   				);
							   			},{'operationId':'FIND_DEMAND_NOTE_BY_NO'});
						        	 }
						        	 },{'operationId':'SEARCH_DEMAND_NOTE_NO'});
						        	 
							   	  },
						     	
						});
						isc.IButton.create({
							ID:"but004",
							title:"Print Cert",
							click:function(){ReportViewWindow.displayReport([id, backup]);},
							//enable:false,
							//disabled:true,

						});
						

						//but004.setDisabled(true);
						var vlayout2 = isc.VLayout.create({members:[showDNDetailItemListGrid,but004,finDNDetailItemListGridSR ]});
						
						//showDNDetailItemListGrid.getItem("but004").disable();

						isc.Window.create({
							ID : "vlayout3",
							title:"Cert Transcript Status",
							width: 400,
							height: 310,
							items:[vlayout2]
						});
						vlayout3.show();
						finDNDetailDynamicForm.setValues({});
						finDNDetailItemListGrid.setData([]);
						finDNDetailReceiptListGrid.setData([]);
						finDNDetailRefundListGrid.setData([]);
						finDNDetailDynamicForm.reset();
//						var result = showDNDetailItemListGrid.searchDemandNoteNo(backup.applNo);
//						console.log(result);
//						var demandNoteNo = tempDemandNo;
						showDNDetailItemListGrid.refresh(backup.applNo);
//						if(showDNDetailItemListGrid.getField("paymentStatus").getValue() != "1"){
//							but004.setDisabled(true);
//							}
						
					}else{
					ReportViewWindow.displayReport([id, this.form.getData()]);}
					
				}

			}
	       });
	}
	var form = isc.DynamicForm.create({ fields : fields, });
	vlayout = isc.VLayout.create({ members : [label,form] });
	vlayout.form = form;
	return vlayout;
}
function closeWindow(){
	self.opener = this;
	self.close();
	}
isc.ListGrid.create({
	ID:"finDNDetailItemListGridSR", width: "100%", height: "*",  dataSource:"demandNoteItemDS", sortField:"itemNo",showIf:"false",
	fields: [
	        {name: "dnDemandNoteNo", showIf:"false"},
	        {name: "itemId", showIf:"false"},
	        {name: "itemNo", width:80, showIf:"false"},
	        {name: "fcFeeCode", title: "Fee Code", width: "80"},
	        {name: "fcFeeCodeDesc", title: "Fee Description", width: "*", dataPath:"feeCode.engDesc"},
//	        {name: "price", 	title: "Price", format:",##0.00", dataPath:"feeCode.feePrice", width:80},
	        {name: "price", 	title: "Unit Price", format:",##0.00", type:"decimal", width:80,
	        	formatCellValue:function(value, record, rowNum, colNum, grid){
	        		console.log("unit price");
	        		var uprice = record.amount/record.chargedUnits;
	        		return isc.NumberUtil.format(uprice, ',##0.00');
	        	}

	        },
	        {name: "chargedUnits", 		title: "Quantity", width:100,
	        	validators:[
	        	          {type:"isInteger"}
	                      ]
	        },
	        {name: "amount", 			title: "Total Amount", format:",##0.00", type:"decimal", width:100},
	        {name: "userId", 		 	width:100},
	        {name: "generationTime", 	title: "Generation Date", width:150}
         ],
         rowDoubleClick:function(record, recordNum, fieldNum){
         }
});

view = createTreeGrid();
viewLoader.setView(view);
view.getMember(0).setData(isc.Tree.create({
    modelType: "children",
    nameProperty: "categoryName",
    childrenProperty: "sub",
		root:{sub:[{categoryName:"Ship Registration Maintenance",
			sub:[{categoryName:"Ship Name Reservation", js:"js/sr/shipReg/ship_name_ckrs.js", },
			{categoryName:"Ship Registration Maintenance", js:"js/sr/shipReg/ship_name_appl_prog.js",},
			{categoryName:"Amend Transaction Details", js:"js/sr/shipReg/transaction_amend.js",},
			{categoryName:"Owner Enquiry Records", js:"js/sr/shipReg/owner_enq_rec.js",},
			{categoryName:"Transcript Application ", js:"js/sr/shipReg/transcript_application.js",},
			{categoryName:"Print Transcript - Batch Mode", js:"js/sr/reports/transcript_print_batch_mode.js",},
			]
		},
		{categoryName:"CSR Form Maintenance",
			sub:[{categoryName:"CSR Form Maintenance ", js:"js/sr/csrForm/csr_input_print.js", },
			{categoryName:"Maintain SD Data", js:"js/sr/csrForm/csr_sd_mnt.js", },
			]
		},
		{categoryName:"SR Demand Note",
			sub:[{categoryName:"Demand Note", js:"js/sr/demandNote/regular.js",},
			{categoryName:"Delete Demand Note Item", js:"js/sr/demandNote/item_del.js",},
			]
		},
		{categoryName:"SR Reports",
			sub:[
			     {categoryName:"Registration Type of Ships registered in H.K.", js:"js/sr/reports/RPT_SR_014_RM0020_RegistrationTypeOfShipsRegisteredInHK.js"},
			     {categoryName:"Breakdown of No. & Grt of Ships by Type", js:"js/sr/reports/RPT_SR_015_RM0040_BreakdownOfNoAndGrtOfShipsByType.js"},
//			{categoryName:"Ship Detailed List by Ship Name", },
//			{categoryName:"Ship Detailed List by Ship Type", },
//			{categoryName:"Ship List By Report", },
//			{categoryName:"Ship List By Ship Type Summary", },
			{categoryName:"Discounted Annual Tonnage Charges Report", js:"js/sr/reports/RPT-SR-012_AD0100_DiscountedAnnualTonnageCharges.js"},
			{categoryName:"Summary of Ships by Ship Type", js:"js/sr/reports/RPT-SR-013_RM0010_ShipsByShipType.js"},
			{categoryName:"Total No. and Tonnage of Ships in the Register", js:"js/sr/reports/RPT-SR-016_RM0030_TotalAndTonnageShipsRegister.js"},
			{categoryName:"Categories of Owners of Ships", js:"js/sr/reports/RPT-SR-017_RM0060_OwnerCatOfShips.js"},
			{categoryName:"Registers Opened, Closed and Change Ownership", js:"js/sr/reports/RPT-SR-018_RM0110_RegistersOpenedClosedChangeOwnership.js"},
			{categoryName:"Summary of Ships in the Pipeline", js:"js/sr/reports/RPT-SR-019_RM0120_ShipsInPipeline.js"},
			{categoryName:"De-Registered Ships Report", js:"js/sr/reports/RPT-SR-020_RW0010_De-RegisteredShips.js"},
			{categoryName:"Registered Ships Report", js:"js/sr/reports/RPT-SR-021_RW0100_RegisteredShip.js"},
			{categoryName:"Annual Report for Ships Registered / De-Registered", js:"js/sr/reports/RPT-SR-022_RY0100_AnnualReportShipsRegisteredDe-Registered.js"},
			{categoryName:"Annual Report for Ships Registered / De-Registered Detail", js:"js/sr/reports/RPT-SR-022_DETAIL_RY0100_AnnualReportShipsRegisteredDe-Registered.js"},
			{categoryName:"Tonnage Distribution of Ships", js:"js/sr/reports/RPT-SR-023_RA0030_TonnageDistributionShips.js"},
			{categoryName:"Ranking of Group Owners / Companies", js:"js/sr/reports/RPT-SR-024_RA0110_RankingGroupOwnersCompanies.js"},
/*
PRG-SR-041 Statistics of Ship de-registration reasons	RPT-SR-025
PRG-SR-042 Statistics of Mortgage Transactions including Transfer Mortgage and Discharge Mortgage	RPT-SR-026
PRG-SR-043 Number of Ships and Tonnage	RPT-SR-027
PRG-SR-044 Ships Registered in Hong Kong Analysed by Gross Tonnage	RPT-SR-028
PRG-SR-045 Ship Registered in Hong Kong by Ship Type	RPT-SR-029

RPT-SR-001 AIP for CO/SD ( only send to CO/SD, and will distribute to PRQC and SQA)
RPT-SR-002 AIP for OFCA
RPT-SR-003 AIP for applicant
RPT-SR-004 AIP for Class Society
RPT-SR-005 Performance Pledge Log Sheet for PRQC assessment
RPT-SR-006 CoR
RPT-SR-007 Memo about change of CoR
RPT-SR-008 Acknowledgement for Collection of Document (Pro to Full with Bill of Sale & POA)
RPT-SR-009 Acknowledgement for Collection of Document with HK Shipping Registry for on site scenario (New Registry (all Cert for delivery))
RPT-SR-010 Acknowledgement for CSR of Ship de-reg status
RPT-CSR-001 CSR
 */
			]
		},

		]}}));