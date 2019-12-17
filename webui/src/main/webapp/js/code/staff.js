createCodeTable2(userDS, 
	[
		{ name: "id"}, 
		{ name: "userName"},
		{ name: "chiName"},
//		{ name: "userPassword"},
		{ name: "userStatus"},
		{ name: "userPasswordTime"},
		{ name: "email"}
	],
	[
		{ name: "id"}, 
		{ name: "userName"},
		{ name: "chiName"},
//		{ name: "userPassword"},
		{ name: "userStatus"},
		{ name: "userPasswordTime"},
		{ name: "email"},
		{ name: "roleIds", 	title:"Roles", type:"select", startRow:false, colSpan:3, width:'*', multiple:true, 
            multipleAppearance:"picklist",  optionDataSource:"roleDS", displayField:"roleCode", valueField:"id"}
	],
	["id"]);