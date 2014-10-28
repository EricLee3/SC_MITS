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
				<li class="sidebar-toggler-wrapper">
				    <!--
				     BEGIN SIDEBAR TOGGLER BUTTON 
				    -->
				    <div class="sidebar-toggler">
				    </div>
				    <!--
				     END SIDEBAR TOGGLER BUTTON 
				    -->
				</li>
				<li>&nbsp;</li>
				<!-- DOC: To remove the search box from the sidebar you just need to completely remove the below "sidebar-search-wrapper" LI element -->
				<!-- <li class="sidebar-search-wrapper">
					BEGIN RESPONSIVE QUICK SEARCH FORM
					DOC: Apply "sidebar-search-bordered" class the below search form to have bordered search box
					DOC: Apply "sidebar-search-bordered sidebar-search-solid" class the below search form to have bordered & solid search box
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
					END RESPONSIVE QUICK SEARCH FORM
				</li> -->
				
				<!-- Menu DashBoard -->
				<li class="start" id="menu_home">
					<a href="/index.do">
					<i class="icon-home"></i>
					<span class="title">
					Dashboard </span>
					<span class="selected">
					</span>
					</a>
				</li>
				
				<!-- Menu Order Management -->
				<li id="menu_order">
					<a href="javascript:;">
					<i class="icon-basket"></i>
					<span class="title">
					오더관리 </span>
					<span class="arrow">
					</span>
					</a>
					<ul class="sub-menu">
						<li>
							<a href="/orders/order_list.do?action=true&status=A&mn=0">
							<i class="fa fa-list"></i>
							전체오더 조회</a>
						</li>
						<li>
							<a href="/orders/order_list.do?action=true&status=1100&mn=1">
							<i class="fa fa-truck"></i>
							주문생성 목록</a>
						</li>
						<li>
							<a href="/orders/order_list.do?action=true&status=3200&mn=2">
							<i class="fa fa-truck"></i>
							출고의뢰 목록</a>
						</li>
						<li>
							<a href="/orders/order_list.do?action=true&status=3350&mn=3">
							<i class="fa fa-truck"></i>
							출고준비 목록</a>
						</li>
						<li>
							<a href="/orders/order_list.do?action=true&status=3700&mn=4">
							<i class="fa fa-truck"></i>
							출고완료 목록</a>
						</li>
						<!-- <li>
							<a href="/orders/order_list.do?action=true&status=9002&mn=6">
							<i class="fa fa-undo"></i>
							주문취소요청 목록</a>
						</li> -->
						<li>
							<a href="/orders/order_list.do?action=true&status=9000&mn=5">
							<i class="fa fa-times-circle"></i>
							주문취소 목록</a>
						</li>
						<li>
							<a href="/orders/order_list.do?action=true&status=1300&mn=6">
							<i class="fa fa-ban"></i>
							미 출고의뢰건 목록</a>
						</li>
					</ul>
				</li>
				<!-- <li class="last">
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
				</li> -->
				<c:if test="${ S_LOGIN_ID == 'admin' }">
				<li class="last" id="menu_system">
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
							<a href="/system/getRedisDataListByCh.do?entCode=KOLOR&sellCode=ASPB&mn=0">
							<i class="fa fa-desktop"></i>
							연동데이타 모니터링 </a>
						</li>
					</ul>
				</li>
				</c:if>
			</ul>
			<!-- END SIDEBAR MENU1 -->
		</div>
	</div>
<!-- BEGIN SIDEBAR2 -->