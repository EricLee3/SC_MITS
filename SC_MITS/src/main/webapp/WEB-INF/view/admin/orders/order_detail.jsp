<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>



<!DOCTYPE html>
<!-- 
Template Name: Metronic - Responsive Admin Dashboard Template build with Twitter Bootstrap 3.2.0
Version: 3.1.1
Author: KeenThemes
Website: http://www.keenthemes.com/
Contact: support@keenthemes.com
Follow: www.twitter.com/keenthemes
Like: www.facebook.com/keenthemes
Purchase: http://themeforest.net/item/metronic-responsive-admin-dashboard-template/4021469?ref=keenthemes
License: You must have a valid license purchased only from themeforest(the above link) in order to legally use the theme for your project.
-->
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en" >
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
<meta charset="utf-8"/>
<title>OMC - Order Management Center</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<meta content="" name="description"/>
<meta content="" name="author"/>
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
<!-- END GLOBAL MANDATORY STYLES -->


<!-- BEGIN PAGE LEVEL STYLES -->
<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/select2/select2.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/Scroller/css/dataTables.scroller.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/ColReorder/css/dataTables.colReorder.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.css"/> -->

<link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal-bs3patch.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/clockface/css/clockface.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-datepicker/css/datepicker3.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-datetimepicker/css/datetimepicker.css"/> 
<!-- END PAGE LEVEL STYLES -->


