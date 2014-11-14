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
<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/select2/select2.css"/> -->
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/Scroller/css/dataTables.scroller.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/ColReorder/css/dataTables.colReorder.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.css"/>

<!-- <link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal-bs3patch.css" rel="stylesheet" type="text/css"/>
<link href="../../assets/global/plugins/bootstrap-modal/css/bootstrap-modal.css" rel="stylesheet" type="text/css"/> -->

<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/clockface/css/clockface.css"/>-->
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-datepicker/css/datepicker3.css"/>
<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css"/> -->
<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css"/> -->
<!-- <link rel="stylesheet" type="text/css" href="../../assets/global/plugins/bootstrap-datetimepicker/css/datetimepicker.css"/>  -->
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
					클레임 관리 <small>claim management</small>
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
							<a href="/admin/orders/order_list.html" class="ajaxify">오더조회</a>
						</li>
					</ul> -->
					<!-- END PAGE TITLE & BREADCRUMB-->
				</div>
			</div>
			<!-- END PAGE HEADER-->
			<!-- BEGIN PAGE CONTENT-->
			<div class="row">
				<div class="col-md-12">
					<!-- <div class="note note-danger">
						<p>
							NOTE: The below datatable is not connected to a real database so the filter and sorting is just simulated for demo purposes only.
						</p>
					</div> -->
					<!-- Begin: life time stats -->
					<div class="portlet">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-circle-o"></i>주문취소요청 현황
							</div>
							<div class="actions">
								<!-- <a href="#" class="btn default yellow-stripe">
								<i class="fa fa-plus"></i>
								<span class="hidden-480">
								New Order </span>
								</a> --> 
								<div class="btn-group">
									<!-- <a class="btn default yellow-stripe" href="#" data-toggle="dropdown">
									<i class="fa fa-share"></i>
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
											Print Invoices </a>
										</li>
									</ul> -->
								</div> 
							</div>
						</div>
						<div class="portlet-body">
							<div class="table-container">
								
								
								<div class="table-actions-wrapper">
									<!-- <span>
									</span>
									<a class="btn btn-sm blue" href="javascript:orderListByDatePeroid('days', 0, 1);">
										<i class="fa fa-calendar"></i>
										Yesterday
									</a>
									<a class="btn btn-sm green" href="javascript:orderListByDatePeroid('days', 0, 0);">
										<i class="fa fa-calendar"></i>
										Today
									</a>
									<a class="btn btn-sm green" href="javascript:orderListByDatePeroid('days', 7, 0);">
										<i class="fa fa-calendar"></i>
										1 week
									</a>
									<a class="btn btn-sm green" href="javascript:orderListByDatePeroid('month', 1, 0);">
										<i class="fa fa-calendar"></i>
										1 month
									</a>
									<a class="btn btn-sm green" href="javascript:orderListByDatePeroid('month', 3, 0);">
										<i class="fa fa-calendar"></i>
										3 month
									</a> -->
									
									
									<!-- <select class="table-group-action-input form-control input-inline input-small input-sm">
										<option value="">Select...</option>
										<option value="1500">Scheduled</option>
										<option value="3200">Released</option>
										<option value="3700">Confirm Shipment</option>
										<option value="9000">Close</option>
									</select>
									<button class="btn btn-sm yellow table-group-action-submit"><i class="fa fa-check"></i> Submit</button> -->
								</div>
								<table class="table table-striped table-bordered table-hover" id="datatable_cancel_orders">
								<thead>
								<tr role="row" class="heading">
									<!-- <th width="2%">
										<input type="checkbox" class="group-checkable">
										<input type="hidden" class="search-filter" name="doc_type" value="0001">
									</th> -->
									<!-- <th width="2%">
										 &nbsp;#
									</th> -->
									<th width="7%">
										 오더번호&nbsp;#
									</th>
									<th width="10%">
										 오더생성일
									</th>
									<th width="8%">
										 관리조직
									</th>
									<th width="8%">
										 판매채널
									</th>
									<th width="10%">
										 고객명
									</th>
									<th width="8%">
										 연락처
									</th>
									<!-- <th width="15%">
										 이메일
									</th> -->
									<!-- <th width="10%">
										 Payment&nbsp;Type
									</th> -->
									<th width="8%">
										 결제금액
									</th>
									<th width="8%">
										 2차 DOS
									</th>
									<th width="8%">
										 원주문상태
									</th>
									<th width="8%">
										 취소요청상태
									</th>
									<th width="10%">
										 취소요청결과
									</th>
									<th width="8%">
										 Actions
									</th>
								</tr>
								<tr role="row" class="filter">
									<!-- <td>
										<span class="row-details row-details-close" id="row_all_exp"></span>
									</td> -->
									<td>
										<input type="text" class="form-control search-filter input-sm" name="order_id">
									</td>
									<td>
										<input class="form-control form-control-inline search-filter input-sm date-picker" size="8" type="text" data-date-start-date="-3m" data-date-format="yyyy-mm-dd"/>
									</td>
									<td>
										<!-- <input type="text" class="form-control search-filter input-sm" name="ent_code"> -->
										<select name="ent_code" class="form-control search-filter input-sm">
											<option value="">Select...</option>
											<option value="KOLOR">KOLOR</option>
										</select>
									</td>
									<td>
										<!-- <input type="text" class="form-control search-filter input-sm" name="seller_code"> -->
										<select name="seller_code" class="form-control search-filter input-sm">
											<option value="">Select...</option>
											<option value="ASPB">Aspen Bay</option>
										</select>
									</td>
									<td>
										<!-- <div class="margin-bottom-5">
											<input type="text" class="form-control search-filter input-sm" name="order_base_price_from" placeholder="From"/>
										</div>
										<input type="text" class="form-control search-filter input-sm" name="order_base_price_to" placeholder="To"/> -->
										<!-- <div class="input-group margin-bottom-5"> -->
											<input type="text" class="form-control search-filter input-sm" name="cust_fname" placeholder=""/>
										<!-- </div> -->
									</td>
									<td>
										<input type="text" class="form-control search-filter input-sm" name="cust_phone">
									</td>
									<!-- <td>
										<input type="text" class="form-control search-filter input-sm" name="cust_email">
									</td> -->
									<td>
										<!-- <div class="margin-bottom-5">
											<input type="text" class="form-control search-filter input-sm margin-bottom-5 clearfix" name="order_purchase_price_from" placeholder="From"/>
										</div>
										<input type="text" class="form-control search-filter input-sm" name="order_purchase_price_to" placeholder="To"/> -->
										<!-- <input type="text" class="form-control search-filter input-sm" name="total_amount"/> -->
									</td>
									<td>
										<input type="text" class="form-control search-filter input-sm" name="vendor_id">
									</td>
									<td>
										<select name="status_text" class="form-control search-filter input-sm">
											<option value="">Select...</option>
											<option value="출고의뢰">출고의뢰</option>
											<option value="출고준비">출고준비</option>
										</select>
									</td>
									<td>
									</td>
									<td>
										<select name="res_status_text" class="form-control search-filter input-sm">
											<option value="">Select...</option>
											<option value="01">[01] 정상</option>
											<option value="02">[02] 기처리건</option>
											<option value="09">[09] 실패</option>
											<option value="90">[90] 출고확정건</option>
										</select>
									</td>
									<td>
										<div class="margin-bottom-5">
											<button class="btn btn-sm yellow filter-submit margin-bottom" id="btn_orderList"><i class="fa fa-search"></i> Search</button>
										</div>
										<!-- <button class="btn btn-sm red" id="filter_reset"><i class="fa fa-times"></i> Reset</button> -->
									</td>
								</tr>
								</thead>
								<tbody>
								</tbody>
								</table>
							</div>
						</div>
					</div>
					<!-- End: life time stats -->
				</div>
			</div>
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


