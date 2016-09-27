$(function() {

	// init date tables
	var logTable = $("#configlog_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
	        url: base_url + "/logconfig/pageList" ,
	        data : function ( d ) {
                d.filterTime = $('#filterTime').val();
                d.key = $('#key').val();
            }
	    },
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { "data": 'appName', "bSortable": false, "visible" : false},
	                { "data": 'configKey', "bSortable": false},
	                { "data": 'configLastValue', "bSortable": false, "render": function ( data, type, row ){
	                	if(data == null){
	                		return "null";
	                	}
	                	return data;
	                }
	                },
	                { "data": 'configNewValue', "bSortable": false, "render": function ( data, type, row ){
	                	if(data == null){
	                		return "null";
	                	}
	                	return data;
                	}
	                },
	                { "data": 'addTime', "bSortable": false, "render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'optTypeStr' , "bSortable": false},
	                { "data": 'operator' , "bSortable": false}
	            ],
	    "searching": false,
	    "ordering": true,
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	
//	$('#joblog_list').on('click', '.logTips', function(){
//		var title = $(this).attr('title2');
//		ComAlert.show(2, title);
//	});
//	
//	$('#joblog_list').on('click', '.logMsg', function(){
//		var title = $(this).attr('title');
//		ComAlertTec.show('<pre style="color: white;background-color: black;width2:'+ $(window).width()*2/3 +'px;" >'+ title +'</pre>');
//	});
	
//	$('#joblog_list').on('click', '.logDetail', function(){
//		var _id = $(this).attr('_id');
//		
//		window.open(base_url + 'joblog/ferrariLogDetailPage?id=' + _id);
//		return;
//		
//		$.ajax({
//			type : 'POST',
//			url : base_url + 'joblog/ferrariLogDetail',
//			data : {"id":_id},
//			dataType : "json",
//			success : function(data){
//				if (data.code == 200) {
//					var html = "";
//					if(data.ferrariFeedback.status){
//						html = data.ferrariFeedback.content;	
//					} else {
//						html = data.ferrariFeedback.errormsg;
//					}
//					ComAlertTec.show('<pre style="color: white;background-color: black;width2:'+ $(window).width()*2/3 +'px;" >'+ html +'</pre>');
//				} else {
//					ComAlertTec.show(data.msg);
//				}
//			},
//		});
//		
//	});
	
//	$('#joblog_list').on('click', '.logKill', function(){
//		var _id = $(this).attr('_id');
//		ComConfirm.show("确认主动终止任务?", function(){
//			$.ajax({
//				type : 'POST',
//				url : base_url + 'joblog/ferrariJobKill',
//				data : {"id":_id},
//				dataType : "json",
//				success : function(data){
//					if (data.code == 200) {
//						ComAlert.show(1, '操作成功');
//						logTable.fnDraw();
//					} else {
//						ComAlert.show(2, data.msg);
//					}
//				},
//			});
//		});
//	});
	
	
	$('#filterTime').daterangepicker({
		timePicker: true,
		timePickerIncrement: 10,
		timePicker12Hour : false,
		format: 'YYYY-MM-DD HH:mm:ss',
		separator : ' - ',
		ranges : {
            '最近1小时': [moment().subtract('hours',1), moment()],
            '今日': [moment().startOf('day'), moment()],
            '昨日': [moment().subtract('days', 1).startOf('day'), moment().subtract('days', 1).endOf('day')],
            '最近7日': [moment().subtract('days', 6), moment()],
            '最近30日': [moment().subtract('days', 29), moment()]
        },
        opens : 'right',
        locale : {
        	customRangeLabel : '自定义',
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            toLabel : '结束时间',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1
        }
	});
	
	$('#searchBtn').on('click', function(){
		logTable.fnDraw();
	});
	
});