<!-- BEGIN THEME STYLES -->
<link href="../../assets/global/css/components.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/css/plugins.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css"/>
<link id="style_color" href="../../assets/admin/layout/css/themes/default.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/admin/layout/css/custom.css" rel="stylesheet" type="text/css"/>
<!-- END THEME STYLES -->
<link rel="shortcut icon" href="favicon.ico"/>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<!-- DOC: Apply "page-header-fixed-mobile" and "page-footer-fixed-mobile" class to body element to force fixed header or footer in mobile devices -->
<!-- DOC: Apply "page-sidebar-closed" class to the body and "page-sidebar-menu-closed" class to the sidebar menu element to hide the sidebar by default -->
<!-- DOC: Apply "page-sidebar-hide" class to the body to make the sidebar completely hidden on toggle -->
<!-- DOC: Apply "page-sidebar-closed-hide-logo" class to the body element to make the logo hidden on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-hide" class to body element to completely hide the sidebar on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-fixed" class to have fixed sidebar -->
<!-- DOC: Apply "page-footer-fixed" class to the body element to have fixed footer -->
<!-- DOC: Apply "page-sidebar-reversed" class to put the sidebar on the right side -->
<!-- DOC: Apply "page-full-width" class to the body element to have full width page without the sidebar menu -->
<body class="page-header-fixed page-quick-sidebar-over-content ">
<!-- BEGIN HEADER -->
<jsp:include page="../inc/inc_header.jsp" />
<!-- END HEADER -->
<div class="clearfix">
</div>
<!-- BEGIN CONTAINER -->
<div class="page-container">
	
	<!-- BEGIN SIDEBAR -->
	<jsp:include page="../inc/inc_sidemenu.jsp" />
	<!-- END SIDEBAR -->
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
			<!-- BEGIN STYLE CUSTOMIZER -->
			<!-- END STYLE CUSTOMIZER -->

			<!-- --------------------------------------------------------------------------------- -->
	<!-- BEGIN SAMPLE PORTLET CONFIGURATION MODAL FORM-->
	<!-- Order Cancel Modal Layter -->
	<div id="md_cancel_order" class="modal fade" tabindex="-1" data-width="450">
	<form class="horizontal-form" id="form_cancel">
	<input type="hidden" name="doc_type" value="${docType}">
	<input type="hidden" name="ent_code" value="${entCode}">
	<input type="hidden" name="sell_code" value="${baseInfo.sellerCode}">
	<input type="hidden" name="order_no" value="${orderNo}">
	<input type="hidden" name="min_status" value="${baseInfo.minStatus}">
	<input type="hidden" name="max_status" value="${baseInfo.maxStatus}">
	<input type="hidden" name="cancel_type" value="order">
	<input type="hidden" name="line_keys" value="">
	
	
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
						<option value="OUT_OF_STOCK">재고부족(품절취소)</option>
						<option value="CUSTOMER_REQUEST">고객요청</option>
						<option value="ETC">기타</option>
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
	</form>
	</div>
	<!-- /.modal -->
	<!-- END SAMPLE PORTLET CONFIGURATION MODAL FORM-->



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
					<i class="fa fa-shopping-cart"></i>오더번호:${orderNo} | 오더 생성일:<span class="hidden-480">
					${baseInfo.orderDate} <!-- Dec 27, 2013 7:16:25 --> </span>
				</div>
				<div class="actions">
					<a href="javascript:history.go(-1);" class="btn default yellow-stripe">
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
							접촉이력
						    <c:if test="${! empty( noteList ) }">
						        <span class="badge badge-info">
								${fn:length(noteList)} </span>
						    </c:if> 
							</a>
						</li>
						<!-- <li>
							<a href="#tab_3" data-toggle="tab">
							Invoices <span class="badge badge-success">
							4 </span>
							</a>
						</li> -->
						<li>
							<a href="#tab_4" data-toggle="tab">
							출고확정정보  
							<c:if test="${ fn:length(shipList) > 0 }">
						        <span class="badge badge-info">
								${fn:length(shipList)} </span>
						    </c:if>
							</a>
						</li>
						<!-- <li>
							<a href="#tab_5" data-toggle="tab">
							History </a>
						</li> -->
					</ul>
					<div class="tab-content">
						<div class="tab-pane" id="tab_1">
							<div class="row">
								<div class="col-md-6 col-sm-12">
									<div class="portlet yellow-crusta box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Basic Information
											</div>
											<div class="tools">
												<a href="javascript:;" class="collapse">
												</a>
											</div>
										</div>
										<div class="portlet-body">
											<div class="row static-info">
												<div class="col-md-5 name">
													 오더번호 #:
												</div>
												<div class="col-md-7 value">
													 ${baseInfo.orderNo}  <!-- <span class="label label-info label-sm">
													Email confirmation was sent </span> -->
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 오더생성일자:
												</div>
												<div class="col-md-7 value">
													${baseInfo.orderDate}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 관리조직/판매채널/2차DOS :
												</div>
												<div class="col-md-7 value">
													${baseInfo.entCode} / ${baseInfo.sellerCode} / ${baseInfo.vendor_id}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 주문자명/연락처:
												</div>
												<div class="col-md-7 value">
													 ${custInfo.custName}/ ${custInfo.custPhone}
												</div>
											</div>
											<%-- <div class="row static-info">
												<div class="col-md-5 name">
													 연락처:
												</div>
												<div class="col-md-7 value">
													 ${custInfo.custPhone}
												</div>
											</div> --%>
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
													 결제수단:
												</div>
												<div class="col-md-7 value">
													 ${baseInfo.paymentTypeName }
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 배송메세지:
												</div>
												<div class="col-md-7 value">
													 ${baseInfo.deliveryMsg}
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-6 col-sm-12">
									<div class="portlet blue-hoki box">
										<div class="portlet-title">
											<div class="caption">
												<i class="fa fa-cogs"></i>Order Status Information
											</div>
											<div class="tools">
												<a href="javascript:;" class="collapse">
												</a>
											</div>
											<div class="actions" id="order_chage_btn">
											
												<a class="btn btn-primary btn-sm blue-stripe" href="javascript:;" id="tool_release">
												주문확정 재처리
												</a>
											
												<a class="btn btn-danger btn-sm red-stripe" data-toggle="modal" href="#md_cancel_order" id="tool_cancel">
												주문취소
												</a>
												<a class="btn btn-danger btn-sm red-stripe hidden"  id="tool_cancel_req">
												주문취소요청
												</a>
											</div>
										</div>
										<div class="portlet-body">
											
											<div class="row static-info">
												<div class="col-md-12 value text-center" id="order_status">
												 	<h4>
												 	<span class="label label-default">
													주문접수 
													</span>&nbsp;<i class="fa fa-arrow-right"></i>
													<span class="label label-default">
													출고의뢰 
													</span>&nbsp;<i class="fa fa-arrow-right"></i>
													<span class="label label-default">
													출고준비 
													</span>&nbsp;<i class="fa fa-arrow-right"></i>
													<span class="label label-default">
													출고완료 
													</span>
													&nbsp;&nbsp;&nbsp;&nbsp;
													</h4>
												</div>
											</div>
											<!-- <div class="row static-info">
												<div class="col-md-12">&nbsp;</div>
											</div> -->
											<div class="row static-info">
												<div class="col-md-12 " id="order_status_detail"></div>
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
											<div class="tools">
												<a href="javascript:;" class="collapse">
												</a>
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
											<div class="tools">
												<a href="javascript:;" class="collapse">
												</a>
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
											<div class="tools">
												<a href="javascript:;" class="collapse">
												</a>
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
													 <fmt:formatNumber type="number" maxFractionDigits="3" value="${baseInfo.totalLine}" />
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Total Charges:
												</div>
												<div class="col-md-7 value text-right">
													 <fmt:formatNumber type="number" maxFractionDigits="3" value="${baseInfo.totalCharge}" />
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Total Tax:
												</div>
												<div class="col-md-7 value text-right">
													 <fmt:formatNumber type="number" maxFractionDigits="3" value="${baseInfo.totalTax}" />
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Total Discount:
												</div>
												<div class="col-md-7 value text-right">
													 <fmt:formatNumber type="number" maxFractionDigits="3" value="${baseInfo.totalDiscount}" />
												</div>
											</div>
											<div class="row static-info">
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 Grand Total:
												</div>
												<div class="col-md-7 value text-right">
													 ${baseInfo.currency}&nbsp;&nbsp;<fmt:formatNumber type="number" maxFractionDigits="3" value="${baseInfo.totalAmount}" />
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
											<div class="tools">
												<a href="javascript:;" class="collapse">
												</a>
											</div>
											<!-- <div class="actions">
												<a href="#" class="btn btn-default btn-sm">
												<i class="fa fa-pencil"></i> Edit </a>
											</div> -->
											<div class="actions">
												<a class="btn red btn-sm" data-toggle="modal" href="javascript:cancelOrderLine();" id="tool_line_cancel">
												부분주문취소
												</a>
											</div>
										</div>
										<div class="portlet-body">
											<div class="table-container">
												<table class="table table-hover table-bordered table-striped" id="table_orderLine">
												<thead>
												<tr role="row" class="heading">
													<th width="2%">
														<!-- <input type="checkbox" class="group-checkable"> -->
													</th>
													<th>
														 No.
													</th>
													<th>
														 상품코드
													</th>
													<th>
														 상품명
													</th>
													<th>
														 주문상태
													</th>
													<th>
														 현재고/가용재고
													</th>
													<th>
														 출하창고
													</th>
													<th>
														 주문수량
													</th>
													<th>
														 개별판매가격
													</th>
													<th>
														 비용(배송비)
													</th>
													<!-- <th>
														 과세
													</th> -->
													<th>
														 할인금액
													</th>
													<th>
														 합계금액
													</th>
												</tr>
												</thead>
												<tbody>
												
												<c:forEach items="${lineInfoList}" var="line">
												    <%-- Key = ${entry.key}, value = ${entry.value}<br> --%>
												    
											    <tr>
													<td>
														<input type="checkbox" name="orderLineKeys[]" value="${line.lineKey}">
														<input type="hidden" name="status[]" value="${line.max_status}">
													</td>
													<td>
														${line.PrimeLineNo}
													</td>
													<td>
														<a href="${line.itemDesc}" target="_blank">
														${line.itemId}</a>
														<!-- <span class="label label-sm label-info">Details</span> -->
													</td>
													<td>
														 ${line.itemdShortDesc}
													</td>
													<td>
														<span class="label label-sm label-${line.status_class}">
														${line.status}</span>
														<c:if test="${ (shortedYN == 'Y') && (shortedItemId == line.itemId)}">
														&nbsp;<span class="label label-sm label-danger">
														품절취소</span>
														</c:if>
													</td>
													<td class="text-right">
														${line.supplyQty} / ${line.availQty}</a>
													</td>
													<td>
														 ${line.shipNode}
													</td>
													<td class="text-right">
														 ${line.qty}
													</td>
													<td class="text-right">
														 <fmt:formatNumber type="number" maxFractionDigits="3" value="${line.UnitPrice}" />
													</td>
													<td  class="text-right">
														 <fmt:formatNumber type="number" maxFractionDigits="3" value="${line.lineShipCharge}" />
													</td>
													<%-- <td  class="text-right">
														 ${line.lineTax}
													</td> --%>
													<td class="text-right">
														 <fmt:formatNumber type="number" maxFractionDigits="3" value="${line.lineDisount}" />
													</td>
													<td class="text-right">
														 <fmt:formatNumber type="number" maxFractionDigits="3" value="${line.lineTatal}" />
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
											<i class="fa fa-gift"></i>접촉이력 등록<!-- Input Contact Information -->
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
										<form class="horizontal-form" id="form_save_note">
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
													<div class="col-md-3">
														<div class="form-group">
															<label class="control-label">접촉일시
															</label>
															<div class="input-group" >
																<input type="text" class="form-control input-sm date date-picker" data-date-format="yyyy-mm-dd" data-date-start-date="-1m" data-date-end-date="+0d" name="contact_date_day">
																<span class="input-group-btn day">
																	<button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button>
																</span>
																<div class="input-group">
																	<!-- <input type="text" class="form-control input-sm timepicker timepicker-24" name="contact_date_time">
																	<span class="input-group-btn time">
																		<button class="btn btn-sm default" type="button"><i class="fa fa-clock-o"></i></button>
																	</span> -->
																	<input type="text" class="form-control input-sm" id="contact_clockface" name="contact_date_time">
																	<span class="input-group-btn">
																		<button class="btn btn-sm default" type="button" id="contact_clockface_toggle"><i class="fa fa-clock-o"></i></button>
																	</span>
																</div>
															</div>
														</div>
													</div>
													<div class="col-md-3">
														<div class="form-group">
															<label class="control-label">접촉담당자
															</label>
															<div class="input-group">
																<span class="input-group-addon">
																<i class="fa fa-user"></i>
																</span>
																<input type="text" name="contact_user" data-required="1" class="form-control input-sm"/>
															</div>
														</div>
													</div>
													<div class="col-md-3">
														<div class="form-group">
															<label class="control-label">접촉사유
															</label>
															<select class="form-control input-sm" name="contact_reason">
																<option value="">Select</option>
																<!-- <option value="BACKORDER_INFO">Backorder Information</option> -->
																<option value="CALLED_CUSTOMER">고객 아웃바운드</option>
																<option value="CUSTOMER_CALLED">고객 인바운드</option>
																<option value="SCHEDULE_AND_RELEASE">출고의뢰 관련</option>
																<option value="CONFIRM_SHIPMENT">출고확정 관련</option>
																<option value="CANCEL_INFO">주문취소 관련</option>
															</select>
														</div>
													</div>
													<div class="col-md-3">
														<div class="form-group">
															<label class="control-label">접촉수단
															</label>
															<select class="form-control input-sm" name="contact_type">
																<option value="">Select</option>
																<option value="PHONE">Phone</option>
																<option value="EMAIL"">E-mail</option>
															</select>
														</div>
													</div>
												</div>
												<div class="row">
													<div class="col-md-4">
														<!-- <div class="form-group">
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
														</div> -->
													</div>
												</div>
												<div class="row">
													<div class="col-md-12">
														<div class="form-group">
															<label class="control-label">접촉내용
															</label>
															<textarea class="form-control input-sm" rows="3" name="contact_note"></textarea>
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
								<table class="table table-striped table-bordered" id="datatable_credit_memos">
								<thead>
								<tr role="row" class="heading">
								    <th width="5%">
										No
									</th>
									<th width="15%">
										 접촉일시
									</th>
									<th width="10%">
										 사용자
									</th>
									<th width="12%">
										 접촉사유
									</th>
									<th width="15%">
										 접촉수단
									</th>
									<!-- <th width="15%">
										 Contact Reference
									</th> -->
									<th width="" class="text-center">
										 접촉내용
									</th>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="${noteList}" var="note" varStatus="status">
									<tr>
								    		<td>
											${status.count}
										</td>
								    		<td>
											${note.noteDate}
										</td>
										<td>
											${note.noteUser}
										</td>
										<td>
											${note.noteReason}
											 <!-- <select class="form-control input-sm" name="contact_reason_view">
												<option value="BACKORDER_INFO">Backorder Information</option>
												<option value="CALLED_CUSTOMER">고객 아웃바운드</option>
												<option value="CUSTOMER_CALLED">고객 인바운드</option>
												<option value="SCHEDULE_AND_RELEASE">출고의뢰 관련</option>
												<option value="CONFIRM_SHIPMENT">출고확정 관련</option>
												<option value="CANCEL_INFO">주문취소 관련</option>
											</select> -->
										</td>
										<td>
											${note.noteContactType}
											<!-- <select class="form-control input-sm" name="contact_type_view">
												<option value="PHONE">Phone</option>
												<option value="EMAIL"">E-mail</option>
											</select> -->
										</td>
										<%-- <td>
											 ${note.noteContactRef}
										</td> --%>
										<td>
											<textarea class="form-control input-sm" rows="2" readonly="readonly">${note.noteText}</textarea>
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
										No
									</th>
									<th width="10%">
										 출고번호&nbsp;#
									</th>
									<th width="10%">
										 전표번호&nbsp;#
									</th>
									<th width="15%">
										 출고일시
									</th>
									<th width="15%">
										 출하노드
									</th>
									<!-- <th width="5%">
										 상태
									</th> -->
									<th>
										 택배사
									</th>
									<th width="15%">
										 송장번호
									</th>
								</tr>
								<!-- <tr role="row" class="filter">
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
								</tr> -->
								</thead>
								<tbody>
									<c:forEach items="${shipList}" var="ship" varStatus="status">
									<!-- 
									
										dataMap.put("sellerCode", sellerCode);
										dataMap.put("shipmentNo", shipmentNo);
										dataMap.put("shipmentNoCube", shipmentNoCube);
										dataMap.put("shipNode", shipNode);
										dataMap.put("shipNodeString", shipNodeString);
										dataMap.put("status", status);
										dataMap.put("scac", scac);
										dataMap.put("scacService", scacService);
										dataMap.put("trackingNo", trackingNo);
										dataMap.put("aShipDate", aShipDate);
										dataMap.put("eShipDate", eShipDate);
									 -->
									<tr>
										<td>
											${status.count}
										</td>
								    		<td>
											${ship.shipmentNo}
										</td>
										<td>
											${ship.shipmentNoCube}
										</td>
										<td>
											 ${ship.aShipDate}
										</td>
										<td>
											${ship.shipNodeString}
										</td>
										<%-- <td>
											${ship.status}
										</td> --%>
										<td>
											${ship.scacService}
										</td>
										<td>
											${ship.trackingNo}
										</td>
									</tr>
									
									</c:forEach>
								</tbody>
								</table>
							</div>
						</div>
						
						
						<!-- History Tab -->
						<!-- <div class="tab-pane" id="tab_5">
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
						</div> -->
						
						
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
	<input type="hidden" name="sell_code" value="${baseInfo.sellerCode}">
	<input type="hidden" name="order_no" value="${orderNo}">
	<input type="hidden" name="min_status" value="${baseInfo.minStatus}">
	<input type="hidden" name="max_status" value="${baseInfo.maxStatus}">
