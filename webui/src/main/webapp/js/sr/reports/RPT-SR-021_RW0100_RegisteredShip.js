simpleSrReport("Registered Ships Report","RPT_SR_021",
		[
		 {name:"reportFrom", title:"From Date", type:"date", dateFormatter:"dd/MM/yyyy", required:true, defaultValue:new Date()},
		 {name:"reportTo", title:"To Date", type:"date", dateFormatter:"dd/MM/yyyy", required:true, defaultValue:new Date()},

		 ]);