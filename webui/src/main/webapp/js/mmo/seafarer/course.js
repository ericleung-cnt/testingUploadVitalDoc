console.log("course.js");
var openCopyCourseForm =  function(callback) {
	console.log("open copy course");
	var courseList = isc.ListGrid.create({
		ID:"courseList",
		dataSource: "courseCodeDS",
		fields:[
			{ name:"institue", title:"Institue"},			
			{ name:"feeCodeEngDesc", title:"Desc"}
		]
	});
	var courseForm_BtnToolbar = isc.ButtonToolbar.create({
		ID:"courseForm_BtnToolbar",
		buttons: [
			{ name:"copy", title:"Copy", width:50,
				click:function(){
					var record = courseList.getSelectedRecord();
					console.log(record);
					callback(record);
					copyCourseWindow.close();
				}
			},
			{ name:"close", title:"Close", width:50,
				click:function(){
					copyCourseWindow.close();
				}
			}
		]
	});
	var copyCourseWindow = isc.Window.create({
		ID:"courseWindow",
		title:"Training Course",
		width:800,
		height:500,
		isModal:true,
		items:[
			isc.VLayout.create({
				members:[
					courseList,
					courseForm_BtnToolbar
				]
			})
		],
		close: function(){ copyCourseWindow.markForDestroy(); },
	});
	copyCourseWindow.show();
	courseList.fetchData();
	return copyCourseWindow;	
} 

//--------  Course Start map to Seafarer_license ----------
isc.DynamicForm.create({
	ID:"courseDetailDynamicForm", dataSource: "licenseDS", numCols: 2,	 width: 580, height:150,
	fields: [
	         {name: "seafarerId", 	hidden:"true"},
	         {name: "seqNo", 		hidden:"true"},

	         {name: "courseDesc", 	title:"Course Description", 	type:"textArea'",	width:"*", 	length:400}
	         ]
});

isc.ButtonToolbar.create({
	ID:"courseDetailForm_ToolBar", width: 580,
	buttons: [
	          {name:"updateBtn", title:"Save", autoFit: true, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (courseDetailDynamicForm.validate()) {
	        			  courseDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormCourseLG.refresh();
								}
							}, {operationType:"update"});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", autoFit: true,
	        	  click : function () {
	        		  courseDetailDynamicForm.setValues({});
	        		  courseDetailDynamicForm.clearErrors(true);
	        		  courseDetailWindow.hide();
	        	  }
	          },
	          { name:"copyCourse", title:"Copy Course", type:"button", width:100,
					click:function(){
						openCopyCourseForm(function(record){							
							console.log(record);
							//console.log(record.remark);
							courseDetailDynamicForm.getItem("courseDesc").setValue(record.institue + ": " + record.feeCodeEngDesc);
						});
					}
				}
	          ]
});

// create window
isc.Window.create({
	ID:"courseDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Course",
	items: [ courseDetailDynamicForm, courseDetailForm_ToolBar ]
});

isc.ListGrid.create({
	ID:"sfRecFormCourseLG", height: "*", dataSource: "licenseDS",
	fields: [
		{name: "seqNo", 		width:120},
		{name: "courseDesc", 	width:"*"}
	],
	rowDoubleClick:function(record, recordNum, fieldNum){
		openSeafarerCourse(record);
    },
    refresh: function (){
   	 this.setData([]);
   	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
    }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormCourseLG_ToolBar",
	buttons: [
	        {name:"refreshBtn", title:"Refresh", autoFit: true,
	        	click : function () {
	        		sfRecFormCourseLG.refresh();
					  }
			},
	        {name:"createBtn", title:"Create", autoFit: true,
				click : function () {
					openSeafarerCourse(null);
	        	  }
	        },
	       ]
});
isc.SectionVLayout.create({ID:"seafarerCourseLayout", height:180, members: [sfRecFormCourseLG, sfRecFormCourseLG_ToolBar ]});
//--------  Course End ----------
function openSeafarerCourse(record){
	courseDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		courseDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		courseDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}