</form>
<!-- END PAGE CONTENT-->


<!-- --------------------------------------------------------------------------------- -->

		</div>
		<!-- BEGIN CONTENT -->
	</div>
	<!-- END CONTENT -->
	
	<!-- BEGIN QUICK SIDEBAR -->
	<jsp:include page="../inc/inc_quick_sidebar.jsp" />
	<!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->

<!-- BEGIN FOOTER -->
<jsp:include page="../inc/inc_footer.jsp" />
<!-- END FOOTER -->

<!-- BEGIN Common Js -->
<jsp:include page="../inc/inc_commonJs.jsp" />
<!-- END Common Js -->


<!-- BEGIN PAGE LEVEL PLUGINS -->

<script type="text/javascript" src="../../assets/global/plugins/select2/select2.min.js"></script>
<script src="../../assets/global/plugins/bootstrap-modal/js/bootstrap-modalmanager.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/bootstrap-modal/js/bootstrap-modal.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->

<!-- date/datetime picker -->
<script type="text/javascript" src="../../assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/clockface/js/clockface.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/bootstrap-daterangepicker/moment.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js"></script>

<!-- data table -->
<script type="text/javascript" src="../../assets/global/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/extensions/TableTools/js/dataTables.tableTools.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/extensions/ColReorder/js/dataTables.colReorder.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/extensions/Scroller/js/dataTables.scroller.min.js"></script>
<!-- END PAGE LEVEL PLUGINS -->


