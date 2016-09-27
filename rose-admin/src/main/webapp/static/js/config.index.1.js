$(function() {
	// init date tables
	var jobTable = $("#config_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/config/pageList",
			
	        data : function ( d ) {
	        	var d2 ={};
                d2.appName = $('#appName').val();
                d2.key = $('#key').val();
                d2.start = d.start;
                d2.length = d.length;
                return d2;
            }
	    },
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { "data": 'appName', "bSortable": false, "visible" : false},
	                { "data": 'configKey', "bSortable": false},
	                { "data": 'configDesc', "bSortable": false},
	                { "data": 'configValue', "bSortable": false},
	                { 
	                	"data": 'addTime', 
	                	"bSortable": false, 
	                	"visible" : true, 
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { 
	                	"data": 'updateTime', 
	                	"bSortable": false, 
	                	"visible" : true, 
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'operator', "bSortable": false},
	                { "data": '操作' , "bSortable": false,
	                	"render": function ( data, type, row ) {
	                		return function(){
	                			// log url
	                			var logUrl = base_url +'/logconfig?key='+ row.configKey;
//	                			var _jobData = eval('(' + row.jobData + ')');
	                			var html = '<p key="'+ row.configKey +'" ' + 
				                			' appName="'+ row.appName +'" ' +
				                			' configValue="'+ row.configValue +'" ' +
			                				' configDesc="'+ row.configDesc +'" ' +
	                						'>'+
	                					'<textarea name="configValue" style="display:none;" >'+ row.configValue +'</textarea>'+
										'<button class="btn btn-info btn-xs update" type="button">编辑</button> <br> '+
									  	'<button class="btn btn-danger btn-xs _operate" type="_del" type="button">删除</button>  '+
									  	'<button class="btn btn-warning btn-xs" type="button" '+
									  		'onclick="javascript:window.open(\'' + logUrl + '\')" >变更日志</button>'+
									'</p>';
	                			return html;
	                		};
	                	}
	                }
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
	$('#searchBtn').on('click', function(){
		jobTable.fnDraw();
	});
	
	
//	// 新增-添加参数
//	$("#addModal .addParam").on('click', function () {
//		var html = '<div class="form-group newParam">'+
//				'<label for="lastname" class="col-sm-2 control-label">参数&nbsp;<button class="btn btn-danger btn-xs removeParam" type="button">移除</button></label>'+
//				'<div class="col-sm-4"><input type="text" class="form-control" name="key" placeholder="请输入参数key[将会强转为String]" maxlength="200" /></div>'+
//				'<div class="col-sm-6"><input type="text" class="form-control" name="value" placeholder="请输入参数value[将会强转为String]" maxlength="200" /></div>'+
//			'</div>';
//		$(this).parents('.form-group').parent().append(html);
//		
//		$("#addModal .removeParam").on('click', function () {
//			$(this).parents('.form-group').remove();
//		});
//	});
//	

	//===================新增================
	$(".addRoseConfig").click(function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	appName : {  
        		required : true ,
                minlength: 4,
                maxlength: 64,
                myValid01:true
            },  
            key : {  
        		required : true ,
                minlength: 4,
                maxlength: 100,
                myValid01:true
            },  
            configDesc : {  
            	required : true ,
                maxlength: 200
            },
            configValue : {
            	required : false ,
                maxlength: 500
            }
        }, 
        messages : {  
        	appName : {  
        		required :"请输入“项目名”"  ,
                minlength:"长度不应低于4位",
                maxlength:"长度不应超过64位"
            },  
            key : {  
        		required :"请输入“配置key”"  ,
                minlength:"长度不应低于4位",
                maxlength:"长度不应超过100位"
            },  
            configDesc : {
            	required :"请输入“配置描述”"  ,
                maxlength:"长度不应超过200位"
            },  
            configValue : {
            	required :"请输入“配置值”"  ,
                maxlength:"长度不应超过500位"
            }
        }, 
		highlight : function(element) {  
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		$.post(base_url + "/config/addRose", $("#addModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				ComAlert.show(1, "新增配置项成功", function(){
    					window.location.reload();
    				});
    			} else {
    				if (data.msg) {
    					ComAlert.show(2, data.msg);
    				} else {
    					ComAlert.show(2, "新增失败");
    				}
    			}
    		});
    		
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form .form-group").removeClass("has-error");
		addModalValidate.resetForm();
	});
	
	//=================更新================
	$("#config_list").on('click', '.update',function() {
		$("#updateModal .form input[name='appName']").val($(this).parent('p').attr("appName"));
		$("#updateModal .form input[name='key']").val($(this).parent('p').attr("key"));
//		$("#updateModal .form textarea[name='configValue']").val($(this).parent('p').attr("configValue"));
		$("#updateModal .form textarea[name='configValue']").val( $(this).parent('p').find("textarea[name='configValue']").val());
		$("#updateModal .form input[name='configDesc']").val($(this).parent('p').attr("configDesc"));
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
            configValue : {
            	required : false ,
                maxlength: 500
            }
        }, 
        messages : {  
        	configValue : {
            	required :"请输入“配置值”"  ,
                maxlength:"长度不应超过500位"
            }
        }, 
		highlight : function(element) {  
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		$.post(base_url + "/config/updateRose", $("#updateModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				ComAlert.show(1, "更新成功", function(){
    					$('#updateModal').modal({backdrop: false, keyboard: false}).modal('hide');
    					jobTable.fnDraw();
    				});
    			} else {
    				if (data.msg) {
    					ComAlert.show(2, data.msg);
					} else {
						ComAlert.show(2, "更新失败");
					}
    			}
    		});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset()
	});
	
	//==================删除==================
	$("#config_list").on('click', '._operate',function() {
		var typeName;
		var url;
		var type = $(this).attr("type");
		if ("job_pause" == type) {
			typeName = "暂停";
			url = base_url + "/job/pause";
		}  else if ("_del" == type) {
			typeName = "删除";
			url = base_url + "/config/removeRose";
		} else {
			return;
		}
		
		var key = $(this).parent('p').attr("key");
		
		ComConfirm.show("确认" + typeName + "?", function(){
			$.ajax({
				type : 'POST',
				url : url,
				data : {"key":key},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, typeName + "成功", function(){
							jobTable.fnDraw();
						});
					} else {
						ComAlert.show(1, typeName + "失败");
					}
				},
			});
		});
	});
	
	
	//============= jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”========
	jQuery.validator.addMethod("myValid01", function(value, element) {
		var length = value.length;
		var valid = /^[a-zA-Z][a-zA-Z0-9_\.\-]*$/;
		return this.optional(element) || valid.test(value);
	}, "只支持英文字母开头，只含有英文字母、数字和下划线");
	
});
