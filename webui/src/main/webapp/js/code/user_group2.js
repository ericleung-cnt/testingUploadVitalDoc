createCodeTable2(userGroup2DS, 
	[
		{ name: "id"}, 
		{ name: "roleCode"},
		{ name: "engDesc"},
		{ name: "chiDesc"},
		{ name:"officeCode", width:100, optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office" },


	],
	[
		{ name: "roleCode"},
		{ name: "engDesc"},
		{ name: "chiDesc"},
		{name:"officeCode", width:200, startRow:"true" ,optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office"},
	],
	["id"]);