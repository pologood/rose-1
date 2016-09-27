<!DOCTYPE html>
<html>
<head>
  	<title>配置中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker-bs3.css">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>配置项变更日志</h1>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	            <div class="col-xs-4">
              		<div class="input-group">
                		<span class="input-group-addon">时间</span>
	                	<input type="text" class="form-control" id="filterTime" readonly 
	                		value="<#if startTime?exists && endTime?exists >${startTime?if_exists?string('yyyy-MM-dd HH:mm:ss')} - ${endTime?if_exists?string('yyyy-MM-dd HH:mm:ss')}</#if>" >
	              	</div>
	            </div>
	            <div class="col-xs-3">
	              	<div class="input-group">
	                	<span class="input-group-addon">配置key</span>
	                	<input type="text" class="form-control" id="key" value="<#if key?exists>${key}</#if>" autocomplete="on" >
	              	</div>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-info" id="searchBtn">搜索</button>
	            </div>
          	</div>
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
					<!--
			            <div class="box-header"><h3 class="box-title">调度日志</h3></div>
			            -->
			            <div class="box-body">
			              	<table id="configlog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
					                	<th class="id" >id</th>
					            		<th class="appName" >项目名</th>
					                	<th class="configKey" >key</th>
					                	<th class="configLastValue" >先前value</th>
					                	<th class="configNewValue" >变更后value</th>
					            		<th class="addTime" >变更时间</th>
					                  	<th class="optType" >操作类型</th>
					                  	<th class="operator" >操作人</th>
					                </tr>
				                </thead>
				                <tbody></tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<@netCommon.comAlert />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script>
	var base_url = '${request.contextPath}';
</script>
<script src="${request.contextPath}/static/js/configlog.index.1.js"></script>
</body>
</html>
