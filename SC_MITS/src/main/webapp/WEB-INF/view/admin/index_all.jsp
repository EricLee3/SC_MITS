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
<title>M.I.T.S | SterlingOMS - Dashboard</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<meta content="" name="description"/>
<meta content="" name="author"/>
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN THEME STYLES -->
<link href="${pageContext.request.contextPath}/assets/global/css/components.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/global/css/plugins.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css"/>
<link id="style_color" href="${pageContext.request.contextPath}/assets/admin/layout/css/themes/default.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/assets/admin/layout/css/custom.css" rel="stylesheet" type="text/css"/>
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
<%@ include file="/inc/inc_header.html" %>
<!-- END HEADER -->
<div class="clearfix">
</div>
<!-- BEGIN CONTAINER -->
<div class="page-container">
	<!-- BEGIN SIDEBAR -->
	<%@ include file="/inc/inc_sidemenu.html" %>
	<!-- END SIDEBAR -->
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
			<!-- BEGIN SAMPLE PORTLET CONFIGURATION MODAL FORM-->
			<div class="modal fade" id="portlet-config" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.modal -->
			<!-- END SAMPLE PORTLET CONFIGURATION MODAL FORM-->
			<!-- BEGIN STYLE CUSTOMIZER -->
			<div class="theme-panel hidden-xs hidden-sm">
				<div class="toggler">
				</div>
				<div class="toggler-close">
				</div>
				<div class="theme-options">
					<div class="theme-option theme-colors clearfix">
						<span>
						THEME COLOR </span>
						<ul>
							<li class="color-default current tooltips" data-style="default" data-original-title="Default">
							</li>
							<li class="color-darkblue tooltips" data-style="darkblue" data-original-title="Dark Blue">
							</li>
							<li class="color-blue tooltips" data-style="blue" data-original-title="Blue">
							</li>
							<li class="color-grey tooltips" data-style="grey" data-original-title="Grey">
							</li>
							<li class="color-light tooltips" data-style="light" data-original-title="Light">
							</li>
							<li class="color-light2 tooltips" data-style="light2" data-html="true" data-original-title="Light 2">
							</li>
						</ul>
					</div>
					<div class="theme-option">
						<span>
						Layout </span>
						<select class="layout-option form-control input-small">
							<option value="fluid" selected="selected">Fluid</option>
							<option value="boxed">Boxed</option>
						</select>
					</div>
					<div class="theme-option">
						<span>
						Header </span>
						<select class="page-header-option form-control input-small">
							<option value="fixed" selected="selected">Fixed</option>
							<option value="default">Default</option>
						</select>
					</div>
					<div class="theme-option">
						<span>
						Sidebar </span>
						<select class="sidebar-option form-control input-small">
							<option value="fixed">Fixed</option>
							<option value="default" selected="selected">Default</option>
						</select>
					</div>
					<div class="theme-option">
						<span>
						Sidebar Position </span>
						<select class="sidebar-pos-option form-control input-small">
							<option value="left" selected="selected">Left</option>
							<option value="right">Right</option>
						</select>
					</div>
					<div class="theme-option">
						<span>
						Footer </span>
						<select class="page-footer-option form-control input-small">
							<option value="fixed">Fixed</option>
							<option value="default" selected="selected">Default</option>
						</select>
					</div>
				</div>
			</div>
			<!-- END STYLE CUSTOMIZER -->
			<!-- BEGIN PAGE HEADER-->
			<div class="row">
				<div class="col-md-12">
					<!-- BEGIN PAGE TITLE & BREADCRUMB-->
					<h3 class="page-title">
					Dashboard <small>dashboard & statistics</small>
					</h3>
					<ul class="page-breadcrumb breadcrumb">
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
							<a href="#">eCommerce</a>
							<i class="fa fa-angle-right"></i>
						</li>
						<li>
							<a href="#">Dashboard</a>
						</li>
					</ul>
					<!-- END PAGE TITLE & BREADCRUMB-->
				</div>
			</div>
			<!-- END PAGE HEADER-->
			<!-- BEGIN PAGE CONTENT-->
			<div class="row">
				<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12 margin-bottom-10">
					<div class="dashboard-stat blue-madison">
						<div class="visual">
							<i class="fa fa-briefcase fa-icon-medium"></i>
						</div>
						<div class="details">
							<div class="number">
								 $168,492.54
							</div>
							<div class="desc">
								 Lifetime Sales
							</div>
						</div>
						<a class="more" href="#">
						View more <i class="m-icon-swapright m-icon-white"></i>
						</a>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
					<div class="dashboard-stat red-intense">
						<div class="visual">
							<i class="fa fa-shopping-cart"></i>
						</div>
						<div class="details">
							<div class="number">
								 1,127,390
							</div>
							<div class="desc">
								 Total Orders
							</div>
						</div>
						<a class="more" href="#">
						View more <i class="m-icon-swapright m-icon-white"></i>
						</a>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
					<div class="dashboard-stat green-haze">
						<div class="visual">
							<i class="fa fa-group fa-icon-medium"></i>
						</div>
						<div class="details">
							<div class="number">
								 $670.54
							</div>
							<div class="desc">
								 Average Orders
							</div>
						</div>
						<a class="more" href="#">
						View more <i class="m-icon-swapright m-icon-white"></i>
						</a>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<!-- Begin: life time stats -->
					<div class="portlet box blue-steel">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-thumb-tack"></i>Overview
							</div>
							<div class="tools">
								<a href="javascript:;" class="collapse">
								</a>
								<a href="#portlet-config" data-toggle="modal" class="config">
								</a>
								<a href="javascript:;" class="reload">
								</a>
								<a href="javascript:;" class="remove">
								</a>
							</div>
						</div>
						<div class="portlet-body">
							<ul class="nav nav-tabs">
								<li class="active">
									<a href="#overview_1" data-toggle="tab">
									Top Selling </a>
								</li>
								<li>
									<a href="#overview_2" data-toggle="tab">
									Most Viewed </a>
								</li>
								<li>
									<a href="#overview_3" data-toggle="tab">
									New Customers </a>
								</li>
								<li class="dropdown">
									<a href="#" class="dropdown-toggle" data-toggle="dropdown">
									Orders <i class="fa fa-angle-down"></i>
									</a>
									<ul class="dropdown-menu" role="menu">
										<li>
											<a href="#overview_4" tabindex="-1" data-toggle="tab">
											Latest 10 Orders </a>
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
								</li>
							</ul>
							<div class="tab-content">
								<div class="tab-pane active" id="overview_1">
									<div class="table-responsive">
										<table class="table table-striped table-hover table-bordered">
										<thead>
										<tr>
											<th>
												 Product Name
											</th>
											<th>
												 Price
											</th>
											<th>
												 Sold
											</th>
											<th>
											</th>
										</tr>
										</thead>
										<tbody>
										<tr>
											<td>
												<a href="#">
												Apple iPhone 4s - 16GB - Black </a>
											</td>
											<td>
												 $625.50
											</td>
											<td>
												 809
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Samsung Galaxy S III SGH-I747 - 16GB </a>
											</td>
											<td>
												 $915.50
											</td>
											<td>
												 6709
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Motorola Droid 4 XT894 - 16GB - Black </a>
											</td>
											<td>
												 $878.50
											</td>
											<td>
												 784
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Regatta Luca 3 in 1 Jacket </a>
											</td>
											<td>
												 $25.50
											</td>
											<td>
												 1245
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Samsung Galaxy Note 3 </a>
											</td>
											<td>
												 $925.50
											</td>
											<td>
												 21245
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Inoval Digital Pen </a>
											</td>
											<td>
												 $125.50
											</td>
											<td>
												 1245
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Metronic - Responsive Admin + Frontend Theme </a>
											</td>
											<td>
												 $20.00
											</td>
											<td>
												 11190
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										</tbody>
										</table>
									</div>
								</div>
								<div class="tab-pane" id="overview_2">
									<div class="table-responsive">
										<table class="table table-striped table-hover table-bordered">
										<thead>
										<tr>
											<th>
												 Product Name
											</th>
											<th>
												 Price
											</th>
											<th>
												 Views
											</th>
											<th>
											</th>
										</tr>
										</thead>
										<tbody>
										<tr>
											<td>
												<a href="#">
												Metronic - Responsive Admin + Frontend Theme </a>
											</td>
											<td>
												 $20.00
											</td>
											<td>
												 11190
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Regatta Luca 3 in 1 Jacket </a>
											</td>
											<td>
												 $25.50
											</td>
											<td>
												 1245
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Apple iPhone 4s - 16GB - Black </a>
											</td>
											<td>
												 $625.50
											</td>
											<td>
												 809
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Samsung Galaxy S III SGH-I747 - 16GB </a>
											</td>
											<td>
												 $915.50
											</td>
											<td>
												 6709
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Motorola Droid 4 XT894 - 16GB - Black </a>
											</td>
											<td>
												 $878.50
											</td>
											<td>
												 784
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Samsung Galaxy Note 3 </a>
											</td>
											<td>
												 $925.50
											</td>
											<td>
												 21245
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Inoval Digital Pen </a>
											</td>
											<td>
												 $125.50
											</td>
											<td>
												 1245
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										</tbody>
										</table>
									</div>
								</div>
								<div class="tab-pane" id="overview_3">
									<div class="table-responsive">
										<table class="table table-striped table-hover table-bordered">
										<thead>
										<tr>
											<th>
												 Customer Name
											</th>
											<th>
												 Total Orders
											</th>
											<th>
												 Total Amount
											</th>
											<th>
											</th>
										</tr>
										</thead>
										<tbody>
										<tr>
											<td>
												<a href="#">
												David Wilson </a>
											</td>
											<td>
												 3
											</td>
											<td>
												 $625.50
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Amanda Nilson </a>
											</td>
											<td>
												 4
											</td>
											<td>
												 $12625.50
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Jhon Doe </a>
											</td>
											<td>
												 2
											</td>
											<td>
												 $125.00
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Bill Chang </a>
											</td>
											<td>
												 45
											</td>
											<td>
												 $12,125.70
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Paul Strong </a>
											</td>
											<td>
												 1
											</td>
											<td>
												 $890.85
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Jane Hilson </a>
											</td>
											<td>
												 5
											</td>
											<td>
												 $239.85
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Patrick Walker </a>
											</td>
											<td>
												 2
											</td>
											<td>
												 $1239.85
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										</tbody>
										</table>
									</div>
								</div>
								<div class="tab-pane" id="overview_4">
									<div class="table-responsive">
										<table class="table table-striped table-hover table-bordered">
										<thead>
										<tr>
											<th>
												 Customer Name
											</th>
											<th>
												 Date
											</th>
											<th>
												 Amount
											</th>
											<th>
												 Status
											</th>
											<th>
											</th>
										</tr>
										</thead>
										<tbody>
										<tr>
											<td>
												<a href="#">
												David Wilson </a>
											</td>
											<td>
												 3 Jan, 2013
											</td>
											<td>
												 $625.50
											</td>
											<td>
												<span class="label label-sm label-warning">
												Pending </span>
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Amanda Nilson </a>
											</td>
											<td>
												 13 Feb, 2013
											</td>
											<td>
												 $12625.50
											</td>
											<td>
												<span class="label label-sm label-warning">
												Pending </span>
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Jhon Doe </a>
											</td>
											<td>
												 20 Mar, 2013
											</td>
											<td>
												 $125.00
											</td>
											<td>
												<span class="label label-sm label-success">
												Success </span>
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Bill Chang </a>
											</td>
											<td>
												 29 May, 2013
											</td>
											<td>
												 $12,125.70
											</td>
											<td>
												<span class="label label-sm label-info">
												In Process </span>
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Paul Strong </a>
											</td>
											<td>
												 1 Jun, 2013
											</td>
											<td>
												 $890.85
											</td>
											<td>
												<span class="label label-sm label-success">
												Success </span>
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Jane Hilson </a>
											</td>
											<td>
												 5 Aug, 2013
											</td>
											<td>
												 $239.85
											</td>
											<td>
												<span class="label label-sm label-danger">
												Canceled </span>
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										<tr>
											<td>
												<a href="#">
												Patrick Walker </a>
											</td>
											<td>
												 6 Aug, 2013
											</td>
											<td>
												 $1239.85
											</td>
											<td>
												<span class="label label-sm label-success">
												Success </span>
											</td>
											<td>
												<a href="#" class="btn default btn-xs green-stripe">
												View </a>
											</td>
										</tr>
										</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- End: life time stats -->
				</div>
				<div class="col-md-6">
					<!-- Begin: life time stats -->
					<div class="portlet box red-sunglo tabbable">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-bar-chart-o"></i>Revenue
							</div>
							<div class="tools">
								<a href="#portlet-config" data-toggle="modal" class="config">
								</a>
								<a href="javascript:;" class="reload">
								</a>
							</div>
						</div>
						<div class="portlet-body">
							<div class="portlet-tabs">
								<ul class="nav nav-tabs" style="margin-right: 50px">
									<li>
										<a href="#portlet_tab2" data-toggle="tab" id="statistics_amounts_tab">
										Amounts </a>
									</li>
									<li class="active">
										<a href="#portlet_tab1" data-toggle="tab">
										Orders </a>
									</li>
								</ul>
								<div class="tab-content">
									<div class="tab-pane active" id="portlet_tab1">
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
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat">
										<span class="label label-success">
										Revenue: </span>
										<h3>$1,234,112.20</h3>
									</div>
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat">
										<span class="label label-info">
										Tax: </span>
										<h3>$134,90.10</h3>
									</div>
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat">
										<span class="label label-danger">
										Shipment: </span>
										<h3>$1,134,90.10</h3>
									</div>
									<div class="col-md-3 col-sm-3 col-xs-6 text-stat">
										<span class="label label-warning">
										Orders: </span>
										<h3>235090</h3>
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
	</div>
	<!-- END CONTENT -->
	<!-- BEGIN QUICK SIDEBAR -->
	<%@ include file="/inc/inc_quick_sidebar.html" %>
	<!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->
<!-- BEGIN FOOTER -->
<%@ include file="/inc/inc_footer.html" %>
<!-- END FOOTER -->

<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="${pageContext.request.contextPath}/assets/global/plugins/flot/jquery.flot.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/assets/global/plugins/flot/jquery.flot.resize.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/assets/global/plugins/flot/jquery.flot.categories.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${pageContext.request.contextPath}/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/assets/admin/layout/scripts/quick-sidebar.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/assets/admin/pages/scripts/ecommerce-index.js"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script>
	jQuery(document).ready(function() {    
    	Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
		QuickSidebar.init() // init quick sidebar
        EcommerceIndex.init();
		
		$('.page-sidebar .ajaxify.start').click() // load the content for the dashboard page.
		
     });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>