<!-- BEGIN PAGE LEVEL PLUGINS -->
<!-- END PAGE LEVEL PLUGINS -->

<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="../../assets/admin/pages/scripts/custom-orders-view.js"></script>
<!-- END PAGE LEVEL SCRIPTS -->

<script>

	var order_detail_tab_index = 0;
	jQuery(document).ready(function() {
        
		OrderDetailView.init();
        
		
		// 오더상세 날짜 포맷변경
        /* $('#title_order_date').html(moment('${baseInfo.orderDate}', moment.ISO_8601).format("YYYY-MM-DD HH:mm"));
        $('#order_date').html($('#title_order_date').html()); */
		
        $('.nav.nav-tabs li').eq(order_detail_tab_index).addClass('active');
        $('.tab-content .tab-pane').eq(order_detail_tab_index).addClass("active");
        
        
        /**
		ui.status.cssname.1000=default
		ui.status.cssname.1100=success
		ui.status.cssname.1300=warning
		ui.status.cssname.1500=info
		ui.status.cssname.3200=primary
		ui.status.cssname.3350=info
		ui.status.cssname.3700=primary
		ui.status.cssname.9000=danger
        **/
        var statusClassName = "${baseInfo.orderStatus_class}";
        var minStatus = "${baseInfo.minStatus}";
        var maxStatus = "${baseInfo.maxStatus}";
        
        var statusDetailText = "";
        
        //------------- 정상상태
        // 주문접수
        if(minStatus == '1100' && maxStatus == '1100')
        {
	    		$('#order_status span:eq(0)').removeClass("label-default").addClass("label-"+statusClassName);
	    		
	    		
	    		statusDetailText = '<strong>[주문접수]</strong> - Front에서 결재완료된 주문이 정상 접수된 상태.';
	    		statusDetailText += '<p class="text-danger"> - 즉시 취소가능<br>';
	    		
	    		var status_label = '<span class="label label-sm label-'+statusClassName+'">${baseInfo.orderStatus}</span>';
	    		statusDetailText = '<h5>'+status_label+' - Front에서 결재완료된 주문이 정상 접수된 상태.</h4>';
	    		statusDetailText += '<p class="text-danger">';
	    		statusDetailText += ' - 지정된 시간에 자동으로 재고확인 및 창고결정 후 Cube로 출고의뢰됨<br>';
	    		statusDetailText += ' - 즉시 주문취소가능<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '</p>';
	    		
	    		
			// 주문확정 숨김
			$("#tool_release").hide();
			// $("#tool_cancel").hide();
			// $("#tool_line_cancel").hide();
	    }
        // 출고의뢰
        else if(minStatus == '3200' && maxStatus == '3200')
        {
    			
        		$('#order_status span:eq(1)').removeClass("label-default").addClass("label-"+statusClassName);

        		// Cube 출고의결과 체크
        		var shortedYN = "${shortedYN}";	// 품절취소 여부
    			var failedYN = "${failedYN}";	// 실패 여부
    			
        		// Cube 품절취소
        		if(shortedYN == 'Y')
        		{
        			
        			var status_label = '<span class="label label-sm label-danger">Cube품절취소 발생</span>';
	    	    		statusDetailText = '<h5>'+status_label+' - Cube로 출고의뢰 했으나 상품의 재고가 부족하여 출고의뢰가 거부된 상태</h4>';
	    	    		statusDetailText += '<p class="text-danger">';
	    	    		statusDetailText += ' - Cube의 재고확인 후 재고가 다시 확보된 경우 출고의뢰 재시도 필요 <br>';
	    	    		statusDetailText += ' - Cube의 재고확보가 빠른시간내 확인되지 않을 경우 고객아웃바운드 후 전체 또는 부분주문취소 처리<br>';
	    	    		statusDetailText += ' - 품절취소가 발생한 상품만 부분취소 후 출고의뢰 재처리 가능.<br>';
	    	    		statusDetailText += ' - 품절취소가 발생한 상품은 오더라인 항목에서 확인 가능<br>';
	    	    		statusDetailText += '</p>';
	    	    		
	    	    		
	    	    		$("#tool_release").text("출고의뢰 재시도");
	    	    		
	    	    		// 주문취소가능. 주문취소요청 불가
	    	    		$("#tool_cancel").show();
	    	    		$("#tool_cancel_req").hide();
	    	    		
	    	    		
	    	    		$("#tool_line_cancel").hide(); // 부분취소 가능
        			
        		// Cube 처리실패
        		}
        		else if(failedYN == 'Y')
        		{
        			
        			var status_label = '<span class="label label-sm label-danger">출고의뢰 실패</span>';
	    	    		statusDetailText = '<h5>'+status_label+' - Cube로 출고의뢰 했으나 Cube에서 출고의뢰 처리도중 실패한 상태.</h4>';
	    	    		statusDetailText += '<p class="text-danger">';
	    	    		statusDetailText += ' - 출고의뢰 재시도 또는 계속 실패할 경우 Cube의 상태 확인 후 Cube담당자에게 문의<br>';
	    	    		statusDetailText += '<br>';
	    	    		statusDetailText += '<br>';
	    	    		statusDetailText += '<br>';
	    	    		statusDetailText += '</p>';
    	    		
    	    		
        			$("#tool_release").text("출고의뢰 재시도");
        			
        			// 주문취소가능. 주문취소요청 불가
    	    		$("#tool_cancel").show();
    	    		$("#tool_cancel_req").hide();
    	    		
    	    		
    	    		$("#tool_line_cancel").hide(); // 부분취소 가능
    	    		
        		
        		// 정상출고의뢰
        		}
        		else
        		{
        			
        			var status_label = '<span class="label label-sm label-'+statusClassName+'">${baseInfo.orderStatus}</span>';
	    	    		statusDetailText = '<h5>'+status_label+' - 재고확인 및 출하창고가 결정되고 Cube로 출고의뢰가 된 상태</h4>';
	    	    		statusDetailText += '<p class="text-danger">';
	    	    		statusDetailText += ' - 즉시 주문취소불가, Cube 주문취소요청 가능<br>';
	    	    		statusDetailText += ' - 부분주문취소 처리후 [출고의뢰]상태인 경우에는 반드시 <span class="label label-primary label-sm blue-stripe">출고의뢰</span>를 다시 해야 Cube로 출고의뢰처리됨.<br>';
	    	    		statusDetailText += '<br>';
	    	    		statusDetailText += '<br>';
	    	    		statusDetailText += '</p>';
        			
	    	    		// 출고의뢰 가능 - 부분취소후 상태일 수 있기 때문 TODO: 정상출고의뢰상태와 부분취소후 출고의뢰상태 구분필요
	    	    		$("#tool_release").text("출고의뢰");
	    			
		    			// 주문취소 불가, 주문취소요청 가능
	    	    		$("#tool_cancel").hide();
	    	    		$("#tool_cancel_req").removeClass("hidden").show();
	    	    		
	    	    		$("#tool_line_cancel").hide(); // 부분취소 불가
        		}
			
        }
        // 출고준비
        else if(minStatus == '3350' && maxStatus == '3350')
        {
        	
	    	$('#order_status span:eq(2)').removeClass("label-default").addClass("label-"+statusClassName);
	    		
			var status_label = '<span class="label label-sm label-'+statusClassName+'">${baseInfo.orderStatus}</span>';
	    		statusDetailText = '<h5>'+status_label+' - Cube로 출고의뢰가 정상적으로 처리된 상태</h4>';
	    		statusDetailText += '<p class="text-danger">';
	    		statusDetailText += ' - 즉시 주문취소불가, Cube 주문취소요청 가능<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '</p>';
	    		
	    		// 주문확정 버튼 숨김, 주문취소요청으로 버튼명 변경
	    		$("#tool_release").hide();
	    		$("#tool_line_cancel").hide(); // 부분취소 불가
	    		
	    		// 주문취소 불가, 주문취소요청 가능
	    		$("#tool_cancel").hide();
	    		$("#tool_cancel_req").removeClass("hidden").show();
	    }
        
        // 출고완료
        else if(minStatus == '3700' && maxStatus == '3700')
        {
        	
	    		$('#order_status span:eq(3)').removeClass("label-default").addClass("label-"+statusClassName);
	    		
	    		var status_label = '<span class="label label-sm label-'+statusClassName+'">${baseInfo.orderStatus}</span>';
	    		statusDetailText = '<h5>'+status_label+' - 주문상품이 정상적으로 출고된 상태</h4>';
	    		statusDetailText += '<p class="text-danger">';
	    		statusDetailText += ' - 주문취소 불가<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '</p>';
	    		
	    		
	    		// 주문확정, 주문취소, 주문취소요청 불가
	    		$("#tool_release").hide();
	    		$("#tool_cancel").hide();
	    		$("#tool_cancel_req").hide();
	    		$("#tool_line_cancel").hide();
	    		
	    }
        // 부분확정 or 재고부족
        else if(minStatus == '1300' || ( maxStatus == '3200' && minStatus != maxStatus))
        {
        		
        		// 주문접수 상태 표시
    			$('#order_status span:eq(0)').removeClass("label-default").addClass("label-warning");
        		
        		// 부분재고부족 또는 재고부족 표시
    			$('#order_status span:eq(0)').next().after($('<span class="label label-'+statusClassName+'">${baseInfo.orderStatus}</span>&nbsp;<i class="fa fa-arrow-right"></i>'));
        		
        		var status_label = '<span class="label label-sm label-'+statusClassName+'">${baseInfo.orderStatus}</span>';
        		
	    		statusDetailText += '<h5>'+status_label+' - 주문확정 처리중 특정 또는 모든상품의 재고가 부족해서 Cube로 [출고의뢰]가 안된 상태</h5>';
	    		statusDetailText += '<p class="text-danger">';
	    		statusDetailText += ' - Cube에서 해당상품의 재고가 확보된 경우 <span class="label label-primary label-sm blue-stripe">주문확정 재처리</span> 클릭시 출고의뢰 처리뙴.<br>';
	    		statusDetailText += ' - 재고확보에 시간이 많이 소요되는 경우 고객아웃바운드 후 해당상품만 <span class="label label-danger label-sm">부분주문취소</span> 처리후 출고의뢰 가능.<br>';
	    		statusDetailText += ' - 부분취소 처리 후 반드시 <span class="label label-primary label-sm blue-stripe">주문확정 재처리</span> 버튼 다시 클릭해야 출고의뢰 처리됨.<br>';
	    		statusDetailText += ' - 고객이 주문취소를 원할 경우 <span class="label label-danger label-sm">주문취소</span> 클릭시 바로 주문취소 되고 Front로 환불요청됨';
	    		statusDetailText += '</p>';
	    		
        		// 주문확정 재처리 버튼표시
        		$("#tool_release").text("주문확정 재처리");
        		
        		// 주문취소 가능
	    		$("#tool_cancel").show();
	    		$("#tool_cancel_req").hide();
        }
        
        // 주문취소
        else if(minStatus == '9000' && minStatus == '9000')
        {
	    		
        		var status_label = '<span class="label label-sm label-'+statusClassName+'">${baseInfo.orderStatus}</span>';
	    		statusDetailText = '<h5>'+status_label+' -고객요청 또는 재고부족으로 인한 주문취소처리가 완료된 상태</h4>';
	    		statusDetailText += '<p class="text-danger">';
	    		statusDetailText += ' - 환불처리결과는 Front의 어드민에서 확인 가능<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '<br>';
	    		statusDetailText += '</p>';
        		
	    		// 주문확정, 주문취소 불가
	    		$("#tool_release").hide();
	    		$("#tool_cancel").hide();
	    		$("#tool_cancel_req").hide();
	    		$("#tool_line_cancel").hide();
        }
        
        
        // Cube 주문취소 요청상태 처리
        	
   		// var cancelReqCode = "${cancelReqInfo.status_code}";
        var cancelReqText = "${cancelReqInfo.status_text}";
        
        var cancelResCode = "${cancelResultInfo.status_code}"; // 01,02,09,90
        var cancelResText = "${cancelResultInfo.status_text}";
        var cancelResClass = "${cancelResultInfo.status_class}";
        var cancelResCubMsg= "${cancelResultInfo.cube_msg}";
            
        // 주문취소 요청상태 처리
        if(cancelReqText != ""){
            		
       		// 현재 주문상태에서 주문취소요청 상태표시
       		var labelText = '<span id="cancel-req" class="label label-sm label-danger">'+cancelReqText+'</span>';
       		
   			statusDetailText = '<h5>'+labelText+' - Cube로 부터 주문취소를 요청한 상태</h4>';
   			statusDetailText += '<p class="text-danger">';
    		statusDetailText += ' - 최대 1시간이내에 취소처리결과 확인 가능. 응답이 없을경우 Cube상태 확인 후 시스템 담당자 문의<br>';
    		statusDetailText += ' - Cube 주문취소요청이 정상처리되면 자동으로 \'주문취소\'처리되고 Front로 환불요청됨<br>';
    		statusDetailText += ' - Cube 주문취소요청이 실패할 경우 실패메세지 확인 후 후속처리 진행할 것.<br>';
    		statusDetailText += ' - 환불처리결과는 Front Admin에서 확인가능<br>';
    		statusDetailText += '</p>';
       		
        	// 주문확정, 주문취소 불가
    		$("#tool_release").hide();
    		$("#tool_cancel").hide();
    		$("#tool_cancel_req").hide();
    		$("#tool_line_cancel").hide();
    		
    		// 주문취소 요청 결과 - 01(취소성공)인 경우 표시되지 않음
         	if(cancelResText != ""){
            		
            		
            		// 현재 주문상태에서 주문취소요청 상태표시
            		/*
					 *   01 - 성공
						 02 - 기처리
						 09 - 실패 또는 처리대상건 없음
						 90 - 출고확정건
					 */
            		var labelText = '<span id="cancel-req" class="label label-sm label-danger">주문취소요청 결과</span>&nbsp;'
            		              + '<span class="label label-sm label-'+cancelResClass+'">'+cancelResText+'</span>'
            		              + '&nbsp;&nbsp;<b>Cube메세지 - ['+cancelResCode+']'+cancelResCubMsg+"</b>";
            		              
            		
            		statusDetailText = '<h5>'+labelText+'</h4>';
            		if(cancelResCode == '01')
            		{
	    				statusDetailText += '<p class="text-danger">';
    	    			statusDetailText += ' - 주문취소요청이 정상적으로 처리된 상태.<br>';
    	    			statusDetailText += ' - OMC의 주문상태가 [주문취소]인지 확인할 것.<br>';
    	    			statusDetailText += '<br>';
    	    			statusDetailText += '<br>';
    	    			statusDetailText += '</p>';
                		
    	    			// 주문확정, 주문취소 불가
    	        		$("#tool_cancel").hide();
    	        		$("#tool_cancel_req").hide();
    	        		$("#tool_line_cancel").hide();
            		}
            		else if(cancelResCode == '02')
            		{
   	    				statusDetailText += '<p class="text-danger">';
       	    			statusDetailText += ' - 이미 Cube에서 주문취소처리가 완료된 건. 새로고침으로 OMC의 주문상태가 [주문취소]인지 다시 확인<br>';
       	    			statusDetailText += ' - [주문취소]상태가 아닐 경우 Cube상태를 다시 확인 후 OMC에서 주문취소처리 할 것 <br>';
       	    			statusDetailText += '<br>';
       	    			statusDetailText += '<br>';
       	    			statusDetailText += '</p>';
       	    			
       	    			// 주문취소 가능, 주문취소요청 불가
       	    			$("#tool_cancel").show();
       	    			$("#tool_cancel_req").hide();
       	    			
       	    			// 부분주문취소버튼 불가
       	    			$("#tool_line_cancel").hide();
            		}
            		else if(cancelResCode == '09'){
            			
	    				statusDetailText += '<p class="text-danger">';
	    				statusDetailText += ' - Cube에서 주문취소처리가 실패했거나 또는 주문취소처리할 정상주문건이 없는 상태<br>';
	    				statusDetailText += ' - Cube메세지 확인 후 Cube담당자에게 문의할 것<br>';
	    				statusDetailText += '<br>';
	    				statusDetailText += '<br>';
   	    				statusDetailText += '</p>';
   	    				
   	    				// 주문취소 불가, 주문취소요청 불가
   	    				$("#tool_cancel").hide();
   	    				$("#tool_cancel_req").hide();
   	    				
   	    				// 부분주문취소버튼 불가
   	    				$("#tool_line_cancel").hide();
            		}
            		// 출고확정
            		else if(cancelResCode == '90'){
            			
	    				statusDetailText += '<p class="text-danger">';
       	    			statusDetailText += ' - 이미 WMS에서 출고처리된 주문건으로 Cube에서 주문처리가 불가한 상태.<br>';
       	    			statusDetailText += ' - OMC의 주문상태가 [출고확정]이면 고객 아웃바운드 필요.<br>';
       	    			statusDetailText += ' - OMC의 주문상태가 [출고확정]이 아닐 경우 일정시간 대기후 새로고침으로 주문상태 재확인 필요.<br>';
       	    			statusDetailText += ' - OMC의 주문상태가 계속 [출고확정]이 아닐 경우 시스템 담당자에게 문의할 것';
       	    			statusDetailText += '</p>';
       	    			
       	    			// 주문취소요청 버튼 숨김
       	    			$("#tool_cancel").hide();
       	    			$("#tool_cancel_req").hide();
       	    			$("#tool_line_cancel").hide();
            		}
            	
    	        	// 주문확정 재처리 버튼 숨김
    	    		$("#tool_release").hide();
	        			
            	}
          		
		} // 주문취소요청 처리 End
        	
        
        statusDetailText = '<div class="note note-warning">'+statusDetailText+'</div>';
        $("#order_status_detail").html(statusDetailText);
        
        
     });
     
	
	
	function pageReload(){
		window.location.reload(false); 
	}
	
	
	function cancelOrderLine(){
		
		var checked = $("#table_orderLine input[type='checkbox']:checked");
		
		if(checked.length == 0){
			alert("취소할 오더라인 하나 이상 선택하세요");
			return;
		}
		
		var cancelKeys = "";
		for(var i=0; i<checked.length; i++){
			cancelKeys = cancelKeys+"|"+checked[i].value;
		}
		cancelKeys = cancelKeys.substring(1);
		
		$('#md_cancel_order input[name="cancel_type"]').val('line');
		$('#md_cancel_order input[name="line_keys"]').val(cancelKeys);
		
		var $modal = $('#md_cancel_order');
		    $modal.modal("show");
	}
 
     
     
 </script>
<!-- END JAVASCRIPTS -->

</body>
<!-- END BODY -->
</html>