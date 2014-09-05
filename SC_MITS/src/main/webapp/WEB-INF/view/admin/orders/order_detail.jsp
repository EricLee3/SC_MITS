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
					<i class="fa fa-shopping-cart"></i>오더번호:${orderNo} | 오더 생성일:<span class="hidden-480" id="title_order_date">
					${baseInfo.orderDate} <!-- Dec 27, 2013 7:16:25 --> </span>
				</div>
				<div class="actions">
					<a href="/admin/orders/order_list.html?orderNo=${baseInfo.orderNo}" class="btn default yellow-stripe ajaxify">
					<i class="fa fa-angle-left"></i>
					<span class="hidden-480">
					Back </span>
					</a>
					<a href="javascript:pageReload();" class="btn default yellow-stripe" id="tool_reload">
					<i class="fa fa-refresh"></i>
					<span class="hidden-480">
					Reload </span>
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
						<li>
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
						<div class="tab-pane" id="tab_1">
							<div class="row">
								<div class="col-md-6 col-sm-12">
									<div class="portlet yellow-crusta box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Order Details
											</div>
											<div class="actions">
												<a class="btn default btn-sm" href="javascript:;" id="tool_release">
												주문확정 전송
												<i class="fa fa-edit"></i>
												</a>
												<a class="btn red btn-sm" href="javascript:;" id="tool_cancel">
												주문취소
												<i class="fa fa-edit"></i>
												</a>
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
													 Phone: ${billInfo.billPhone}<br>
													 Mobile: ${billInfo.billMPhone}<br>
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
													 ${shipInfo.shipName}<br>
													 ${shipInfo.shipAddr1}<br>
													 ${shipInfo.shipAddr2}<br>
													 ${shipInfo.shipCity}&nbsp;${shipInfo.shipState}&nbsp;${shipInfo.shipZipcode}<br>
													 <br>
													 Phone: ${shipInfo.shipPhone}<br>
													 Mobile: ${shipInfo.shipMPhone}<br>
													 Fax: ${shipInfo.shipFaxNo}<br>
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
						
						<!-- Notes Tab -->
						<div class="tab-pane" id="tab_2">
						
						
							<div class="row">
							<div class="col-md-12">
								<div class="portlet box blue-hoki">
									<div class="portlet-title">
										<div class="caption">
											<i class="fa fa-gift"></i>Input Contact Information
										</div>
										<div class="tools">
											<a href="javascript:;" class="collapse">
											</a>
											<!-- <a href="#portlet-config" data-toggle="modal" class="config">
											</a>
											<a href="javascript:;" class="reload">
											</a>
											<a href="javascript:;" class="remove">
											</a> -->
										</div>
										
										<div class="actions">
											<a class="btn btn-default btn-sm" href="javascript:;" id="btn_save_note">
											Save
											<i class="fa fa-edit"></i>
											</a>
										</div>
									</div>
									<div class="portlet-body form">
										<!-- BEGIN FORM-->
										<form class="form-horizontal" id="form_save_note">
											<input type="hidden" name="doc_type" value="${docType}">
											<input type="hidden" name="ent_code" value="${entCode}">
											<input type="hidden" name="order_no" value="${orderNo}">
											<input type="hidden" name="seller_code" value="${baseInfo.sellerCode}">
										
										
											<div class="form-body">
												<!-- <h4 class="form-section">Basic Information</h4> -->
												
												<!-- Alert Message Area -->
												<div class="alert alert-danger display-hide">
													<button class="close" data-close="alert"></button>
													You have some form errors. Please check below.
												</div>
												<div class="alert alert-success display-hide">
													<button class="close" data-close="alert"></button>
													Your form validation is successful!
												</div>
												<!-- Alert Message Area -->
												
												<div class="row">
													<div class="col-md-5">
														<div class="form-group">
															<label class="control-label col-md-3">Contact Time <span class="required">
															* </span>
															</label>
															<div class="col-md-5">
																<div class="input-group date date-picker" data-date-format="yyyy-mm-dd" data-date-start-date="+0d">
																	<input type="text" class="form-control" readonly name="contact_date_day">
																	<span class="input-group-btn">
																		<button class="btn default" type="button"><i class="fa fa-calendar"></i></button>
																	</span>
																</div>
															</div>
															<div class="col-md-4">
																<div class="input-group">
																	<input type="text" class="form-control timepicker timepicker-24" readOnly name="contact_date_time">
																	<span class="input-group-btn">
																		<button class="btn default" type="button"><i class="fa fa-clock-o"></i></button>
																	</span>
																</div>
															</div>
															
															
														</div>
													</div>
													<div class="col-md-3">
														<div class="form-group">
															<label class="control-label col-md-4">Contact User <span class="required">
															* </span>
															</label>
															<div class="col-md-8">
																<div class="input-group">
																	<span class="input-group-addon">
																	<i class="fa fa-user"></i>
																	</span>
																	<input type="text" name="contact_user" data-required="1" class="form-control"/>
																</div>
															</div>
														</div>
													</div>
													<div class="col-md-4">
														<div class="form-group">
															<label class="control-label col-md-4">Reason Code <span class="required">
															* </span>
															</label>
															<div class="col-md-8">
																<select class="form-control" name="contact_reason">
																	<option value="">Select</option>
																	<option value="BACKORDER_INFO">Backorder Information</option>
																	<option value="CALLED_CUSTOMER">Called Customer</option>
																</select>
															</div>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="col-md-4">
														<div class="form-group">
															<label class="control-label col-md-4">Contact Type
															</label>
															<div class="col-md-8">
																<select class="form-control" name="contact_type">
																	<option value="">Select</option>
																	<option value="EMAIL"">E-mail</option>
																	<option value="PHONE">Phone</option>
																</select>
															</div>
														</div>
													</div>
													<div class="col-md-4">
														<div class="form-group">
															<label class="control-label col-md-4">Contact Reference
															</label>
															<div class="col-md-8">
																<div class="input-group">
																	<span class="input-group-addon">
																	<i class="fa fa-user"></i>
																	</span>
																	<input type="text" name="contact_ref" data-required="1" class="form-control"/>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="col-md-12">
														<div class="form-group">
															<label class="control-label col-md-1">Add Note
															</label>
															<div class="col-md-11">
																<textarea class="form-control" rows="3" name="contact_note"></textarea>
															</div>
														</div>
													</div>
												</div>
											</div>
										</form>
										<!-- END FORM-->
									</div>
								</div>
								</div>
							</div>
						
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
						
						<!-- <div class="tab-pane" id="tab_3">
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
						</div> -->
						
						<!-- Shipment Tab -->
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
						
						
						<!-- History Tab -->
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
        
		OrderDetailView.init();
        
		
		// 오더상세 날짜 포맷변경
        $('#title_order_date').html(moment('${baseInfo.orderDate}', moment.ISO_8601).format("YYYY-MM-DD HH:mm"));
        $('#order_date').html($('#title_order_date').html());
		
        $('.nav.nav-tabs li').eq(order_detail_tab_index).addClass('active');
        $('.tab-content .tab-pane').eq(order_detail_tab_index).addClass("active");
        
        
     });
     
	function pageReload(){
		
		Metronic.blockUI({
			target:'.page-content-wrapper',
            boxed:true
        });
		
		order_detail_tab_index = $('.nav.nav-tabs li[class="active"]').index();
		$('.page-content-body').load('orders/orderDetail.do?docType=${docType}&entCode=${entCode}&orderNo=${orderNo}', function(){
			
			window.setTimeout(function () {
               Metronic.unblockUI('.page-content-wrapper');
            }, 100);
		});
	}
 
     
     
 </script>
<!-- END JAVASCRIPTS -->

</body>
<!-- END BODY -->
</html>