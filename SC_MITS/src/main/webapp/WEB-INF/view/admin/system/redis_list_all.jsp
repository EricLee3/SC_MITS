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
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/Scroller/css/dataTables.scroller.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/ColReorder/css/dataTables.colReorder.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.css"/>

<link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal-bs3patch.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal.css" rel="stylesheet" type="text/css"/>
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
<!-- BEGIN PAGE HEADER-->
<div class="row">
	<div class="col-md-12">
		<!-- BEGIN PAGE TITLE & BREADCRUMB-->
		<h3 class="page-title">
		연동데이타 정보 <small>view order details</small>
		</h3>
		<!-- <ul class="page-breadcrumb breadcrumb">
			<li class="btn-group">
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
			</li>
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
		</ul> -->
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
						<i class="fa fa-cogs"></i>KOLOR - Aspen Bay
					</div>
					<div class="tools">
						<a href="javascript:;" class="collapse">
						</a>
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
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
							    <!-- 주문생성 -->
							    <tr>
									<td>
									</td>
									<td>
										 ${KOLOR.create.name}
									</td>
									<td>
										 ${KOLOR.create.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.create.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info" >Details</span> </a>
									</td>
								</tr>
								<!-- 주문확정 대상 -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.release.name}
									</td>
									<td>
										 ${KOLOR.release.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.release.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								
								<!-- 주문상태 업데이트  -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_update_s2m.name}
									</td>
									<td>
										 ${KOLOR.info_update_s2m.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_update_s2m.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 &nbsp;
									</td>
									<td>
									</td>
									<td class="text-right">
									</td>
									<td>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
							
							<h4>주문정보 연동데이타 - SC-CUBE</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
							    <!-- 주문상태 업데이트  -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_update_s2c.name}
									</td>
									<td>
										 ${KOLOR.info_update_s2c.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_update_s2c.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_update_c2s.name}
									</td>
									<td>
										 ${KOLOR.info_update_c2s.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_update_c2s.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
						</div>
						<div class="col-md-6 col-sm-6">
							
							<h4>Cube 요청데이터 결과</h4>
							<div class="table-container">
							<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
							    <!-- 주문상태 업데이트  -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_cube_3202_90.name}
									</td>
									<td>
										 ${KOLOR.info_cube_3202_90.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_cube_3202_90.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_cube_3202_09.name}
									</td>
									<td>
										 ${KOLOR.info_cube_3202_09.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_cube_3202_09.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_cube_9000_req.name}
									</td>
									<td>
										 ${KOLOR.info_cube_9000_req.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_cube_9000_req.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_cube_9000_res.name}
									</td>
									<td>
										 ${KOLOR.info_cube_9000_res.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_cube_9000_res.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
							
							<h4>주문정보 에러데이타</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR_ERR.info_create_err.name}
									</td>
									<td>
										 ${KOLOR_ERR.info_create_err.desc}
									</td>
									<td class="text-right">
										 ${KOLOR_ERR.info_create_err.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								
								<c:forEach items="${KOLOR_ERR.err_key_list}" var="errKeyList">
							    <tr>
									<td>
									</td>
									<td>
										 ${errKeyList.name}
									</td>
									<td>
										 ${errKeyList.desc}
									</td>
									<td class="text-right">
										 ${errKeyList.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
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
							<h4>상품정보 연동데이타 - SC-CUBE</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
								<!-- 상품연동  -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_product_c2s.name}
									</td>
									<td>
										 ${KOLOR.info_product_c2s.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_product_c2s.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_product_s2c.name}
									</td>
									<td>
										 ${KOLOR.info_product_s2c.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_product_s2c.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								
								</tbody>
								</table>
							</div>
							<h4>상품정보 연동데이타 - SC-MA</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_product_s2m.name}
									</td>
									<td>
										 ${KOLOR.info_product_s2m.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_product_s2m.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
						</div>
						<div class="col-md-6 col-sm-6">
						
							<h4>상품정보 연동 에러데이타</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
								<!-- 상품에러 CA -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR_ERR.info_product_err_ca.name}
									</td>
									<td>
										 ${KOLOR_ERR.info_product_err_ca.desc}
									</td>
									<td class="text-right">
										 ${KOLOR_ERR.info_product_err_ca.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<!-- 상품에러 MA -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR_ERR.info_product_err_ma.name}
									</td>
									<td>
										 ${KOLOR_ERR.info_product_err_ma.desc}
									</td>
									<td class="text-right">
										 ${KOLOR_ERR.info_product_err_ma.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
						
						</div>
					</div>
					<!-- Inventory -->
					<div class="row">
						<div class="col-md-6 col-sm-6">
							<h4>재고정보 연동데이타 - SC-CUBE</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
								<!-- 재고연동  -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_inventory_c2s.name}
									</td>
									<td>
										 ${KOLOR.info_inventory_c2s.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_inventory_c2s.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_inventory_s2c.name}
									</td>
									<td>
										 ${KOLOR.info_inventory_s2c.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_inventory_s2c.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
							
							<h4>재고정보 연동데이타 - SC-MA</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
								<!-- 재고연동  -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_inventory_s2m.name}
									</td>
									<td>
										 ${KOLOR.info_inventory_s2m.desc}
									</td>
									<td class="text-right">
										 ${KOLOR.info_inventory_c2s.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
						</div>
						<div class="col-md-6 col-sm-6">
							
							
							<h4>재고정보 연동 에러데이타</h4>
							<div class="table-container">
								<table class="table table-hover table-condensed" id="table_orderLine">
								<thead>
								<tr role="row" class="heading">
									<th width="2%">
										<!-- <input type="checkbox" class="group-checkable"> -->
									</th>
									<th>
										 Key Name
									</th>
									<th>
										 Name
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
								<!-- 재고에러 CA -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR_ERR.info_inventory_err_ca.name}
									</td>
									<td>
										 ${KOLOR_ERR.info_inventory_err_ca.desc}
									</td>
									<td class="text-right">
										 ${KOLOR_ERR.info_inventory_err_ca.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<!-- 재고에러 CA -->
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR_ERR.info_inventory_err_ma.name}
									</td>
									<td>
										 ${KOLOR_ERR.info_inventory_err_ma.desc}
									</td>
									<td class="text-right">
										 ${KOLOR_ERR.info_inventory_err_ma.size}
									</td>
									<td>
										<a class="pull-right" data-toggle="modal" id="key-detail" >
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								</tbody>
								</table>
								<!-- Detail DataList Modal -->
								<div class="modal container fade" id="md_key_detail" tabindex="-1" style="height:350px;" role="dialog" aria-hidden="true">
								</div>
								
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
</div>

<form name="form_action" id="form_action" method="POST">
	<input type="hidden" name="t_key_data">
	<!-- <input type="hidden" name="key">
	<input type="hidden" name="index"> -->
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

<!-- END PAGE LEVEL PLUGINS -->

<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="../../assets/global/plugins/bootstrap-modal/js/bootstrap-modalmanager.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/bootstrap-modal/js/bootstrap-modal.js" type="text/javascript"></script>

<!-- data table -->
<script type="text/javascript" src="../../assets/global/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/extensions/TableTools/js/dataTables.tableTools.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/extensions/ColReorder/js/dataTables.colReorder.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/extensions/Scroller/js/dataTables.scroller.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../../assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js"></script>

<!-- END PAGE LEVEL SCRIPTS -->

<script>

	jQuery(document).ready(function() {
        
		// general settings
        $.fn.modal.defaults.spinner = $.fn.modalmanager.defaults.spinner = 
          '<div class="loading-spinner" style="width: 200px; margin-left: -100px;">' +
            '<div class="progress progress-striped active">' +
              '<div class="progress-bar" style="width: 100%;"></div>' +
            '</div>' +
          '</div>';

        $.fn.modalmanager.defaults.resize = true;
		
		
		//ajax demo:
        var $modal = $('#md_key_detail');

        $('a#key-detail').on('click', function(){
          // create the backdrop and wait for next modal to be triggered
          $('body').modalmanager('loading');
		  
          var keyName =  $(this).parent().parent().children().eq(1).text();
          keyName = $.trim(keyName);
          
          var keyDesc =  $(this).parent().parent().children().eq(2).text();
          keyDesc = $.trim(keyDesc);
          
          
          setTimeout(function(){
              $modal.load('/admin/system/redis_detail_list.html', '', function(){
	              
            	  		$('#md_key_detail .modal-header .modal-title').text(keyDesc+" ("+keyName+") List");
            	  
	            	  	
            	  		setDatatable(keyName);
            	  		$modal.modal();
	            });
        	  }, 200);
        });
		
        
        
        
        
        
        
        /* $modal.on('click', '.update', function(){
          $modal.modal('loading');
          setTimeout(function(){
            $modal
              .modal('loading')
              .find('.modal-body')
                .prepend('<div class="alert alert-info fade in">' +
                  'Updated!<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                '</div>');
          }, 1000);
        }); */
		
     });	
	
function setDatatable(keyName){
    	
    	$('#data_list_table').dataTable( {
	        "ajax": "/system/getRedisKeyData.sc?key="+keyName,
	        "paging":   false,
		    "ordering": false,
		    "info":     false,
		 	"processing": true,
		    "dom":"",
		    "dataType":"xml",
	        "columns": [
	            { "data": "seq" },
	            { "data": function render(data, type, row)
	  				{
						return '<textarea class="form-control input-sm" rows="5" name="">'+data["keyData"]+'</textarea>';
	  				 }
	            },
	            {
  				  "data": function render(data, type, row)
  				  {
						return '<a href="javascript:retryKey(\''+keyName+'\','+data["seq"]+');" class="btn default btn-xs blue-stripe">Retry</a>';
  				  }
	            },
	            {
  				  "data": function render(data, type, row)
  				  {
						return "<a href='javascript:deleteKey(\""+keyName+"\","+data["seq"]+");' class='btn default btn-xs red-stripe'>Delete</a>";
  				  }
	            }
	        ]
	    } );
	
	
}


function retryKey(key, index){
	
}

function deleteKey(key, index, data){
	
	/* $("#form_action input[name='t_key_data']").val(data);
	
	$.ajax({
		url: '/system/deleteKey.sc?key='+key+'&index='+index,
		data: $('#form_action').serialize(),
		success:function(data)
		{
			var table = $("#data_list_table").dataTable();
			table.api().ajax.reload();	
		}
	
	}); */
}
     
 </script>
<!-- END JAVASCRIPTS -->

</body>
<!-- END BODY -->
</html>