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
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/select2/select2.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/Scroller/css/dataTables.scroller.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/extensions/ColReorder/css/dataTables.colReorder.min.css"/>
<link rel="stylesheet" type="text/css" href="../../assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.css"/>

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
					Orders <small>orders listing</small>
					</h3>
					<ul class="page-breadcrumb breadcrumb">
						<li class="btn-group">
							<!-- <button type="button" class="btn blue dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="true">
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
							</ul> -->
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
					</ul>
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
								<i class="fa fa-shopping-cart"></i>Order Listing
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
									<span>
									</span>
									
									<a class="btn btn-sm green" href="javascript:orderListByDatePeroid('days', '7');">
										<i class="fa fa-calendar"></i>
										1 week
									</a>
									<a class="btn btn-sm green" href="javascript:orderListByDatePeroid('month', '1');">
										<i class="fa fa-calendar"></i>
										1 month
									</a>
									<a class="btn btn-sm green" href="javascript:orderListByDatePeroid('month', '3');">
										<i class="fa fa-calendar"></i>
										3 month
									</a>
									
									
									<!-- <select class="table-group-action-input form-control input-inline input-small input-sm">
										<option value="">Select...</option>
										<option value="1500">Scheduled</option>
										<option value="3200">Released</option>
										<option value="3700">Confirm Shipment</option>
										<option value="9000">Close</option>
									</select>
									<button class="btn btn-sm yellow table-group-action-submit"><i class="fa fa-check"></i> Submit</button> -->
								</div>
								<table class="table table-striped table-bordered table-hover" id="datatable_orders">
								<thead>
								<tr role="row" class="heading">
									<!-- <th width="2%">
										<input type="checkbox" class="group-checkable">
										<input type="hidden" class="form-filter" name="doc_type" value="0001">
									</th> -->
									<th width="2%">
										 &nbsp;#
									</th>
									<th width="5%">
										 Order&nbsp;#
									</th>
									<th width="15%">
										 Order&nbsp;Date
									</th>
									<th width="10%">
										 EnterPrise
									</th>
									<th width="10%">
										 Seller
									</th>
									<th width="10%">
										 Customer
									</th>
									<th width="10%">
										 Mobile
									</th>
									<th width="15%">
										 Email
									</th>
									<!-- <th width="10%">
										 Payment&nbsp;Type
									</th> -->
									<th width="10%">
										 Amount
									</th>
									<th width="15%">
										 Status
									</th>
									<th width="10%">
										 Actions
									</th>
								</tr>
								<tr role="row" class="filter">
									<!-- <td>
									</td> -->
									<td>
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="order_id">
									</td>
									<td>
										<div class="input-group date date-picker margin-bottom-5" data-date-format="yyyy-mm-dd">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_date_from" placeholder="From">
											<span class="input-group-btn">
											<button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
										<div class="input-group date date-picker" data-date-format="yyyy-mm-dd">
											<input type="text" class="form-control form-filter input-sm" readonly name="order_date_to" placeholder="To">
											<span class="input-group-btn">
											<button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button>
											</span>
										</div>
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="ent_code">
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="seller_code">
									</td>
									<td>
										<!-- <div class="margin-bottom-5">
											<input type="text" class="form-control form-filter input-sm" name="order_base_price_from" placeholder="From"/>
										</div>
										<input type="text" class="form-control form-filter input-sm" name="order_base_price_to" placeholder="To"/> -->
										<div class="input-group margin-bottom-5">
											<input type="text" class="form-control form-filter input-sm" name="cust_fname" placeholder="First Name"/>
										</div>
										<div class="input-group">
											<input type="text" class="form-control form-filter input-sm" name="cust_lname" placeholder="Last Name"/>
										</div>
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="cust_phone">
									</td>
									<td>
										<input type="text" class="form-control form-filter input-sm" name="cust_email">
									</td>
									<!-- <td>
										<select name="payment_type" class="form-control form-filter input-sm">
											<option value="">Select...</option>
											<option value="A">All</option>
											<option value="CREDIT_CARD">CREDIT_CARD</option>
										</select>
									</td> -->
									<td>
										<!-- <div class="margin-bottom-5">
											<input type="text" class="form-control form-filter input-sm margin-bottom-5 clearfix" name="order_purchase_price_from" placeholder="From"/>
										</div>
										<input type="text" class="form-control form-filter input-sm" name="order_purchase_price_to" placeholder="To"/> -->
										<!-- <input type="text" class="form-control form-filter input-sm" name="total_amount"/> -->
									</td>
									<td>
										<select name="order_status" class="form-control form-filter input-sm">
											<option value="">Select...</option>
											<option value="A">All</option>
											<option value="1100">주문접수</option>
											<option value="3200">주문확정</option>
											<option value="3350">출고준비</option>
											<option value="3700">출고완료</option>
											<option value="1300">재고부족</option>
											<option value="9000">주문취소</option>
										</select>
									</td>
									<td>
										<div class="margin-bottom-5">
											<button class="btn btn-sm yellow filter-submit margin-bottom" id="btn_orderList"><i class="fa fa-search"></i> Search</button>
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


<script src="../../assets/global/scripts/custom-datatable.js"></script>
<script src="../../assets/admin/pages/scripts/custom-ecommerce-orders.js"></script>


<script>


	jQuery(document).ready(function() {    
		
		EcommerceOrders.init('#datatable_orders');
		
	    var table = $('#datatable_orders').dataTable();
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
	    
	    <%
	    	String action = (String)request.getAttribute("action");
	    	String status = (String)request.getAttribute("status");
	    	
	    	if(action != null && "true".equals(action)){
	    %>
		    $('select.form-filter[name="order_status"]').val('<%=status%>');
			$('#btn_orderList').click();
		
		<% } %>
	});
	
	function orderListByDatePeroid(dateType, term){
		
		var sDate = moment().subtract(dateType, term).format('YYYY-MM-DD');
		var eDate = moment().format('YYYY-MM-DD');
		
		$('input.form-filter[name="order_date_from"]').val(sDate);
		$('input.form-filter[name="order_date_to"]').val(eDate);
		
		$('#btn_orderList').click();
	}
	
	
	function getQueryVariable(variable) {
	  //var query = window.location.search.substring(1);
	  var query = window.location.search.toString();
	  
	  alert(query);
	  
	  if(query.indexOf('?') == -1 ) return '';
	  query = query.substring(query.indexOf('?')+1);
	  
	  var vars = query.split("&");
	  for (var i=0;i<vars.length;i++) {
	    var pair = vars[i].split("=");
	    if (pair[0] == variable) {
	      return pair[1];
	    }
	  } 
	  //alert('Query Variable ' + variable + ' not found');
	}
	
</script>