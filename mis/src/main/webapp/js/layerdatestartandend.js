var start = {
	elem: "#start",
	format: "YYYY/MM/DD",
	max: "2099-06-16",
	istime: false,
	istoday: false,
	choose: function(datas) {
		end.min = datas;
		end.start = datas
	}
};
var end = {
	elem: "#end",
	format: "YYYY/MM/DD",
	min: laydate.now(),
	max: "2099-06-16",
	istime: false,
	istoday: false,
	choose: function(datas) {
		start.max = datas
	}
};
laydate(start);
laydate(end);