<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- BEGIN SIDEBAR1 -->
	<div class="page-sidebar-wrapper">
		<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
		<!-- DOC: Change data-auto-speed="200" to adjust the sub menu slide up/down speed -->		
		<div class="page-sidebar navbar-collapse collapse">
			<!-- BEGIN SIDEBAR MENU1 -->
			<ul class="page-sidebar-menu" data-slide-speed="200" data-auto-scroll="true" data-auto-scroll="true" data-slide-speed="200">
				<!-- DOC: To remove the search box from the sidebar you just need to completely remove the below "sidebar-search-wrapper" LI element -->
				<li class="sidebar-search-wrapper">
					<!-- BEGIN RESPONSIVE QUICK SEARCH FORM -->
					<!-- DOC: Apply "sidebar-search-bordered" class the below search form to have bordered search box -->
					<!-- DOC: Apply "sidebar-search-bordered sidebar-search-solid" class the below search form to have bordered & solid search box -->
					<form class="sidebar-search sidebar-search-bordered" action="extra_search.html" method="POST">
						<a href="javascript:;" class="remove">
						<i class="icon-close"></i>
						</a>
						<div class="input-group">
							<input type="text" class="form-control" placeholder="Search...">
							<span class="input-group-btn">
							<a href="javascript:;" class="btn submit"><i class="icon-magnifier"></i></a>
							</span>
						</div>
					</form>
					<!-- END RESPONSIVE QUICK SEARCH FORM -->
				</li>
				
				<!-- Menu DashBoard -->
				<li class="start active opne">
					<a href="/index.do">
					<i class="icon-home"></i>
					<span class="title">
					Dashboard </span>
					<span class="selected">
					</span>
					</a>
				</li>
				
				<!-- Menu Order Management -->
				<li>
					<a href="javascript:;">
					<i class="icon-basket"></i>
					<span class="title">
					오더관리 </span>
					<span class="arrow">
					</span>
					</a>
					<ul class="sub-menu">
						<li>
							<a href="/orders/order_list.do">
							<i class="icon-basket"></i>
							오더 조회</a>
						</li>
						<li>
							<a href="/admin/orders/order_list.html?param1=aaa">
							<i class="icon-plane"></i>
							출고완료 목록</a>
						</li>
						<li>
							<a href="/admin/orders/order_list.html?param1=aaa">
							<i class="icon-action-undo"></i>
							재고부족 목록</a>
						</li>
						<li>
							<a href="/admin/orders/order_list.html?param1=aaa">
							<i class="icon-action-undo"></i>
							주문취소 목록</a>
						</li>
					</ul>
				</li>
				<li class="last">
					<a href="javascript:;">
					<i class="fa fa-cogs"></i>
					<span class="title">
					반품오더 관리 </span>
					<span class="selected">
					</span>
					<span class="arrow">
					</span>
					</a>
					<ul class="sub-menu">
						<li>
							<a href="/admin/returns/return_order_regist.html">
							반품오더 접수 </a>
						</li>
						<li>
							<a href="/admin/returns/return_order_list.html">
							반품오더 조회 </a>
						</li>
					</ul>
				</li>
				<li class="last">
					<a href="javascript:;">
					<i class="fa fa-cogs"></i>
					<span class="title">
					시스템 관리 </span>
					<span class="selected">
					</span>
					<span class="arrow">
					</span>
					</a>
					<ul class="sub-menu">
						<li>
							<a href="/system/getRedisDataList.do">
							연동데이타 모니터링 </a>
						</li>
					</ul>
				</li>
			</ul>
			<!-- END SIDEBAR MENU1 -->
		</div>
	</div>
<!-- BEGIN SIDEBAR2 -->