
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
			  {name:"regDate", width:100},
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
	 })

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
	 })

	 isc.VLayout.create(
			 {ID: "printForm",
			 members:
	[
	 simpleSrReport("Transcript","RPT_SR_011",
			 [
			  {name:"applNo", required:true, title:"Appl No.", width:500, changed:function(form, item, value) {
				  form.getItem("zip").setValue(value.includes(","));
			  },},
			  {name:"reportDate", type:"datetime", required:true, defaultValue:new Date(), title:"Report Date", width:200, dateFormatter:"dd/MM/yyyy HH:mm" },
			  {name:"printBy",required:true, valueMap:{"HQ":"Headquarter","RD":"Regional Desk"}, displayField:"name", valueField:"id", title:"Report generate by", width:200},
			  {name:"DemandNoteNo", title:"Demand Note No", },
			  {name:"registrar", optionDataSource:"registrarDS", displayField:"name", valueField:"id", title:"Registrar", width:200},
			  {name:"certified", type:"boolean", title:"Certified"},
			  {name:"paymentRequired", type:"boolean", title:"Payment Required", changed:function(_1,_2,_3){
				  this.form.getItem("reason").setEnabled(!this.getValue());
				  }},
			  {name:"reason", title:"Reason", },
			  {name:"printMortgage", type:"boolean", title:"Print Mortgage", },
			  {name:"zip", type:"boolean", value:false, visible:false}
			  ], function(form) {
		 			if (form.getField("certified").getValue()) {
		 				if (!form.getField("registrar").getValue()) {
		 					isc.say("Registrar required for certified transcript.");
		 					return false;
		 				}
		 			}
		 			return true;
	 			}
	 ),

	 isc.HLayout.create({
		 ID: "transcriptAppListGrid",
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


