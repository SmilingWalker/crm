<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


	<script type="text/javascript">

	$(function(){



        $(".time").datetimepicker({
            minView: "month",
            language:  'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true,
            pickerPosition: "bottom-left"
        })
		/**
		 * 打开创建活动模态窗口
		 */
		$("#createModal").click(function () {
			$("#create-activityForm")[0].reset()
			/**
			 * 修改模态窗口内的所有者信息
			 * 发送 ajax 请求到后端，获得当前所有的用户信息，并且将当前用户设置为登录用户
			 */
			let html = "";
			$.ajax({
				url:"workbench/activity/getUserList.do",
				type:"get",
			dataType:"json",
			success:function (data) {

				/**
				 * 返回用户列表，根据用户列表进行Html更新
				 */
				$.each(data,function (i,n) {
					html+= "<option value="+n.id+">"+n.name+ "</option>";
				});
                $("#create-marketActivityOwner").html(html);
                $("#create-marketActivityOwner").val("${user.id}")
			}
		})

			$("#createActivityModal").modal("show");
		})
		// 保存当前新创建的活动
        $("#create-save").click(function () {

            // 发送ajax请求

			$.ajax({
				url:"workbench/activity/saveActivity.do",
				type:"post",
			dataType:"json",
				data:{
					owner:$("#create-marketActivityOwner").val(),
					name:$("#create-marketActivityName").val(),
					startDate:$("#StartDate").val(),
					endDate:$("#endDate").val(),
					cost:$("#create-cost").val(),
					description:$("#create-describe").val()
				},

				success:function (data){
					// 两种情况，添加活动成功和添加失败
					//{success:boolean}
					if(!data.success){
						alert("添加失败")
					}else {
						//添加成功
						refreshPageList($("#activityPage").bs_pagination('getOption', 'rowsPerPage')
								,$("#activityPage").bs_pagination('getOption', 'currentPage'));

						$("#createActivityModal").modal("hide");
					}
				}
			})
        })

		$("#QueryByCondition").click(function () {
			$("#hide-name").val($.trim($("#query-name").val()));
			$("#hide-owner").val($.trim($("#query-owner").val()))
			$("#hide-startDate").val($.trim($("#query-startDate").val()))
			$("#hide-endDate").val($.trim($("#query-endDate").val()))
			// 条件查询，根据当前获得的条件进行查询
            refreshPageList($("#activityPage").bs_pagination('getOption', 'rowsPerPage')
                ,1);
		})
		refreshPageList(2,1);

        //全选的选择框
		$("#selectall_box").click(function () {
		    $("input[name=select_box]").prop("checked",this.checked);
        })

        $("#query-pagelist").on("click",$("input[name=select_box]"),function () {
            $("#selectall_box").prop("checked",$("input[name=select_box]").length==$("input[name=select_box]:checked").length)
        })

        $("#delete").click(function () {
            let $selection = $("input[name=select_box]:checked");
            if ($selection.length===0){
                alert("当前未选择任何的活动项");
            }
            let param = "";
            for (let i = 0; i <$selection.length ; i++) {
				param += "id="+$($selection[i]).val()
				if (i!=($selection.length-1)){
					param+="&"
				}
            }
            if(confirm("是否确定删除")){
				$.ajax({
					url:"workbench/activity/delete.do",
					data:param,
					type:"post",
					dataType:"json",
					success:function (data) {
						//{success:false}
						if (data.success){
                            refreshPageList($("#activityPage").bs_pagination('getOption', 'rowsPerPage')
                                ,1);
						}
						else {
							alert("删除操作失败，请检查");
						}

					}
				})
			}

        })

		$("#editActivityBtn").click(function () {
			let $selection = $("input[name=select_box]:checked");

			if ($selection.length===0){
				alert("请选择一条记录");
			}else if($selection.length>1){
				alert("只能修改单条记录");
			}else {
				//需要走后台，发送ajax请求，获得用户列表和活动项
				let id = $selection.val();
				$.ajax({
					url:"workbench/activity/getUserListAndAct.do",
					type:"post",
				dataType:"json",
					data:{
						"id":id
					},
				success:function (data) {
						//{ulist:[user,user],activity:}
					let options = ""
					$.each(data.ulist,function (i,n) {
						options += "<option value='"+n.id+"'>"+n.name+"</option>"
					})
					let activity = data.activity;
					$("#edit-id").val(activity.id);
					$("#edit-owner").html(options);
					$("#edit-owner").val(activity.owner);
					$("#edit-name").val(activity.name);
					$("#edit-startDate").val(activity.startDate);
					$("#edit-endDate").val(activity.endDate);
					$("#edit-cost").val(activity.cost);
					$("#edit-description").val(activity.description);

					$("#editActivityModal").modal("show");
				}
			})
			}
		})

		$("#updateBtn").click(function () {

			// 发送ajax请求

			$.ajax({
				url:"workbench/activity/updateAct.do",
				type:"post",
				dataType:"json",
				data:{
					id: $("#edit-id").val(),
					owner:$("#edit-owner").val(),
					name:$("#edit-name").val(),
					startDate:$("#edit-startDate").val(),
					endDate:$("#edit-endDate").val(),
					cost:$("#edit-cost").val(),
					description:$("#edit-description").val()
				},

				success:function (data){
					// 两种情况，添加活动成功和添加失败
					//{success:boolean}
					if(!data.success){
						alert("修改内容失败")
					}else {
						//添加成功
						$("#editActivityModal").modal("hide");
                        refreshPageList($("#activityPage").bs_pagination('getOption', 'rowsPerPage')
                            ,1);
					}
				}
			})

		})
	});

	/**
	 * 活动列表刷新的函数 分页查询
	 * @param pageSize 单页条数
	 * @param pageNum 当前页面数
	 *
	 * 主要在几个地方使用
	 * 1.首先是页面刚进入时，进行列表进行全刷新
	 * 2.其次，活动进行增加、删除时进行刷新
	 * 3.点击选择页面的时候需要进行刷新
	 * 4.点击查询按钮的时候需要进行刷新
	 */
	function refreshPageList(pageSize,pageNum){
		// 刷新全选框

		$("#selectall_box").prop("checked",false)

		//重新为输入框赋值

		$("#query-name").val($.trim($("#hide-name").val()));
		$("#query-owner").val($.trim($("#hide-owner").val()))
		$("#query-startDate").val($.trim($("#hide-startDate").val()))
		$("#query-endDate").val($.trim($("#hide-endDate").val()))

		$.ajax({
			url:"workbench/activity/getActivityByCondition.do",
			type:"get",
		dataType:"json",
			data:{
				"pageSize":pageSize,
				"pageNum":pageNum,
				"name":$.trim($("#query-name").val()),
				"owner":$.trim($("#query-owner").val()),
				"startDate":$.trim($("#query-startDate").val()),
				"endDate":$.trim($("#query-endDate").val()),
			},
		success:function (data) {
				//返回值包含两个内容 1.total 表示当前所有的查询条数 2.活动列表dataList
			let total = data.total;
			let datalist = data.dataList;
			html = "";
			$.each(datalist,function (i,n) {
				html += '<tr class="active">'
				html+= '<td><input type="checkbox" name="select_box" value="'+n.id+'" /></td>'
				html+=
						'<td><a style="text-decoration: none; cursor: pointer;"onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\'">'+n.name+'</a></td>'
				html+= '<td>'+n.owner+'</td>'
				html+= '<td>'+n.startDate+'</td>'
				html+= '<td>'+n.endDate+'</td>'
				html+= '</tr>'
			})
			$("#query-pagelist").html(html);

			let totalPages = Math.ceil(parseInt(total)/parseInt(pageSize));

			$("#activityPage").bs_pagination({
				currentPage: pageNum, // 页码
				rowsPerPage: pageSize, // 每页显示的记录条数
				maxRowsPerPage: 20, // 每页最多显示的记录条数
				totalPages: totalPages, // 总页数
				totalRows: data.total, // 总记录条数

				visiblePageLinks: 3, // 显示几个卡片

				showGoToPage: true,
				showRowsPerPage: true,
				showRowsInfo: true,
				showRowsDefaultInfo: true,

				onChangePage : function(event, data){
                    refreshPageList($("#activityPage").bs_pagination('getOption', 'rowsPerPage')
                        ,$("#activityPage").bs_pagination('getOption', 'currentPage'));
				}
			});


		}
	})
	}

</script>
</head>
<body>

	<input style="visibility: hidden" id="hide-name">
	<input style="visibility: hidden" id="hide-owner">
	<input style="visibility: hidden" id="hide-startDate">
	<input style="visibility: hidden" id="hide-endDate">


	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="create-activityForm">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="StartDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="StartDate">
							</div>
							<label for="endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time"  id="endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="create-save">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<input style="visibility: hidden" id="edit-id">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">
								</select>
							</div>
                            <label for="edit-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="query-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="query-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="query-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="query-endDate">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="QueryByCondition">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" data-toggle="modal" id="createModal"><span
						  class="glyphicon glyphicon-plus"></span> 创建
				  </button>
				  <button type="button" class="btn btn-default" data-toggle="modal" id="editActivityBtn"><span
						  class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="delete"><span
                          class="glyphicon glyphicon-minus"></span> 删除
                  </button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="selectall_box" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="query-pagelist">
					</tbody>
				</table>
			</div>
			
			<div id="activityPage">

			</div>
			
		</div>
		
	</div>
</body>
</html>