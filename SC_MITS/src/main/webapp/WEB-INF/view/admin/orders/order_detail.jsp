<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- BEGIN PAGE HEADER-->
<div class="row">
	<div class="col-md-12">
		<!-- BEGIN PAGE TITLE & BREADCRUMB-->
		<h3 class="page-title">
		주문 상세 정보 <small>view order details</small>
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
	<div class="col-md-12">
		<!-- Begin: life time stats -->
		<div class="portlet">
			<div class="portlet-title">
				<div class="caption">
					<i class="fa fa-shopping-cart"></i>오더번호: ${orderNo} | 오더 생성일: <span class="hidden-480" id="title_order_date">
					${baseInfo.orderDate} <!-- Dec 27, 2013 7:16:25 --> </span>
				</div>
				<div class="actions">
					<a href="/admin/orders/order_list.html?orderNo=${baseInfo.orderNo}" class="btn default yellow-stripe ajaxify">
					<i class="fa fa-angle-left"></i>
					<span class="hidden-480">
					Back </span>
					</a>
					<a href="orders/orderDetail.do?docType=${docType}&entCode=${entCode}&orderNo=${orderNo}" class="btn default yellow-stripe ajaxify" id="tool_reload">
					<i class="fa fa-refresh"></i>
					<span class="hidden-480">
					Reload </span>
					</a>
					<a class="btn btn-sm red" href="javascript:;" id="tool_schedule">
					Release
					<i class="fa fa-edit"></i>
					</a>
					<a class="btn btn-sm red" href="javascript:;" id="tool_cancel">
					Cancel
					<i class="fa fa-edit"></i>
					</a>
					
					<!-- <div class="btn-group">
						<a class="btn default yellow-stripe" href="#" data-toggle="dropdown">
						<i class="fa fa-cog"></i>
						<span class="hidden-480">
						Tools </span>
						<i class="fa fa-angle-down"></i>
						</a>
						<ul class="dropdown-menu pull-right">
							<li>
								<a href="#">
								Export to Excel </a>
							</li>
							<li>
								<a href="#">
								Export to CSV </a>
							</li>
							<li>
								<a href="#">
								Export to XML </a>
							</li>
							<li class="divider">
							</li>
							<li>
								<a href="#">
								Print Invoice </a>
							</li>
							<li id="tool_schedule">
								<a href="#">
								Schedule </a>
							</li>
							<li id="tool_release">
								<a href="#">
								Release </a>
							</li>
							<li class="divider">
							</li>
							<li>
								<a href="#">
								 Cancel Order</a>
							</li>
						</ul>
					</div> -->
				</div>
			</div>
			<div class="portlet-body">
				<div class="tabbable">
					<ul class="nav nav-tabs nav-tabs-lg">
						<li class="active">
							<a href="#tab_1" data-toggle="tab">
							주문상세정보 </a>
						</li>
						<li>
							<a href="#tab_2" data-toggle="tab">
							Contact History </a>
						</li>
						<!-- <li>
							<a href="#tab_3" data-toggle="tab">
							Invoices <span class="badge badge-success">
							4 </span>
							</a>
						</li> -->
						<li>
							<a href="#tab_4" data-toggle="tab">
							출고확정정보 <span class="badge badge-danger">
							2 </span>
							</a>
						</li>
						<li>
							<a href="#tab_5" data-toggle="tab">
							History </a>
						</li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="tab_1">
							<div class="row">
								<div class="col-md-6 col-sm-12">
									<div class="portlet yellow-crusta box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Order Details
											</div>
											<div class="actions">
											</div>
										</div>
										<div class="portlet-body">
											<div class="row static-info">
												<div class="col-md-5 name">
													 Order #:
												</div>
												<div class="col-md-7 value">
													 ${baseInfo.orderNo}  <!-- <span class="label label-info label-sm">
													Email confirmation was sent </span> -->
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Order Date & Time:
												</div>
												<div class="col-md-7 value" id="order_date">
													 
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Order Status:
												</div>
												<div class="col-md-7 value">
													<span class="label label-${baseInfo.orderStatus_class }">
													${baseInfo.orderStatus} </span>
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Enterprise/Seller :
												</div>
												<div class="col-md-7 value">
													${baseInfo.entCode} / ${baseInfo.sellerCode}
												</div>
											</div>
											<%-- <div class="row static-info">
												<div class="col-md-5 name">
													 Total Amount:
												</div>
												<div class="col-md-7 value">
													${baseInfo.currency}&nbsp;&nbsp;${baseInfo.totalAmount}
												</div>
											</div> --%>
										</div>
									</div>
								</div>
								<div class="col-md-6 col-sm-12">
									<div class="portlet blue-hoki box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Customer Information
											</div>
											<div class="actions">
											</div>
										</div>
										<div class="portlet-body">
											<div class="row static-info">
												<div class="col-md-5 name">
													 Customer Name:
												</div>
												<div class="col-md-7 value">
													 ${custInfo.custName}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Phone Number:
												</div>
												<div class="col-md-7 value">
													 ${custInfo.custPhone}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Email:
												</div>
												<div class="col-md-7 value">
													 ${custInfo.custEmail}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Payment Information:
												</div>
												<div class="col-md-7 value">
													 ${baseInfo.paymentType }
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-12">
									<div class="portlet green-meadow box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Billing Address
											</div>
											<div class="actions">
											</div>
										</div>
										<div class="portlet-body">
											<div class="row static-info">
												<div class="col-md-12 value">
													 ${billInfo.billName}<br>
													 ${billInfo.billAddr1}<br>
													 ${billInfo.billAddr2}<br>
													 ${billInfo.billCity}&nbsp;${billInfo.billState}&nbsp;${billInfo.billZipcode}<br>
													 <br>
													 Phone: ${billInfo.billPhone}&nbsp;${billInfo.billMPhone} <br>
													 Fax: ${billInfo.billFaxNo}<br>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-4 col-sm-12">
									<div class="portlet red-sunglo box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Shipping Address
											</div>
											<div class="actions">
											</div>
										</div>
										<div class="portlet-body">
											<div class="row static-info">
												<div class="col-md-12 value">
													 ${billInfo.billName}<br>
													 ${billInfo.billAddr1}<br>
													 ${billInfo.billAddr2}<br>
													 ${billInfo.billCity}&nbsp;${billInfo.billState}&nbsp;${billInfo.billZipcode}<br>
													 <br>
													 Phone: ${billInfo.billPhone}&nbsp;${billInfo.billMPhone} <br>
													 Fax: ${billInfo.billFaxNo}<br>
												</div>
											</div>
										</div>
									</div>
								</div>
								
								<div class="col-md-4 col-sm-12">
									<div class="portlet purple-plum box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Charges
											</div>
											<div class="actions">
												<!-- <a href="#" class="btn btn-default btn-sm">
												<i class="fa fa-pencil"></i> Edit </a> -->
											</div>
										</div>
										<div class="portlet-body">
											<div class="row static-info">
												<div class="col-md-5 name">
													 Line Sub Total:
												</div>
												<div class="col-md-7 value text-right">
													 ${baseInfo.totalLine}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Total Charges:
												</div>
												<div class="col-md-7 value text-right">
													 ${baseInfo.totalCharge}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Total Tax:
												</div>
												<div class="col-md-7 value text-right">
													 ${baseInfo.totalTax}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Total Discount:
												</div>
												<div class="col-md-7 value text-right">
													 ${baseInfo.totalDiscount}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Grand Total:
												</div>
												<div class="col-md-7 value text-right">
													 ${baseInfo.currency}&nbsp;&nbsp;${baseInfo.totalAmount}
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 col-sm-12">
									<div class="portlet grey-cascade box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>OrderLine
											</div>
											<!-- <div class="actions">
												<a href="#" class="btn btn-default btn-sm">
												<i class="fa fa-pencil"></i> Edit </a>
											</div> -->
										</div>
										<div class="portlet-body">
											<div class="table-responsive">
												<table class="table table-hover table-bordered table-striped">
												<thead>
												<tr>
													<th>
														 No.
													</th>
													<th>
														 Item Id
													</th>
													<th>
														 Item Name
													</th>
													<th>
														 Line Status
													</th>
													<th>
														 Quantity
													</th>
													<th>
														 UnitPrice
													</th>
													<th>
														 ShipNode
													</th>
													<th>
														 Charge
													</th>
													<th>
														 Tax
													</th>
													<th>
														 Discount
													</th>
													<th>
														 Total
													</th>
												</tr>
												</thead>
												<tbody>
												
												<c:forEach items="${lineInfoList}" var="line">
												    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
												    
											    <tr>
											    	<td>
														<a href="#" target="_blank">
														${line.PrimeLineNo}</a>
													</td>
													<td>
														${line.itemId}
														<a class="btn btn-xs blue-hoki pull-right" href="${line.itemDesc}" target="_blank"> Details </a>
													</td>
													<td>
														 ${line.itemdShortDesc}
													</td>
													<td>
														<span class="label label-sm label-${line.status_class}">
														${line.status}</span>
													</td>
													<td class="text-right">
														 ${line.qty}
													</td>
													<td class="text-right">
														 ${line.UnitPrice}
													</td>
													<td>
														 ${line.shipNode}
													</td>
													<td  class="text-right">
														 ${line.lineShipCharge}
													</td>
													<td  class="text-right">
														 ${line.lineTax}
													</td>
													<td class="text-right">
														 ${line.lineDisount}
													</td>
													<td class="text-right">
														 ${line.lineTatal}
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
							
							<!-- START  Order Amount Summary Area -->
							<!-- 
							<div class="row">
								<div class="col-md-6">
								</div>
								<div class="col-md-6">
									<div class="well">
										<div class="row static-info align-reverse">
											<div class="col-md-8 name">
												 Sub Total:
											</div>
											<div class="col-md-3 value">
												 $1,124.50
											</div>
										</div>
										<div class="row static-info align-reverse">
											<div class="col-md-8 name">
												 Shipping:
											</div>
											<div class="col-md-3 value">
												 $40.50
											</div>
										</div>
										<div class="row static-info align-reverse">
											<div class="col-md-8 name">
												 Grand Total:
											</div>
											<div class="col-md-3 value">
												 $1,260.00
											</div>
										</div>
										<div class="row static-info align-reverse">
											<div class="col-md-8 name">
												 Total Paid:
											</div>
											<div class="col-md-3 value">
												 $1,260.00
											</div>
										</div>
										<div class="row static-info align-reverse">
											<div class="col-md-8 name">
												 Total Refunded:
											</div>
											<div class="col-md-3 value">
												 $0.00
											</div>
										</div>
										<div class="row static-info align-reverse">
											<div class="col-md-8 name">
												 Total Due:
											</div>
											<div class="col-md-3 value">
												 $1,124.50
											</div>
										</div>
									</div>
								</div>
							</div> -->
							
							<!-- END  Order Amount Summary Area -->
							
						</div>
						
						
						<div class="tab-pane" id="tab_2">
							<div class="table-container">
								<table class="table table-striped table-bordered table-hover" id="datatable_credit_memos">
								<thead>
								<tr role="row" class="heading">
									<th width="15%">
										 Date
									</th>
									<th width="10%">
										 User
									</th>
									<th width="12%">
										 Reason
									</th>
									<th width="15%">
										 Contact Type
									</th>
									<th width="15%">
										 Contact Reference
									</th>
									<th width="" class="text-center">
										 Notes
									</th>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="${noteList}" var="note">
									<tr>
								    	<td>
											${note.noteDate}
										</td>
										<td>
											${note.noteUser}
										</td>
										<td>
											 ${note.noteReason}
										</td>
										<td>
											${note.noteContactType}
										</td>
										<td>
											 ${note.noteContactRef}
										</td>
										<td>
											 ${note.noteText}
										</td>
									</tr>
									
									</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
						
						
						<!-- 
						
						<div class="tab-pane" id="tab_3">
							<div class="table-container">
								<div class="table-actions-wrapper">
									<span>
									</span>
									<select class="table-group-action-input form-control input-inline input-small input-sm">
										<option value="">Select...</option>
										<option value="pending">Pending</option>
										<option value="paid">Paid</option>
										<option value="canceled">Canceled</option>
									</select>
									<button class="btn btn-sm yellow table-group-action-submit"><i class="fa fa-check"></i> Submit</button>
								</div>
								<table class="table table-striped table-bordered table-hover" id="datatable_invoices">
								<thead>
								<tr role="row" class="heading">
									<th width="5%">
										<input type="checkbox" class="group-checkable">
									</th>
									<th width="5%">
										 Invoice&nbsp;#
									</th>
									<th width="15%">
										 Bill To
									</th>
									<th width="15%">
										 Invoice&nbsp;Date
									</th>
									<th width="10%">
										 Amount
									</th>
									<th width="10%">
										 Status
									</th>
									<th width="10%">
										 Actions
									</th>
								</tr>
								<tr role="row" class="filter">
									<td>
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="order_invoice_no">
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="order_invoice_bill_to">
									</td>
									<td>
										<div class="input-group date date-picker margin-bottom-5" data-date-format="dd/mm/yyyy">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_invoice_date_from" placeholder="From">
											<span class="input-group-btn">
											<button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
										<div class="input-group date date-picker" data-date-format="dd/mm/yyyy">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_invoice_date_to" placeholder="To">
											<span class="input-group-btn">
											<button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
									</td>
									<td>
										<div class="margin-bottom-5">
											<input type="text" class="form-control form-filter input-sm" name="order_invoice_amount_from" placeholder="From"/>
										</div>
										<input type="text" class="form-control form-filter input-sm" name="order_invoice_amount_to" placeholder="To"/>
									</td>
									<td>
										<select name="order_invoice_status" class="form-control form-filter input-sm">
											<option value="">Select...</option>
											<option value="pending">Pending</option>
											<option value="paid">Paid</option>
											<option value="canceled">Canceled</option>
										</select>
									</td>
									<td>
										<div class="margin-bottom-5">
											<button class="btn btn-sm yellow filter-submit margin-bottom"><i class="fa fa-search"></i> Search</button>
										</div>
										<button class="btn btn-sm red filter-cancel"><i class="fa fa-times"></i> Reset</button>
									</td>
								</tr>
								</thead>
								<tbody>
								</tbody>
								</table>
							</div>
						</div>
						
						<div class="tab-pane" id="tab_4">
							<div class="table-container">
								<table class="table table-striped table-bordered table-hover" id="datatable_shipment">
								<thead>
								<tr role="row" class="heading">
									<th width="5%">
										 Shipment&nbsp;#
									</th>
									<th width="15%">
										 Ship&nbsp;To
									</th>
									<th width="15%">
										 Shipped&nbsp;Date
									</th>
									<th width="10%">
										 Quantity
									</th>
									<th width="10%">
										 Actions
									</th>
								</tr>
								<tr role="row" class="filter">
									<td>
										<input type="text" class="form-control form-filter input-sm" name="order_shipment_no">
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="order_shipment_ship_to">
									</td>
									<td>
										<div class="input-group date date-picker margin-bottom-5" data-date-format="dd/mm/yyyy">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_shipment_date_from" placeholder="From">
											<span class="input-group-btn">
											<button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
										<div class="input-group date date-picker" data-date-format="dd/mm/yyyy">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_shipment_date_to" placeholder="To">
											<span class="input-group-btn">
											<button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
									</td>
									<td>
										<div class="margin-bottom-5">
											<input type="text" class="form-control form-filter input-sm" name="order_shipment_quantity_from" placeholder="From"/>
										</div>
										<input type="text" class="form-control form-filter input-sm" name="order_shipment_quantity_to" placeholder="To"/>
									</td>
									<td>
										<div class="margin-bottom-5">
											<button class="btn btn-sm yellow filter-submit margin-bottom"><i class="fa fa-search"></i> Search</button>
										</div>
										<button class="btn btn-sm red filter-cancel"><i class="fa fa-times"></i> Reset</button>
									</td>
								</tr>
								</thead>
								<tbody>
								</tbody>
								</table>
							</div>
						</div>
						<div class="tab-pane" id="tab_5">
							<div class="table-container">
								<table class="table table-striped table-bordered table-hover" id="datatable_history">
								<thead>
								<tr role="row" class="heading">
									<th width="25%">
										 Datetime
									</th>
									<th width="55%">
										 Description
									</th>
									<th width="10%">
										 Notification
									</th>
									<th width="10%">
										 Actions
									</th>
								</tr>
								<tr role="row" class="filter">
									<td>
										<div class="input-group date datetime-picker margin-bottom-5" data-date-format="dd/mm/yyyy hh:ii">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_history_date_from" placeholder="From">
											<span class="input-group-btn">
											<button class="btn btn-sm default date-set" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
										<div class="input-group date datetime-picker" data-date-format="dd/mm/yyyy hh:ii">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_history_date_to" placeholder="To">
											<span class="input-group-btn">
											<button class="btn btn-sm default date-set" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="order_history_desc" placeholder="To"/>
									</td>
									<td>
										<select name="order_history_notification" class="form-control form-filter input-sm">
											<option value="">Select...</option>
											<option value="pending">Pending</option>
											<option value="notified">Notified</option>
											<option value="failed">Failed</option>
										</select>
									</td>
									<td>
										<div class="margin-bottom-5">
											<button class="btn btn-sm yellow filter-submit margin-bottom"><i class="fa fa-search"></i> Search</button>
										</div>
										<button class="btn btn-sm red filter-cancel"><i class="fa fa-times"></i> Reset</button>
									</td>
								</tr>
								</thead>
								<tbody>
								</tbody>
								</table>
							</div>
						</div>
						
						
						
						
					</div>
				</div>
			</div>
		</div>
		<!-- End: life time stats -->
	</div>