<script src="../../assets/global/scripts/custom-datatable.js"></script>
<script src="../../assets/admin/pages/scripts/custom-cancel-orders.js"></script>


<script>


	jQuery(document).ready(function() {    
		
		EcommerceOrders.init('#datatable_cancel_orders');
		
	    var table = $('#datatable_cancel_orders').dataTable();
	    var tableTools = new $.fn.dataTable.TableTools( table, {
	        
	    	"sSwfPath": "../../assets/global/plugins/datatables/extensions/TableTools/swf/copy_csv_xls_pdf.swf",
	    	"aButtons": [
	                        {
	                        	"sExtends": "copy",
		                        "sButtonText": "Copy to clipboard",
		                        "sMessage": "Copied!"
	                        },
	                        /* {
	                        	"sExtends": "print",
		                        "sButtonText": "Print",
		                        "sInfo": 'Please press "CTR+P" to print or "ESC" to quit',
		                        "sMessage": "Generated by DataTables"
	                        }, */
	                        {
	                            "sExtends":    "collection",
	                            "sButtonText": "Save",
	                            "aButtons":    [ "csv", "xls", "pdf" ]
	                        }
	                    ]
	    } );
	    $( tableTools.fnContainer() ).insertAfter('div.btn-group');
	    
	    
	});
	
	
</script>