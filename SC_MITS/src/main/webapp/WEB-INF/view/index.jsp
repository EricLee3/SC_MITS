<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<jsp:include page="../view/admin/inc/inc_header.jsp" />
<!-- END HEADER -->
<div class="clearfix">
</div>
<!-- BEGIN CONTAINER -->
<div class="page-container">
	<!-- BEGIN SAMPLE PORTLET CONFIGURATION MODAL FORM-->
	<!-- <div class="modal fade" id="portlet-config" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
					<h4 class="modal-title">Modal title</h4>
				</div>
				<div class="modal-body">
					 Widget settings form goes here
				</div>
				<div class="modal-footer">
					<button type="button" class="btn blue">Save changes</button>
					<button type="button" class="btn default" data-dismiss="modal">Close</button>
				</div>
			</div>
			/.modal-content
		</div>
		/.modal-dialog
	</div> -->
	<!-- /.modal -->
	<!-- END SAMPLE PORTLET CONFIGURATION MODAL FORM-->
	<!-- BEGIN SIDEBAR -->
	<jsp:include page="../view/admin/inc/inc_sidemenu.jsp" />
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
					Dashboard <small>dashboard & statistics</small>
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
						<!-- <li>
							<a href="#">eCommerce</a>
							<i class="fa fa-angle-right"></i>
						</li> -->
						<li>
							<a href="/admin/admin_index.html" class="ajaxify">Dashboard</a>
						</li>
						<li class="pull-right">
							<div id="dashboard-report-range" class="dashboard-date-range tooltips" data-placement="top" data-original-title="Change dashboard date range">
								<i class="icon-calendar"></i>
								<span></span>
								<i class="fa fa-angle-down"></i>
							</div>
						</li>
					</ul>
					<!-- END PAGE TITLE & BREADCRUMB-->
				</div>
			</div>
			<!-- END PAGE HEADER-->
			<!-- BEGIN PAGE CONTENT-->
			<div class="row" id="order_overall">
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat blue-madison">
						<div class="visual">
							<i class="fa fa-dollar fa-icon-medium"></i>
						</div>
						<div class="details">
							<div class="number" id="ds_tot_amount">
							</div>
							<div class="desc">
								 총 결제금액(원) <!-- Lifetime Sales -->
							</div>
						</div>
						<a class="more" href="#">
						Lifetime Sales<!-- View more <i class="m-icon-swapright m-icon-white"></i> -->
						</a>
					</div>
				</div>
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat red-intense">
						<div class="visual">
							<i class="fa fa-shopping-cart"></i>
						</div>
						<div class="details">
							<div class="number" id="ds_tot_orders">
							</div>
							<div class="desc">
								 총 오더건수
							</div>
						</div>
						<a class="more" href="#">
						 Total Orders <!-- <i class="m-icon-swapright m-icon-white"></i> -->
						</a>
					</div>
				</div>
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat purple-plum">
						<div class="visual">
							<i class="fa fa-plane"></i>
						</div>
						<div class="details">
							<div class="number" id="ds_tot_charge">
							</div>
							<div class="desc">
							총 비용(배송비+과세-할인금액)
							</div>
						</div>
						<a class="more" href="#">
						Total Charge <!-- <i class="m-icon-swapright m-icon-white"></i> -->
						</a>
					</div>
				</div>
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat green-haze">
						<div class="visual">
							<i class="fa fa-undo fa-icon-medium"></i>
						</div>
						<div class="details">
							<div class="number" id="ds_tot_cancel_amt">
							</div>
							<div class="desc">
								 총 취소금액(환불예정금액)
							</div>
						</div>
						<a class="more" href="#">
						Total Cancel Amount <!-- <i class="m-icon-swapright m-icon-white"></i> -->
						</a>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<!-- Begin: life time stats -->
					
					<!-- End: life time stats -->
					
					<div class="portlet box red-sunglo tabbable" id="ptl_tt_orderChart">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-bar-chart-o"></i>채널별 오더현황 - <span>최근 3개월</span>
							</div>
							<div class="tools">
								<!-- <a href="#portlet-config" data-toggle="modal" class="config">
								</a> -->
								<a href="javascript:;" class="reload">
								</a>
							</div>
							<div class="actions">
								<div class="btn-group">
									<a class="btn btn-default btn-sm" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
									구간선택 <i class="fa fa-angle-down"></i>
									</a>
									<ul class="dropdown-menu pull-right">
										<li>
											<a href="javascript:EcommerceIndex.getOrderReport(2);">
											<i class="icon-calendar"></i> 최근 3개월 </a>
										</li>
										<li>
											<a href="javascript:EcommerceIndex.getOrderReport(5);">
											<i class="icon-calendar"></i> 최근 6개월 </a>
										</li>
										<li>
											<a href="javascript:EcommerceIndex.getOrderReport(8);">
											<i class="icon-calendar"></i> 최근 9개월 </a>
										</li>
										<!-- <li class="divider">
										</li> -->
										<!-- <li>
											<a href="#">
											Pending <span class="badge badge-danger">
											4 </span>
											</a>
										</li> -->
									</ul>
								</div>
							</div>
						</div>
						<div class="portlet-body" id="ptl_bd_orderChart">
							<div class="portlet-tabs">
								<ul class="nav nav-tabs" style="margin-right: 120px">
									<li>
										<a href="#portlet_tab2" role="tab"  data-toggle="tab" id="statistics_amounts_tab2">
										오더건수 </a>
									</li>
									<li>
										<a href="#portlet_tab1" role="tab"  data-toggle="tab" id="statistics_amounts_tab1">
										결제금액 </a>
									</li>
									<li class="active">
										<a href="#portlet_tab0" role="tab"  data-toggle="tab" id="statistics_amounts_tab0">
										Overall </a>
									</li>
								</ul>
								<div class="tab-content">
									<div class="tab-pane active" id="portlet_tab0">
										<div id="statistics_0" class="chart">
										</div>
									</div>
									<div class="tab-pane" id="portlet_tab1">
										<div id="statistics_1" class="chart">
										</div>
									</div>
									<div class="tab-pane" id="portlet_tab2">
										<div id="statistics_2" class="chart">
										</div>
									</div>
									
								</div>
							</div>
							<div class="well no-margin no-border">
								<div class="row">
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat" id="tot_amount_month">
										<span class="label label-success">
										총 결제금액: </span>
										<h3>0.00</h3>
									</div>
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat" id="tot_count_month">
										<span class="label label-info">
										총 주문건수: </span>
										<h3>0</h3>
									</div>
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat" id="tot_charge_month">
										<span class="label label-danger">
										배송비: </span>
										<h3>0.00</h3>
									</div>
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat" id="tot_cancel_month">
										<span class="label label-warning">
										취소금액(환불예정): </span>
										<h3>0.00</h3>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-6">
					<!-- Begin: life time stats -->
					<div class="portlet box blue-steel" id="pt_order_overview">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-thumb-tack"></i>최근 오더목록
							</div>
							
							<div class="tools">
								<!-- 
								<a href="javascript:;" class="collapse">
								</a>
								<a href="#portlet-config" data-toggle="modal" class="config">
								</a> -->
								<a href="javascript:;" class="reload">
								</a>
								<!-- <a href="javascript:;" class="remove">
								</a> -->
							</div>
							<div class="actions">
								<div class="btn-group">
									<a class="btn btn-default btn-sm" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
									스토어 <i class="fa fa-angle-down"></i>
									</a>
									<ul class="dropdown-menu pull-right">
										<li>
											<a href="javascript:EcommerceIndex.getOrderListByCh('*');">
											All </a>
										</li>
										<li class="divider"> </li>
										<li>
											<a href="javascript:EcommerceIndex.getOrderListByCh('ASPB');">
											Aspen Bay </a>
										</li>
										<li>
											<a href="javascript:EcommerceIndex.getOrderListByCh('JNS');">
											Joseph & Stacey </a>
										</li>
										<li>
											<a href="javascript:EcommerceIndex.getOrderListByCh('OUTRO');">
											Outro </a>
										</li>
										<!-- <li class="divider">
										</li> -->
										<!-- <li>
											<a href="#">
											Pending <span class="badge badge-danger">
											4 </span>
											</a>
										</li> -->
									</ul>
								</div>
								<a class="btn btn-default btn-sm" href="javascript:;" id="btn_more_order">
								More
								<i class="fa fa-edit"></i>
								</a>
							</div>
						</div>
						<div class="portlet-body">
							<div class="tabbable-line" id="tab_overview">
							<ul class="nav nav-tabs">
								<li class="active">
									<a href="#overview_1" data-toggle="tab">
									최근 오더 </a>
								</li>
								<li>
									<a href="#overview_2" data-toggle="tab">
									미 출고의뢰건(담당자 확인필요) </a>
								</li>
								<li>
									<a href="#overview_3" data-toggle="tab">
									출고준비 목록 </a>
								</li>
								<li>
									<a href="#overview_4" data-toggle="tab">
									출고완료 목록 </a>
								</li>
								<li>
									<a href="#overview_5" data-toggle="tab">
									주문취소 요청목록 </a>
								</li>
								<!-- <li>
									<a href="#overview_5" data-toggle="tab">
									주문처리 에러목록 </a>
								</li> -->
								<!-- <li class="dropdown">
									<a href="#" class="dropdown-toggle" data-toggle="dropdown">
									오더처리 대기건 <i class="fa fa-angle-down"></i>
									</a>
									<ul class="dropdown-menu" role="menu">
										<li>
											<a href="#overview_4" tabindex="-1" data-toggle="tab">
											주문접수 </a>
										</li>
										<li>
											<a href="#overview_4" tabindex="-1" data-toggle="tab">
											Pending Orders </a>
										</li>
										<li>
											<a href="#overview_4" tabindex="-1" data-toggle="tab">
											Completed Orders </a>
										</li>
										<li>
											<a href="#overview_4" tabindex="-1" data-toggle="tab">
											Rejected Orders </a>
										</li>
									</ul>
								</li> -->
							</ul>
							<div class="tab-content">
								<div class="tab-pane active" id="overview_1">
									<div class="table-scrollable">
										<table class="table table-striped table-hover table-bordered" id="table_new_list">
											<thead>
											<tr>
												<th>
													 오더번호
												</th>
												<th>
													 오더일시
												</th>
												<th>
													 관리조직
												</th>
												<th>
												 	판매처
												</th>
												<th>
													 통화
												</th>
												<th>
													 금액
												</th>
												<th>
													 Status
												</th>
												<th>
												     상세
												</th>
											</tr>
											</thead>
											<tbody>
											</tbody>
											</table>
									</div>
								</div>
								<div class="tab-pane" id="overview_2">
									<div class="table-scrollable">
										<table class="table table-striped table-hover table-bordered" id="table_pending_list">
										<thead>
										<tr>
											<th>
												 오더번호
											</th>
											<th>
												 오더일시
											</th>
											<th>
												 관리조직
											</th>
											<th>
												 판매처
											</th>
											<th>
												 통화
											</th>
											<th>
												 금액
											</th>
											<th>
												 Status
											</th>
											<th>
											     상세
											</th>
										</tr>
										</thead>
										<tbody>
										</tbody>
										</table>
									</div>
								</div>
								
								<div class="tab-pane" id="overview_3">
									<div class="table-scrollable">
										<table class="table table-striped table-hover table-bordered" id="table_createShipment_list">
										<thead>
										<tr>
											<th>
												 오더번호
											</th>
											<th>
												 오더일시
											</th>
											<th>
												 관리조직
											</th>
											<th>
												 판매처
											</th>
											<th>
												 통화
											</th>
											<th>
												 금액
											</th>
											<th>
												 Status
											</th>
											<th>
											     상세
											</th>
										</tr>
										</thead>
										<tbody>
										</tbody>
										</table>
									</div>
								</div>
								
								<div class="tab-pane" id="overview_4">
									<div class="table-scrollable">
										<table class="table table-striped table-hover table-bordered" id="table_shipped_list">
										<thead>
										<tr>
											<th>
												 오더번호
											</th>
											<th>
												 오더일시
											</th>
											<th>
												 관리조직
											</th>
											<th>
												 판매처
											</th>
											<th>
												 통화
											</th>
											<th>
												 금액
											</th>
											<th>
												 Status
											</th>
											<th>
											     상세
											</th>
										</tr>
										</thead>
										<tbody>
										</tbody>
										</table>
									</div>
								</div>
								<div class="tab-pane" id="overview_5">
									<div class="table-scrollable">
										<table class="table table-striped table-hover table-bordered" id="table_cancelled_list">
										<thead>
										<tr>
											<th>
												 오더번호
											</th>
											<th>
												 오더일시
											</th>
											<th>
												 관리조직
											</th>
											<th>
												 판매처
											</th>
											<th>
												 통화
											</th>
											<th>
												 금액
											</th>
											<th>
												 Status
											</th>
											<th>
											     상세
											</th>
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
			<!-- END PAGE CONTENT-->
			
		</div>
		<!-- BEGIN CONTENT -->
	</div>
	<!-- END CONTENT -->
	<!-- BEGIN QUICK SIDEBAR -->
	<jsp:include page="../view/admin/inc/inc_quick_sidebar.jsp" />
	<!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->
