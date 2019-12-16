createCodeTable2(courseCodeDS,[
	{name:"id", width:100},
	{name:"institue", width:250},
	{name:"fcFeeCode", width:100},
	{name:"feeCodeEngDesc", width:"*"},
	{name:"status", width:100}
],[
	{name:"id", type:"staticText", required:false},
	{name:"institue", width:300},
	{name:"fcFeeCode", width:300, optionDataSource:"feeCodeDS", valueField:"id", displayField:"engDesc"},
	{name:"status"}
],["id"]);