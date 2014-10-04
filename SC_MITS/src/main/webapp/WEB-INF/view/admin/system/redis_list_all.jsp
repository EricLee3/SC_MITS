<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- Order Cancel Modal Layter -->
<div id="md_cancel_order" class="modal fade" tabindex="-1" data-width="450">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
		<h4 class="modal-title">주문 취소</h4>
	</div>
	<div class="modal-body">
		<div class="form-body">
			<div class="form-group">
				<label class="control-label">취소사유
				</label>
				<select class="form-control input-sm" name="cancel_reason">
					<option value="">Select</option>
					<option value="BACKORDER_INFO">Backorder Information</option>
					<option value="CALLED_CUSTOMER">Called Customer</option>
				</select>
			</div>
			<div class="form-group">
				<label class="control-label">Comment
				</label>
				<textarea class="form-control input-sm" rows="3" name="cancel_note"></textarea>
			</div>
			<div class="alert alert-warning">
				<strong>Warning!</strong>
				취소처리를 한 후에는 주문상태를 원복할 수 없습니다.
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<button type="button" data-dismiss="modal" class="btn btn-sm btn-default">Close</button>
		<button type="button" class="btn btn-sm red" id="btn_cancel">Cancel Order</button>
	</div>
</div>


<!-- BEGIN PAGE HEADER-->
<div class="row">
	<div class="col-md-12">
		<!-- BEGIN PAGE TITLE & BREADCRUMB-->
		<h3 class="page-title">
		연동데이타 정보 <small>view order details</small>
		</h3>
		<ul class="page-breadcrumb breadcrumb">
			<!-- <li class="btn-group">
				<button type="button" class="btn blue dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="true">
				<span>Actions</span><i class="fa fa-angle-down"></i>
				</button>
				<ul class="dropdown-menu pull-right" role="menu">
					<li>
						<a href="#">Action</a>
					</li>
					<li>
						<a href="#">Another action</a>
					</li>
					<li>
						<a href="#">Something else here</a>
					</li>
					<li class="divider">
					</li>
					<li>
						<a href="#">Separated link</a>
					</li>
				</ul>
			</li> -->
			<li>
				<i class="fa fa-home"></i>
				<a href="index.html">Home</a>
				<i class="fa fa-angle-right"></i>
			</li>
			<li>
				<a href="#">오더관리</a>
				<i class="fa fa-angle-right"></i>
			</li>
			<li>
				<a href="#">주문 상세 정보</a>
			</li>
		</ul>
		<!-- END PAGE TITLE & BREADCRUMB-->
	</div>
</div>
<!-- END PAGE HEADER-->
<!-- BEGIN PAGE CONTENT-->
<div class="row">
		<div class="col-md-12 col-sm-12">
			<div class="portlet grey-cascade box">
				<div class="portlet-title">
					
					<div class="caption">
						<i class="fa fa-cogs"></i>KOLOR
					</div>
					<!-- <div class="actions">
						<a href="#" class="btn btn-default btn-sm">
						<i class="fa fa-pencil"></i> Edit </a>
					</div> -->
					<!-- <div class="actions">
						<a class="btn red btn-sm" data-toggle="modal" href="javascript:cancelOrderLine();">
						주문취소
						<i class="fa fa-edit"></i>
						</a>
					</div> -->
				</div>
				<div class="portlet-body">
					<!-- Orders -->
					<div class="row">	
						<div class="col-md-6 col-sm-12">
							<h4>주문정보 연동데이타 - SC-MA</h4>
							<div class="table-container">
								<table class="table table-hover table-bordered table-striped" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Data Length
									</th>
									<th>
										 Data
									</th>
								</tr>
								</thead>
								<tbody>
								
								<c:forEach items="${KOLOR.order_ma}" var="list">
								    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
								    
							    <tr>
									<td>
									</td>
									<td>
										 ${list.name}
									</td>
									<td>
										 ${list.size}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
									
								</tr>
								</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
						<div class="col-md-6 col-sm-6">
							<h4>주문정보 연동데이타 - SC-CUBE</h4>
							<div class="table-container">
								<table class="table table-hover table-bordered table-striped" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Data Length
									</th>
									<th>
										 Data
									</th>
								</tr>
								</thead>
								<tbody>
								
								<c:forEach items="${KOLOR.order_ca}" var="list">
								    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
								    
							    <tr>
									<td>
									</td>
									<td>
										 ${list.name}
									</td>
									<td>
										 ${list.size}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
									
								</tr>
								</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
					</div>
					<!-- Product -->
					<div class="row">
						<div class="col-md-6 col-sm-6">
							<h4>상품정보 연동데이타 - SC-MA</h4>
							<div class="table-container">
								<table class="table table-hover table-bordered table-striped" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Data Length
									</th>
									<th>
										 Data
									</th>
								</tr>
								</thead>
								<tbody>
								
								<c:forEach items="${KOLOR.product_ma}" var="list">
								    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
								    
							    <tr>
									<td>
									</td>
									<td>
										 ${list.name}
									</td>
									<td>
										 ${list.size}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
									
								</tr>
								</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
						<div class="col-md-6 col-sm-6">
							<h4>상품정보 연동데이타 - SC-CUBE</h4>
							<div class="table-container">
								<table class="table table-hover table-bordered table-striped" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Data Length
									</th>
									<th>
										 Data
									</th>
								</tr>
								</thead>
								<tbody>
								
								<c:forEach items="${KOLOR.product_ca}" var="list">
								    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
								    
							    <tr>
									<td>
									</td>
									<td>
										 ${list.name}
									</td>
									<td>
										 ${list.size}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
									
								</tr>
								</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
					</div>
					<!-- Inventory -->
					<div class="row">
						<div class="col-md-6 col-sm-6">
							<h4>재고정보 연동데이타 - SC-MA</h4>
							<div class="table-container">
								<table class="table table-hover table-bordered table-striped" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Data Length
									</th>
									<th>
										 Data
									</th>
								</tr>
								</thead>
								<tbody>
								
								<c:forEach items="${KOLOR.inventory_ma}" var="list">
								    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
								    
							    <tr>
									<td>
									</td>
									<td>
										 ${list.name}
									</td>
									<td>
										 ${list.size}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
									
								</tr>
								</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
						<div class="col-md-6 col-sm-6">
							<h4>재고정보 연동데이타 - SC-CUBE</h4>
							<div class="table-container">
								<table class="table table-hover table-bordered table-striped" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Data Length
									</th>
									<th>
										 Data
									</th>
								</tr>
								</thead>
								<tbody>
								
								<c:forEach items="${KOLOR.inventory_ca}" var="list">
								    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
								    
							    <tr>
									<td>
									</td>
									<td>
										 ${list.name}
									</td>
									<td>
										 ${list.size}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
									
								</tr>
								</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
</div>

<form name="form_action" id="form_action" method="POST">
</form>
<!-- END PAGE CONTENT-->

<!-- BEGIN PAGE LEVEL PLUGINS -->
<!-- END PAGE LEVEL PLUGINS -->

<!-- BEGIN PAGE LEVEL SCRIPTS -->
<!-- <script src="../../assets/admin/pages/scripts/custom-orders-view.js"></script>
 --><!-- END PAGE LEVEL SCRIPTS -->

<script>
	jQuery(document).ready(function() {
        
		
        
     });
     
 </script>
<!-- END JAVASCRIPTS -->

</body>
<!-- END BODY -->
</html>