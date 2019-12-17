var txCodeMap = {};
var txListCodeMap = {};

transactionCodeDS.fetchData(null,
        function (dsResponse, data) {
	for (var i = 0; i < data.length; i++) {
		txCodeMap[data[i].id] = data[i].id + " " + data[i].tcDesc;
		txListCodeMap[data[i].id] = data[i].tcDesc;
	}
	}
);
var grid = isc.ListGrid.create({
	height:600,
	showFilterEditor:true,
	dataSource:"txnDS",
	fields:[
		{name:"applNo", title:"Appl No.",},
		{name:"code", title:"Code" },
		{name:"codeDesc", title:"Code Description", formatCellValue:function(value, record, rowNum, colNum, grid) {
			return txListCodeMap[record.code];
		}, valueMap:txListCodeMap, canEdit:false, canFilter:false},
		{name:"transactionTime", title:"Transaction Time", },
		{name:"userId", title:"User ID", },
		{name:"dateChange", title:"Date Change", format:"dd/MM/yyyy"},
		{name:"hourChange", title:"Hour Change", },
		{name:"details", title:"Details", },
	],
	rowDoubleClick: function(record, row, col) {
		var form = isc.DynamicForm.create(
				{
					dataSource:txnDS,
					width:580,
					fields:
						[
							{name:"id", type:"hidden"},
							{name:"applNo", title:"Appl No.", type:"staticText"},
							{name:"code", title:"Code", type:"staticText", valueMap:txCodeMap },
							{name:"transactionTime", title:"Transaction Time", type:"staticText", format:"dd/MM/yyyy HH:mm:ss"},
							{name:"userId", title:"User ID", width:200, },
							{name:"dateChange", width:200},
							{name:"hourChange", width:200},
							{name:"details", width:400},
						]
				});
		form.setData(record);
		var buttons = isc.HLayout.create(
				{
					align:"right",
					members:
						[isc.Button.create(
							{
								title:"Save",
								click:function() {
									if (form.validate()) {
										txnDS.updateData(form.getData(), function() {
											win.close();
										}  );
									}
								}}
							)]
				}
		);
		console.log("show transaction " + record);
		var win = isc.Window.create({
			height:340,
			width:600,
			title:"Transaction",
			items:[form, buttons]
		});
		win.show();
	}
});
grid.fetchData();
isc.VLayout.create({
	members:[
		grid,
		isc.HLayout.create({
			align:"right",
			members:
				[

				]
		}),
	]
});