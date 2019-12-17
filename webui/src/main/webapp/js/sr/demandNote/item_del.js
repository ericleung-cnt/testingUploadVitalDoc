var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Demand Note Items<br /></font></b></p>"
	});

var searchSection =
	isc.ListGrid.create({
		dataSource : "demandNoteItemDS",
		fetchOperation:"demandNoteItemDS_unused",
		showFilterEditor:true,
		fields: [
		         {name:"itemId", width:100, },
		         {name:"applNo", width:100, },
		         {name:"itemNo", width:100, },
		         {name:"dnDemandNoteNo", width:100, },
		         {name:"chargedUnits", width:100, },
		         {name:"amount", width:100, },
		         {name:"chgIndicator", width:100, },
		         {name:"adhocDemandNoteText", width:100, },
		         {name:"userId", width:100, },
		         {name:"generationTime", width:100, },
		         {name:"fcFeeCode", width:100, },

		         ],
	});
searchSection.fetchData();

var btns = isc.ButtonsHLayout.create({
	members : [
	           isc.IButton.create({ title:"Delete", click:function() {
	        	   if (searchSection.getSelection().length == 1) {
	        		   var demandNote = searchSection.getSelection()[0].dnDemandNoteNo;
	        		   if (!demandNote || demandNote == "") {
	        			   isc.askForValue("Input Delete reason", function(reason){
	        				   if (reason != null) {
	        					   demandNoteItemDS.removeData(null, function(){
	        						   searchSection.fetchData({id:new Date().getTime()});// force to read from server
	        					   }, {operationId:"demandNoteItemDS_removeSrItem", data:{id:searchSection.getSelection()[0].itemId, reason:reason}});
	        				   }
	        			   }, {width:400});
	        		   } else {
	        			   isc.confirm("Please select demand note item without demand note");
	        		   }
	        	   }
	        	   }}),
	           isc.IExportButton.create({ listGrid: searchSection }),
	           ]
});

var contentLayout =
	isc.VLayout.create({
		width: "75%",
		height: "100%",
		padding: 10,
		members: [ sectionTitle, searchSection, btns ]
	});

isc.HLayout.create({
	width: "100%",
	height: "100%",
	members: [ contentLayout ]
});