</div>

<form name="form_action" id="form_action" method="POST">
	<input type="hidden" name="doc_type" value="${docType}">
	<input type="hidden" name="ent_code" value="${entCode}">
	<input type="hidden" name="order_no" value="${orderNo}">
</form>
<!-- END PAGE CONTENT-->

<!-- BEGIN PAGE LEVEL PLUGINS -->
<!-- END PAGE LEVEL PLUGINS -->

<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="../../assets/admin/pages/scripts/custom-orders-view.js"></script>
<!-- END PAGE LEVEL SCRIPTS -->

<script>
     jQuery(document).ready(function() {
        EcommerceOrdersView.init();
        
        // Schedule Order ( and Release)
        $('#tool_schedule').click(function(e){
        	
        	if( confirm("Are you sure release this order?")){
        		e.preventDefault();
            	ajaxCallApi(this, '/orders/scheduleOrder.sc');
        	}
        	
        });
        
        
        // Cancel Order
        $('#tool_cancel').click(function(e){
        	
        	if( confirm("Are you sure cancel this order?")){
        		e.preventDefault();
            	ajaxCallApi(this, '/orders/cancelOrder.sc');
        	}
        	
        }); 
        
        
        $('#title_order_date').html(moment('${baseInfo.orderDate}', moment.ISO_8601).format("YYYY-MM-DD HH:mm"));
        $('#order_date').html($('#title_order_date').html());
        
        
     });
     
     function ajaxCallApi(eventObj, callUrl){
    	 
    	 
    	 $.ajax({
				url: callUrl,
				data: $('#form_action').serialize(),
				success:function(data)
				{
					
					if(data.success == 'Y'){
						alert(data.outputMsg);
						$('#tool_reload').click();
						
					}else{
						alert(data.errorMsg);
					}
					
				},
				beforeSend:function(xhr, status){
					Metronic.blockUI({
		                boxed:true
		             });
	        	},
				complete:function(xhr, status){
					Metronic.unblockUI();
	        	}
		});
     }
 </script>
<!-- END JAVASCRIPTS -->

</body>
<!-- END BODY -->
</html>