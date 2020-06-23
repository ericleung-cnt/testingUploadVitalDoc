Date.prototype.ddMMyyyy = function() {
	var mm = this.getMonth() + 1;
	var dd = this.getDate();
	var yyyy = this.getFullYear();
	
	return '' + (dd<=9 ? '0' + dd : dd) + '/' + (mm<=9 ? '0' + mm : mm) + '/' + yyyy;
};

	 var shipRegList = isc.ListGrid.create({
		 ID: "SrList",
		 showFilterEditor:true,
		 dataSource:"regMasterDS",
		 width:"100%",
		 fields:
			 [
			  {name:"applNo", title:"Appl No.", width:100},
			  {name:"regName", title:"Ship Name"},
			  {name:"regDate", title:"Reg Date"},
			  {name:"offNo", title:"Official No.", width:100},
			  {name:"imoNo", title:"IMO No.", width:100},
			  {name:"deRegTime", title:"De-Reg Time", displayFormat:"dd/MM/yyyy hh:mm", width:150},
		     ],
	     rowDoubleClick:function(record,row,col) {
	    	 field = this.parentElement.parentElement.parentElement.parentElement.members[0].form.getItem("applNo");
	    	 console.log(field.getValue());
	    	 var oldValue = field.getValue();
	    	 if (oldValue == null || oldValue == "") {
	    		 field.setValue(record.applNo);
	    	 } else {
	    		 field.setValue(oldValue +"," + record.applNo);
	    	 }
    		 field.changed(this.parentElement.parentElement.parentElement.parentElement.members[0].form, field, field.getValue());
//	    	 var oldValue = field;
//	    	 if (oldValue == null || oldValue == ""){
//	    		 field = record.applNo;
//	    	 } else {
//	    		 field = oldValue + "," + record.applNo;
//	    	 }

	     }
	 })
	 setTimeout(overrideApplNoChanged, 0, shipRegList);
	 forceUpper(shipRegList);

	 var transcriptAppList = isc.ListGrid.create({
		 ID: "applList",
		 showFilterEditor:true,
		 dataSource:"transcriptApplicationDS",
		 autoFetchData: true,
		 fields:
			 [
				{name:"issueType", title:"Issue type"},
				{name:"vesselName", title:"Ship name"},
				{name:"imoNumber", title:"IMO No."},
				{name:"officialNumber", title:"Official No."}
			 ],
		 rowDoubleClick: function(record, row, col){
			 console.log(record);
		 }
	 });
	 forceUpper(transcriptAppList);

	 var appBtns = isc.ButtonToolbar.create({
		ID:"transcriptBtn_Toolbar",
		buttons:[
	          {
	        	  name:"deleteBtn", title:"Delete", autoFit: true, disabled:true,
	        	  click : function () {
        			  var rec = applList.getSelectedRecord();
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  console.log(value);
	        			  //var rec = applList.getSelectedRecord();
	        			  console.log("rec:" + rec);
	        			  applList.removeData(rec);
	        		  })
	        	  }
	          }
		]
	 });

	 var printTranscript = function(formData){
		  regMasterDS.fetchData(
					{applNo: formData.applNo},
					function(resp, rm, req){
						if (rm==null){
							isc.warn("Appl No. not found");
							return;
						}
						if (rm.regStatus=="A") {
							isc.warn("Application not registered yet");
							return;
						}
						
						txnLockDS.fetchData(
							{applNo: formData.applNo}, 
							function(resp, txnLock, req){
								if (txnLock.length>0){
									isc.warn("Appl No." + formData.applNo + " was locked by SR");
									return;
								}  	
								var reportType = "RPT_SR_011";
								ReportViewWindow.vitalDocReport(
									[reportType, formData],
									function(){
										regMasterDS.updateData(
											{
												reportType: reportType,
												applNo: formData.applNo,
												registrar: formData.registrar.toString(),
												certified: formData.certified,
												fullAddr: formData.fullAddr,
												printMortgage: formData.printMortgage,
												reportDate: formData.reportDate.ddMMyyyy(),
												zip: formData.zip
											},
											function() {
											},
											{operationId: "UPLOAD_VITALDOC_SRDOC"}
										);
									}
								);
							}
						);
					},
					{operationId: "FETCH_FOR_TRANSCRIPT"}
				  );
				 
				 //ReportViewWindow.displayReport(["RPT_SR_011", data]);

	 };
	 
	 var reportForm = simpleSrReport("Transcript","RPT_SR_011",
			 [
			  {name:"applNo", required:true, title:"Appl No.", width:500, 
				  changed:function(form, item, value) {
				  //form.getItem("zip").setValue(value.includes(","));
					  console.log("transcript applno changed");
					  if (value.length<8 || value.includes(",")){
						  return;
					  }
//					  regMasterDS.fetchData(
//						{applNo:applNo},
//						function(resp, data, req){
//							if (data.regStatus=="A") {
//								isc.warn("Application not registered yet");
//								return;
//							}
//							txnLockDS.fetchData(
//								{applNo:applNo}, 
//								function(resp, data, req){
//									if (data.length>0){
//										isc.warn("Appl No." + record.applNo + " already locked");
//										return;
//									}  												  
//								}
//							);
//						},
//						{operationId: "FETCH_FOR_TRANSCRIPT"}
//					 );
				  },
			  },
			  {name:"reportDate", type:"datetime", required:true, defaultValue:new Date(), title:"Report Date", width:200, dateFormatter:"dd/MM/yyyy HH:mm",
				  changed:function(form,item,value) {
					  console.log("report date " + value);
					  if (value && value.trim && value.trim() != "") {
						  if (value.trim().length == 8 && value.trim().match(/^\d{8}$/g) != null) {
							  item.setValue(value.trim().replace(/(\d{2})(\d{2})(\d{4})/g, function(match, p1, p2, p3, string) { return p1+"/"+p2+"/"+p3+" "+23+":"+59;}));
						  } else {
							  item.setValue(value.trim().replace(/(\d{2})(\d{2})(\d{4})(\d{2})(\d{2})/g, function(match, p1, p2, p3, p4, p5, string) { return p1+"/"+p2+"/"+p3+" "+p4+":"+p5;}));
						  }
					  }
				  }},
			  {name:"registrar", optionDataSource:"registrarDS", displayField:"name", valueField:"id", title:"Registrar", width:200},
			  {name:"certified", type:"boolean", title:"Certified", defaultValue:true,changed:function(form,item,value) {
				  form.getItem("registrar").setDisabled(!value);
			  }},
			  {name:"paymentRequired", type:"boolean", title:"Payment Required", changed:function(_1,_2,_3){
				  this.form.getItem("reason").setEnabled(!this.getValue());
				  }},
			  {name:"fullAddr", type:"boolean", title:"Full Address (uncheck for Individual Owner or Representative Person only)", defaultValue:false }, 
			  {name:"reason", title:"Reason", characterCasing:"upper"},
			  {name:"printMortgage", type:"boolean", title:"Print Mortgage", defaultValue:true },
			  {name:"zip", type:"boolean", value:false, visible:false}
			  ]
	 );

	 reportForm.form.items[9].endRow = false;
	 console.log("reportForm.form.items[9].click = function()");
	 reportForm.form.items[9].click = function() {
		 if (reportForm.form.getField("certified").getValue()) {
			 if (!reportForm.form.getField("registrar").getValue()) {
				 isc.say("Registrar required for certified transcript.");
				 return false;
			 }
		 }
		 if (reportForm.form.validate()) {
			 var data = reportForm.form.getData();
			 var applNoList = data.applNo.split(/,/);
			 var formData = [];
			 for (j=0; j<applNoList.length; j++){
				 formData.push(Object.assign({},reportForm.form.getData()));
				 formData[j].applNo = applNoList[j];
			 }
			 var i = 0;
			 while (i < applNoList.length) {
				 if (applNoList[i].trim().length > 0) {
					 printTranscript(formData[i]);
//					  regMasterDS.fetchData(
//						{applNo: formData[i].applNo},
//						function(resp, rm, req){
//							if (rm==null){
//								isc.warn("Appl No. not found");
//								return;
//							}
//							if (rm.regStatus=="A") {
//								isc.warn("Application not registered yet");
//								return;
//							}
//							
//							txnLockDS.fetchData(
//								{applNo: formData[i].applNo}, 
//								function(resp, txnLock, req){
//									if (txnLock.length>0){
//										isc.warn("Appl No." + formData[i].applNo + " was locked by SR");
//										return;
//									}  	
//									ReportViewWindow.displayReport(["RPT_SR_011", formData[i]]);
//								}
//							);
//						},
//						{operationId: "FETCH_FOR_TRANSCRIPT"}
//					  );
//					 
//					 //ReportViewWindow.displayReport(["RPT_SR_011", data]);
				 }
				 i++;
			 }
		 }
		 return true;

	 };


	 reportForm.form.addField({type:"button", name:"Refresh", click:function() {
		 reportForm.form.getField("applNo").setValue("");
	 }, startRow:false, endRow:false});
	 isc.VLayout.create(
			 {ID: "printForm",
			 members:
	[
	 reportForm,

	 isc.HLayout.create({
		 ID: "transcriptAppListGrid",
		 show:function(){
				this.Super("show", arguments);{
					var isReadOnly = loginWindow.PRINT_TRANSCRIPT_BATCH_MODE_READ_ONLY();
					if(isReadOnly){
						appBtns.setDisabled(true);
						reportForm.form.setCanEdit(false);
						reportForm.form.printButton.setDisabled(true);
					}
				}
			},
		 members:[
			 isc.VLayout.create({
				 members:[
					 isc.SectionStack.create({
						 sections:[
							 { title: "Search", expanded:true, resizeable:false, items:[shipRegList]},
							 { title: "Transcript Application", expanded:true, resizeable:false, items:[transcriptAppList, appBtns]}
						 ]
					 })
				 ]
			 })
		 ]
	 })

	 ]
});

