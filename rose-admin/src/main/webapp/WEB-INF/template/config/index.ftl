<!DOCTYPE html>
<html>
<head>
  	<title>配置中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) 
		<section class="content-header"><h1>配置管理</h1></section>
		-->
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	    		<div class="col-xs-4">
	              	<div class="input-group">
	                	<span class="input-group-addon">项目名:</span>
	                	<input type="text" class="form-control" id="appName" autocomplete="on" >
	              	</div>
	            </div>
	            
	            <div class="col-xs-4">
	              	<div class="input-group">
	                	<span class="input-group-addon">配置key:</span>
	                	<input type="text" class="form-control" id="key" autocomplete="on" >
	              	</div>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-info" id="searchBtn">搜索</button>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-warning addRoseConfig" type="button">+新增配置项</button>
	            </div>
          	</div>
	    
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-body">
			              	<table id="config_list" class="table table-bordered table-striped">
				                <thead>
					            	<tr>
					            		<th class="id" >id</th>
					            		<th class="appName" >项目名(应用名)</th>
					                	<th class="configKey" >key</th>
					                	<th class="configDesc" >配置描述</th>
					                	<th class="configValue" >value</th>
					            		<th class="addTime" >创建时间</th>
					            		<th class="updateTime" >更新时间</th>
					                  	<th class="operator" >操作人</th>
					                  	<th class="操作" >操作</th>
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

<!-- 新增模态框 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-sg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >新增配置项</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">项目名 <font color="red">*</font></label>
						<div class="col-sm-5"><input type="text" class="form-control" name="appName" placeholder="项目名" minlength="4" maxlength="100" ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">配置key <font color="red">*</font></label>
						<div class="col-sm-7"><input type="text" class="form-control" name="key" placeholder="配置key" maxlength="100" ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">配置描述 <font color="red">*</font></label>
						<div class="col-sm-9"><input type="text" class="form-control" name="configDesc" placeholder="配置描述" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">配置value <font color="red"></font></label>
						<div class="col-sm-9"><textarea class="form-control" rows="6" name="configValue" maxlength="500" placeholder="配置值"></textarea></div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-5 col-sm-11">
							<button type="submit" class="btn btn-primary">保存</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						</div>
					</div>
				</form>
         	</div>
		</div>
	</div>
</div>

<!-- 更新模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-sg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >编辑配置项</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">项目名 <font color="red">*</font></label>
						<div class="col-sm-5"><input type="text" class="form-control" name="appName" placeholder="项目名" minlength="4" maxlength="100" readonly></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">配置key <font color="red">*</font></label>
						<div class="col-sm-7"><input type="text" class="form-control" name="key" placeholder="配置key" maxlength="100" readonly></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">配置描述 <font color="red">*</font></label>
						<div class="col-sm-9"><input type="text" class="form-control" name="configDesc" placeholder="配置描述" maxlength="200" readonly></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">配置value <font color="red"></font></label>
						<div class="col-sm-9"><textarea class="form-control" rows="6" name="configValue" maxlength="500" placeholder="配置值"></textarea></div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-5 col-sm-11">
							<button type="submit" class="btn btn-primary">更新</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						</div>
					</div>
				</form>
         	</div>
		</div>
	</div>
</div>

<@netCommon.commonScript />
<@netCommon.comAlert />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script>
	var base_url = '${request.contextPath}';
</script>
<script src="${request.contextPath}/static/js/config.index.1.js"></script>
</body>
</html>
