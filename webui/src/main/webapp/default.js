var newTitle = "New";
var editTitle = "Edit";
var promptDeleteMessage = "Are you sure to delete?";
var deleteSuccessfulMessage = "Delete successful!";
var deleteFailMessage = "Delete fail!";
var promptSaveMessage = "Are you sure to save?";
var saveSuccessfulMessage = "Update successful!";
var saveFailMessage = "Update fail!";
var pleaseFetchDataMessage = "Please fetch data first!";

isc.IButton.create({//Logout
	ID: "logoutButton",
	width: 60, layoutVAlign: "top",
	title: "Logout",
	click:function(){
		isc.ask("Are you sure to logout?", function (value){
			if (value){ DMI.call("ssrsApp", "securityDMI", "logout", "location.reload()"); }
		});
	}
});

isc.Label.create({// User ID
	ID: "usIDLabel",
	left: 400, width: 10,
	height:20,
	align: "left", valign: "top",
	wrap: false,
	contents: "<font size='2'><b><font color='red'>" +loginData.ssrsEnv+ "</font> HELLO "+ loginData.userId +"</b></font>"
});
isc.HLayout.create({
	ID : "headerHLayout",
	autoDraw : false,
	width:"100%",//layoutTopMargin: 5,
	height:40,
	members:[
		isc.Img.create({
			ID: "bgIcon",
			src: "marine1.png",
			height: 45, width: 64,
			click: function (){
				isc.showConsole();
			}
		}),
		isc.Label.create({
			padding: 5,
			align: "center", valign: "top",
			width: "100%",
			wrap: false,
			iconSize: 32,
			contents: "<font size='4'><B>Ships and Seafarers Registration System</B></font>"
		}) 
	]
});

isc.TabSet.create({// Tabset
	ID : "mainTabset",
	selectedTab : 0,
	scrollerButtonSize : 0,
	pickerButtonSize : 0,
	opacity : 90,
	defaultTabWidth : 73,
	autoDraw : false,
	showTabPicker: false, 
	showTabScroller: false,
	reset:function(){
		var tabs = this.tabs;
		this.removeTabs(tabs);
	},
	tabBarControls: [
		usIDLabel, isc.LayoutSpacer.create({width: 5}), logoutButton
	],
	tabBarThickness: 22
});

isc.HLayout.create({//Tabset
	ID: "footerHLayout",
	height:40,
	autoDraw:false
});
isc.VLayout.create({
	ID : "indexVLayout",
	autoDraw: false,
	contextMenu: null,
	members : [headerHLayout, mainTabset]
});
viewLoader.setView(indexVLayout);