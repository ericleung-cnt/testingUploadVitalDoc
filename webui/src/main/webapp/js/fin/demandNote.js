//-----------------Refund window
console.log("demandNote.js");
isc.DynamicForm.create({
	ID:"finDNRefundDynamicForm",
	width: "100%",
	dataSource:"demandNoteRefundDS",
	numCols: 4,
	cellBorder:0,
	fields: [
	         {name: "demandNoteNo", title: "Demand Note No", type:"staticText", endRow:true},
	         {name: "refundAmount",	title: "Amount", 		 endRow:true, format:",##0.00"},
	         {name: "remarks", 		title: "Remarks", type:"textArea", colSpan:3, rowSpan:3, height:80, width:380}
	         ]


});

isc.Window.create({
	ID:"finDNRefundWindow",
	width: 600, isModal: true, showModalMask: true,
	height: 250,
	title: "Recommendation For Refund",
	items: [
	        isc.VLayout.create({
	        	members: [
							finDNRefundDynamicForm,
							isc.ButtonToolbar.create({
								ID:"finDNRefundForm_ToolBar",
								buttons: [
								          {name:"refundBtn", title:"Recommendation For Refund  ", onControl:"FINANCE_REFUND",  width:160,
								        	  click : function () {
								        		  finDNRefundDynamicForm.saveData(function (dsResponse, data, dsRequest){},{'operationType':'add', 'operationId':'REFUND_DEMAND_NOTE'});
								        	  }
								          }

								          ]
							})
	        	           ]
	        })
	    ]
});




