var EcommerceOrders = function() {

	var initPickers = function() {
		$('.date-picker').datepicker({
			rtl : Metronic.isRTL(),
			autoclose : true
		});
	}

	var handleOrders = function(table_name) {

		var grid = new Datatable();

		$
				.extend(
						true,
						$.fn.DataTable.TableTools.classes,
						{
							"container" : "btn-group tabletools-dropdown-on-portlet",
							"buttons" : {
								"normal" : "btn btn-sm default",
								"disabled" : "btn btn-sm default disabled"
							},
							"collection" : {
								"container" : "DTTT_dropdown dropdown-menu tabletools-dropdown-menu"
							}
						});

		grid
				.init({
					src : $(table_name),
					onSuccess : function(grid) {
								},
					onError : function(grid) {
								},
					dataTable : { // here you can define a typical datatable
									// settings from
									// http://datatables.net/usage/options

						"lengthMenu" : [ [ 10, 20, 50, 100, -1 ],
								[ 10, 20, 50, 100, "All" ] ],
						"pageLength" : 10,
						"serverSide" : false,
						"scrollY": "400px",
						"deferRender": true,
						"ajax" : {
							"url" : "/orders/getOrderCancelReqList.sc"

						},
						"columnDefs" : [
								// Order No
								{
									"render" : function(data, type, row) {
										return '<a href="/orders/orderDetail.do?docType=0001&entCode='
												+ row['enterPrise']
												+ '&orderNo='
												+ data
												+ '">'
												+ data + '</a>';
									},
									"orderable": true,
									"targets" : 0
								},
								// Original Order Status
								{
									"render" : function(data, type, row) {

										return '<span class="label label-sm label-'
												+ row['org_status_class']
												+ ' ">'
												+ data
												+ '</span><br>';
									},
									"targets" : 8
								},
								// Cancel Req Status
								{
									"render" : function(data, type, row) {

										return '<span class="label label-sm label-'
												+ row['status_class']
												+ ' ">'
												+ data
												+ '</span><br>';
									},
									"targets" : 9
								},
								// Cancel Res Status
								{
									"render" : function(data, type, row) {
										
										if(data == ''){
											return '';
										}
										
										//<button class="btn popovers" data-trigger="hover" data-placement="right" data-content="Popover body goes here! Popover body goes here!" data-original-title="Popover in right">Right</button>
										return '<span class="label label-sm label-'+row["res_status_class"]+' data-toggle="tooltip" title="'+row["cube_msg"]+'">'
												+ '['+data+'] '+row["res_status_text"]
												+ '</span>';
									},
									"targets" : 10
								},
								// Detail View Button
								{
									"render" : function(data, type, row) {
										return '<a href="/orders/orderDetail.do?docType=0001&entCode='
												+ row['enterPrise']
												+ '&orderNo='
												+ row['orderNo']
												+ '" class="btn default btn-xs blue-stripe"><i class="fa fa-search"></i> View</a>';
									},
									"targets" : 11
								}

						],
						"columns" : [
						             
//								{
//									"class" : 'details-control',
//									"orderable" : false,
//									"data" : null,
//									"visible": false,
//									"defaultContent" : '<span class="row-details row-details-close"></span>'
//								},
								{
									"data" : "orderNo"
								},
								{
									"data" : function render(data, type, row) {
										return moment(data["orderDate"],
												moment.ISO_8601).format(
												"YYYY-MM-DD HH:mm");
									}
								},
								{
									"data" : "enterPrise"
								},
								{
									"data" : "sellerOrg"
								},
								{
									"data" : "custName"
								},
								{
									"data" : "custPhone"
								},
//								{
//									"data" : "custEmail"
//								},
								{
									"data" : function render(data, type, row) {
										return '<span class="pull-right">'
												+ data["currency"]
												+ '&nbsp;&nbsp;'
												+ data["totalAmount"]
														.replace(
																/(\d)(?=(\d\d\d)+(?!\d))/g,
																"$1,")
												+ '</span>';
									}
								},
								{
									"data" : "vendorId"
								},
								{
									"data" : "org_status_text"
								}, 
								{
									"data" : "status_text"
								},
								{
									"data" : "res_status_code"
								},
								// Button Column
								{
									"data" : null,
									"orderable" : false
								} ],
						"order" : [ [ 1, "desc" ] ], // set first column as a
														// default sort by asc

						"dom" : "<'row'<'col-md-4 col-sm-12'l><'col-md-8 col-sm-12'<'table-group-actions pull-right'>>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>"
						//"<'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>"

					}
				});

		// handle group actionsubmit button click
		grid.getTableWrapper().on(
				'click',
				'.table-group-action-submit',
				function(e) {
					e.preventDefault();
					var action = $(".table-group-action-input", grid
							.getTableWrapper());
					if (action.val() != "" && grid.getSelectedRowsCount() > 0) {
						grid.setAjaxParam("customActionType", "group_action");
						grid.setAjaxParam("customActionName", action.val());
						grid.setAjaxParam("id", grid.getSelectedRows());
						grid.getDataTable().ajax.reload();
						grid.clearAjaxParams();
					} else if (action.val() == "") {
						Metronic.alert({
							type : 'danger',
							icon : 'warning',
							message : 'Please select an action',
							container : grid.getTableWrapper(),
							place : 'prepend'
						});
					} else if (grid.getSelectedRowsCount() === 0) {
						Metronic.alert({
							type : 'danger',
							icon : 'warning',
							message : 'No record selected',
							container : grid.getTableWrapper(),
							place : 'prepend'
						});
					}
				});

	}
	
	
	// 컬럼별 검색처리
	var filterColumn = function( table_name, filterObj ) {
	    	
		$(table_name).DataTable().column( $(filterObj).parents().index() )
        				.search( $(filterObj).val() )
        				.draw();
	}
	
	
	return {

		// main function to initiate the module
		init : function(table_name) {

			initPickers();
			handleOrders(table_name);
			
			$('select.search-filter, input.search-filter').on( 'keyup change', function (event) {
				filterColumn( table_name, $(this) );
		    } );
			

		}

	};

}();