<!-- BEGIN FOOTER -->
<jsp:include page="../view/admin/inc/inc_footer.jsp" />
<!-- END FOOTER -->


<!-- BEGIN Common Js -->
<jsp:include page="../view/admin/inc/inc_commonJs.jsp" />
<!-- END Common Js -->



<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="../../assets/global/plugins/flot/jquery.flot.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/flot/jquery.flot.resize.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/flot/jquery.flot.categories.js" type="text/javascript"></script>

<!-- <script src="../../assets/global/plugins/bootstrap-modal/js/bootstrap-modalmanager.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/bootstrap-modal/js/bootstrap-modal.js" type="text/javascript"></script> -->
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

<!-- for using browser history function -->
<!-- <script src="../../assets/admin/custom/jquery.address-1.5.min.js" type="text/javascript"></script> -->
<!-- END PAGE LEVEL PLUGINS -->


<script src="../../assets/global/scripts/custom-datatable.js"></script>
<script src="../../assets/admin/pages/scripts/custom-ecommerce-index.js"></script>

<!-- END PAGE LEVEL SCRIPTS -->

<script>
	
	var chartData = new Object();
	var term = 2;

	// Order Detail Page's Active Tab Index
	/* var order_detail_tab_index = 0; */

	jQuery(document).ready(function() {    
   		
		EcommerceIndex.init();
		// set DatePicker Menu
		EcommerceIndex.initDashboardDaterange();
   		
	});
	
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>