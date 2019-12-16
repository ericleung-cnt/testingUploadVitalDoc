console.log("stop_list.js");
createCodeTable2(stopListDS, 
	[
		{ name: "id", width:100}, 
		{ name: "serbNo", width: 120},
		{ name: "desc"}
	],
	[
		{ name: "id", type: "staticText"}, 
		{ name: "serbNo", required: true, length:8},
		{ name: "desc", length:50, colSpan:3, width:500}

	],
	["id"], 'paged');