//--------------------- Detail Form ------------------
isc.DynamicForm.create({
	ID:"finDNDetailDynamicForm",
	width: "100%",
	dataSource:"demandNoteHeaderDS",
	numCols: 8,
	canEdit:false,
	cellBorder:0,
	fields: [
	         {name: "demandNoteNo", title: "Demand Note No.:", 	type:"staticText"},
	         {name: "applNo", 		title: "Appl No.:", 		type:"staticText"},
	         {name: "amount", 		title: "Total:", 		 	type:"staticText", format:",##0.00"},
	         {name: "status", 	title: "Status:", 		 	type:"staticText", endRow:true, valueMap:{"3":"Issued", "11":"Written Off", "12":"Cancelled", "16":"Refunded"}},

	         {name: "generationTime", title: "Issue Date:",	type:"staticText", startRow:true, format:"dd/MM/yyyy"},
	         {name: "dueDate", 		title: "Due Date:",		type:"staticText", format:"dd/MM/yyyy" },
	         {name: "firstReminderDate", title: "1st Reminder Date:", type:"staticText", endRow:false, format:"dd/MM/yyyy"},
	         {name: "secondReminderDate", title: "2nd Reminder Date:", type:"staticText", endRow:false, format:"dd/MM/yyyy"},
	         {name: "paymentStatus",type:"staticText",  	width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
	         {name: "shipNameEng", title:"Ship Name:", type:"staticText", colSpan:3, startRow:"true"},
	         {name: "billName", title: "Billing Person:", type:"staticText", colSpan:3, startRow:true, width: 380, 	type:"staticText", length:40},
	         {name: "coName", 	title: "C/O:", type:"staticText", colSpan: 3, startRow:true, endRow:true, type:"staticText",width:380, length:40},

	         //{name: "cwStatus", 	showIf:"false"},
	         //{name: "cwBy", 		title: "Cancel By", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false},
	         //{name: "cwBy", 		title: "Write-Off By", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false},

	         //{name: "cwTime", 		title: "Cancel Time", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false},
	         //{name: "cwTime", 		title: "Write-Off Time", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false},

	         {name: "address1",		title: "Address:", type:"staticText", colSpan: 3, startRow:true, endRow:false, width: 380, length:40},
	         //{name: "cwRemark", 	title: "Cancel Remark", type:"textArea", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false, readOnlyDisplay:"static", colSpan:3, rowSpan:3, height:80, width:380},
	         //{name: "cwRemark", 	title: "Write-Off Remark", type:"textArea", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false, readOnlyDisplay:"static", colSpan:3, rowSpan:3, height:80, width:380},

	         {name: "address2",		title: "", type:"staticText", colSpan: 3, startRow:true, endRow:true, width: 380, length:40},
	         {name: "address3", 	title: "", 	type:"staticText", colSpan: 3, startRow:true, endRow:true, width: 380, length:40},
	         {name: "email", title:"Email:", type:"staticText", startRow:true },
	         {name: "demandNoteItems", showIf:"false"},



	         ],
	  refresh:function(demandNoteNo){
		  finDNDetailDynamicForm.fetchData({"demandNoteNo":demandNoteNo},
					function (dsResponse, data, dsRequest) {
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

});

isc.ButtonToolbar.create({
	ID:"finDNDetailSectionForm_ToolBar",
	buttons: [
	          {name:"reprintOldBtn", title:"Print Old Demand Note", width:150, //autoFit: true,
				  click : function () {
//					  Suf = P or F is sr
					  var sufV = finDNDetailDynamicForm.getValue('applNoSuf');
					  if(sufV=='P' || sufV=='F'){
						  ReportViewWindow.displayReport(["oldDemandNoteGeneratorSR", finDNDetailDynamicForm.getValues()]);
					  }else{
						  ReportViewWindow.displayReport(["oldDemandNoteGeneratorMMO", finDNDetailDynamicForm.getValues()]);
					  }

				  }
	          },
	          {name:"reprintBtn", title:"Reprint", width:100, //autoFit: true,
	        	  click : function () {
	        		  ReportViewWindow.displayReport(["RPT_FIN_001", finDNDetailDynamicForm.getValues()]);
	        	  }
	          },
	          {name:"refundBtn", title:"Recommendation For Refund", width:160, onControl:"FINANCE_REFUND",
	        	  click : function () {
	        		  finDNRefundWindow.show();
	        		  finDNRefundDynamicForm.setValue('demandNoteNo', finDNDetailDynamicForm.getValue('demandNoteNo'));
	        	  }
	          }

	          ]
});

isc.ListGrid.create({
	ID:"finDNDetailItemListGrid", width: "100%", height: "*",  dataSource:"demandNoteItemDS", sortField:"itemNo",
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
isc.ListGrid.create({
	ID:"finDNDetailReceiptListGrid", width: "100%", height: "*",  dataSource:"demandNoteReceiptDS", sortField:"receiptNo",
	fields: [
	         {name: "dnDemandNoteNo", showIf:"false"},
	         {name: "itemId", showIf:"false"},
	         {name: "receiptNo", title:"Receipt No.", width:120},
	         {name: "inputTime", 	title: "Receipt Date", width:140},
	         {name: "paymentType", 	width:140, valueMap:{"10":"CASH", "20":"CHEQUE", "30":"EPS", "40":"REMITTANCE", "50":"CREDIT CARD", "60":"DEPOSIT", "70":"AUTOPAY", "80":"OCTOPUS", "90":"PPS"}},
	         {name: "amount", 		title: "Receipt Amount", format:",##0.00", width:140},
	         {name: "cancelDate", title:"Cancellation Date", width:100},
	         {name: "machineCode", title:"Machine Code", width:200}
	         //{name: "remark", 		 	width:"*"},
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	         }
});
isc.ListGrid.create({
	ID:"finDNDetailRefundListGrid", width: "100%", height: "*",  dataSource:"demandNoteRefundDS", sortField:"refundId",
	fields: [
	         //{name: "dnDemandNoteNo", showIf:"false"},
	         //{name: "refundId", showIf:"true", width:150},
	         {name: "refundAmount", 		title: "Refund Amount", format:",##0.00", width:100},
	         {name: "remarks", title:"Refund Remarks", width:"*"},
	         {name: "voucherNo", 	width:150},
	         {name: "repayDate", title:"Refund Payment Date", width:150},
	         {name: "voucherDate", 	width:150},
	         {name: "userCode", title:"Request Sender",	width:150},
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	         }
});

isc.Window.create({
	ID:"finDNDetailWindow",
	width: 1000, isModal: true, showModalMask: true,
	height: 700,
	title: "Demand Note Detail",
	items: [
	        isc.VLayout.create({
	        	members: [
//	        	           isc.TitleLabel.create({contents: "<p><b><font size=2px>MMO Adhoc Demand Note</font></b></p>"}),
	        	           isc.SectionStack.create({
	        	        		sections: [
	        	        		   {title: "Demand Note Information", expanded: true , resizeable: false,  items: [ finDNDetailDynamicForm, finDNDetailSectionForm_ToolBar ] },
	        	        		   {title: "Fee", expanded: true , resizeable: false, items: [ finDNDetailItemListGrid ] },
	        	        		   {title: "Receipt", expanded: true , resizeable: false, items: [ finDNDetailReceiptListGrid ] },
	        	        		   {title: "Refund", expanded: true , resizeable: false, items: [ finDNDetailRefundListGrid ] }
	        	        		 ]

	        	        	})
	        	           ]
	        })
	    ]
});

function openFinDn(record){
	finDNDetailWindow.show();
	console.log("openFinDn");
	finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(record && record.paymentStatus == "0");
	finDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(true);
	finDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(true);
	if(record!=null){
		if(record.demandNoteNo.length==6){
			finDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(false);
		}else{
			finDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(false);
		}

		finDNDetailDynamicForm.setValues({});
		finDNDetailItemListGrid.setData([]);
		finDNDetailReceiptListGrid.setData([]);
		finDNDetailRefundListGrid.setData([]);
		finDNDetailDynamicForm.reset();
		var demandNoteNo = record.demandNoteNo;
		finDNDetailDynamicForm.refresh(demandNoteNo);

		/*
		finDNDetailItemListGrid.showField('fcFeeCode');
		finDNDetailItemListGrid.hideField('fcFeeCodeDesc');

		mmoDNDetailDynamicForm.setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('saveBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(true);
*/
	}
}

//--------------------- Search Form -------------------
isc.SearchForm.create({
	ID:"finDemandNoteSearchForm",	width: 350, numCols: 4, dataSource:"demandNoteHeaderDS",
	saveOnEnter:true,
	submit:function(){
		finDemandNoteSearchFormToolBar.getButton('searchBtn').click();
	},
	fields: [
	     {name: "demandNoteNo", title: "Demand Note NO.", type: "text"},
		 {name: "billName", 	title: "Billing Person", type: "text", endRow:true}
	  ]
	});


isc.ButtonToolbar.create({
	ID:"finDemandNoteSearchFormToolBar", width:100,
		buttons: [
		          {name:"searchBtn", title:"Search", autoFit: true,
		        	  click : function () {
		        		  // sfRecWindow.show();
		        		  finDemandNoteSearchListGrid.setData([]);
						  var criteria = finDemandNoteSearchForm.getValuesAsCriteria(false);
						  finDemandNoteSearchListGrid.fetchData(criteria, function(dsResponse, data, dsRequest){
						  });


		        	  }
		          }
		          ]
	});

isc.HLayout.create({
	ID: "finDemandNoteSearchSectionLayout",
	height:35, layoutMargin:10,
	members: [
	          finDemandNoteSearchForm,
	          finDemandNoteSearchFormToolBar
	          ]
});


isc.ListGrid.create({
	ID:"finDemandNoteSearchListGrid", dataSource:"demandNoteHeaderDS", showFilterEditor:true, //filterOnKeypress:true,
	height:"100%",
	sortField:"generationTime", sortDirection:"descending",
	fields: [
	         {name: "demandNoteNo", 	width:120},
	         {name: "applNo", 			width:100},
	         {name: "amount", 			title: "Total Amount", width:100, format:",##0.00"},
	         {name: "status", 	 		width:120, valueMap:{"3":"Issued", "11":"Written Off", "12":"Cancelled", "16":"Refunded"}} ,
	         {name: "paymentStatus", 	width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
	         {name: "generationTime", title:"Issue Date",	width:120},
	         {name: "dueDate", 	 		width:80} ,
	         {name: "billName", 	 	title: "Billng Person", width:200} ,
	         {name: "coName", 	 		title: "C/O", width:200,
//	        	 formatCellValue: function (value, record) {
//	        		 var name1 = record.coName1;
//	        		 var name2 = record.coName2;
//	        		 if(name1!=null && name2!=null){
//	        			 return name1+" "+name2;
//	        		 }else if(name1!=null){
//	        			 return name1;
//	        		 }else if(name2!=null){
//	        			 return name2;
//	        		 }
//	        		 return "";
//	             }
	         },
	         {name: "addresses", 	 	title: "Address", width:200, canFilter:false,
	        	 formatCellValue:function(value, record, rowNum, colNum, grid){
	        		 	var add1 = record.address1;
	        		 	var add2 = record.address2;
	        		 	var add3 = record.address3;
	        		 	var result = add1;
	        		 	if(add2!=null){
	        		 		result = result+", "+add2;
	        		 	}
	        		 	if(add3!=null){
	        		 		result = result+", "+add3;
	        		 	}
		        		return result;
		        	}
	         } ,
	         {name: "shipNameEng", title:"Ship Name", width:120},
	         {name: "firstReminderDate", title:"1st reminder", width:80},
	         {name: "secondReminderDate", title:"2nd reminder", width:80}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	 	    	openFinDn(record);
	 	    }
});


var appBtns = isc.ButtonsHLayout.create({
	members :
 	 [
		 isc.Button.create({title:"Refresh", click:function(){ finDemandNoteSearchListGrid.fetchData(); }}),
		 isc.IExportButton.create({ listGrid: finDemandNoteSearchListGrid }),
	 ],
});

isc.HLayout.create({
	ID: "finDemandNoteMainLayout",
	members: [
	      isc.VLayout.create({
		    members: [
		               isc.TitleLabel.create({contents: "<p><b><font size=2px>Demand Note Details</font></b></p>"}),
		               isc.SectionStack.create({
		           		sections: [
		           			//{title: "Search", expanded: true, resizeable: false, items: [ finDemandNoteSearchSectionLayout]},
		           			{title: "Result", expanded: true, items: [ finDemandNoteSearchListGrid, appBtns ]	}
		           			]

		               	})
		              ]
	      })

	      ]
});
