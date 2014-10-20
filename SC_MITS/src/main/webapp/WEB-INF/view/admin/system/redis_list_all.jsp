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
										 ${fn:length(KOLOR.create.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
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
										 ${fn:length(KOLOR.release.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
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
										 ${fn:length(KOLOR.info_update_s2m.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
								<tr>
									<td>
									</td>
									<td>
										 ${KOLOR.info_update_m2s.name}
									</td>
									<td>
										 ${KOLOR.info_update_m2s.desc}
									</td>
									<td class="text-right">
										 ${fn:length(KOLOR.info_update_m2s.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
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
										 ${fn:length(KOLOR.info_update_s2c.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
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
										 ${fn:length(KOLOR.info_update_c2s.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
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
										 ${fn:length(KOLOR.info_product_s2m.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
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
										 ${fn:length(KOLOR.info_product_c2s.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
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
										 ${fn:length(KOLOR.info_product_s2c.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
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
										 ${fn:length(KOLOR.info_inventory_c2s.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
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
										 ${fn:length(KOLOR.info_inventory_c2s.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
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
										 ${fn:length(KOLOR.info_inventory_s2c.list)}
									</td>
									<td>
										<a href="#" class="pull-right" target="_blank">
										<span class="label label-sm label-info">Details</span> </a>
									</td>
								</tr>
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
<!-- END PAGE LEVEL SCRIPTS -->

<script>

	jQuery(document).ready(function() {
        
     });
     
 </script>
<!-- END JAVASCRIPTS -->

</body>
<!-- END BODY -->
</html>