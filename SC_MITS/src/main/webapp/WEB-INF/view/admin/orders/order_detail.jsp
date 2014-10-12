<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/select2/select2.css"/>
<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/Scroller/css/dataTables.scroller.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/ColReorder/css/dataTables.colReorder.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.css"/> -->

<!-- <link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal-bs3patch.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal.css" rel="stylesheet" type="text/css"/> -->

<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/clockface/css/clockface.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-datepicker/css/datepicker3.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css"/>-->
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
	<!-- BEGIN SAMPLE PORTLET CONFIGURATION MODAL FORM-->
	<!-- Order Cancel Modal Layter -->
	<div id="md_cancel_order" class="modal fade" tabindex="-1" data-width="450">
	<form class="horizontal-form" id="form_cancel">
	<input type="hidden" name="doc_type" value="${docType}">
	<input type="hidden" name="ent_code" value="${entCode}">
	<input type="hidden" name="order_no" value="${orderNo}">
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
	</form>
	</div>
	<!-- /.modal -->
	<!-- END SAMPLE PORTLET CONFIGURATION MODAL FORM-->
	
	
	
	<!-- BEGIN SIDEBAR -->
	<jsp:include page="../inc/inc_sidemenu.jsp" />
	<!-- END SIDEBAR -->
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
			<!-- BEGIN STYLE CUSTOMIZER -->
			<!-- END STYLE CUSTOMIZER -->

			<!-- --------------------------------------------------------------------------------- -->

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
							Contact History
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
							출고확정정보</span>
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
												<div class="col-md-7 value" id="order_date">
													 
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 관리조직 / 판매채널 :
												</div>
												<div class="col-md-7 value">
													${baseInfo.entCode} / ${baseInfo.sellerCode}
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
													 주문상태:
												</div>
												<div class="col-md-7 value">
												<!-- 
												ui.status.cssname.1000=default
												ui.status.cssname.1100=success
												ui.status.cssname.1300=warning
												ui.status.cssname.1500=info
												ui.status.cssname.3200=primary
												ui.status.cssname.3350=info
												ui.status.cssname.3700=primary
												ui.status.cssname.9000=danger
												 -->
												 	<span class="label label-default">
													주문접수 
													</span>&nbsp;
													<span class="label label-default">
													주문확정 
													</span>&nbsp;
													<span class="label label-default">
													출고준비 
													</span>&nbsp;
													<span class="label label-success">
													출고완료 
													</span>&nbsp;
													<span class="label label-default">
													주문취소
													</span>&nbsp;
												</div>
											</div>
											<div class="row static-info">
												<div class="col-md-5 name">
												</div>
												<div class="col-md-7 value">
													<span class="label label-${baseInfo.orderStatus_class }">
													${baseInfo.orderStatus} 
													</span>&nbsp;
													<c:if test = "${baseInfo.maxStatus == '3200' && baseInfo.minStatus == '3200'}"> 
													[ Cube 출고의뢰중 ]
													</c:if>
													<c:if test = "${baseInfo.maxStatus == '3350' && baseInfo.minStatus == '3350'}"> 
													[ Cube 출고의뢰완료 ]
													</c:if>
												</div>
											</div>
											<div class="row">
												<div class="col-md-12">&nbsp;</div>
												<div class="col-md-12 text-right">
													<a class="btn btn-primary btn-sm blue-stripe" href="javascript:;" id="tool_release">
													<i class="fa fa-edit"></i>
													주문확정
													</a>
													<a class="btn btn-danger btn-sm red-stripe" data-toggle="modal" href="#md_cancel_order">
													<i class="fa fa-edit"></i>
													주문취소
													</a>
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
											<div class="tools">
												<a href="javascript:;" class="collapse">
												</a>
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
											<div class="row static-info">
												<div class="col-md-12">&nbsp;</div>
											</div>
											<div class="row static-info">
												<div class="col-md-12">&nbsp;</div>
												<div class="col-md-12">&nbsp;</div>
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
											<div class="actions">
												<a class="btn red btn-sm" data-toggle="modal" href="javascript:cancelOrderLine();">
												주문취소
												<i class="fa fa-edit"></i>
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
														 재고수량
													</th>
													<th>
														 상품명
													</th>
													<th>
														 주문상태
													</th>
													<th>
														 주문수량
													</th>
													<th>
														 개별판매가격
													</th>
													<!-- <th>
														 ShipNode
													</th> -->
													<th>
														 비용(배송비)
													</th>
													<th>
														 과세
													</th>
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
													<td class="text-right">
														${line.supplyQty}</a>
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
													<%-- <td>
														 ${line.shipNode}
													</td> --%>
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
																<option value="BACKORDER_INFO">Backorder Information</option>
																<option value="CALLED_CUSTOMER">Called Customer</option>
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
<!-- date/datetime picker -->
<script type="text/javascript" src="../../assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/bootstrap-daterangepicker/moment.min.js"></script>
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
        $('#title_order_date').html(moment('${baseInfo.orderDate}', moment.ISO_8601).format("YYYY-MM-DD HH:mm"));
        $('#order_date').html($('#title_order_date').html());
		
        $('.nav.nav-tabs li').eq(order_detail_tab_index).addClass('active');
        $('.tab-content .tab-pane').eq(order_detail_tab_index).addClass("active");
        
        
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