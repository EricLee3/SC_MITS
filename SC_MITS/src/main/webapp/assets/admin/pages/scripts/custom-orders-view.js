var OrderDetailView = function () {

	
    var handleInvoices = function () {

        var grid = new Datatable();

        grid.init({
            src: $("#datatable_invoices"),
            onSuccess: function (grid) {
                // execute some code after table records loaded
            },
            onError: function (grid) {
                // execute some code on network or other general error  
            },
            dataTable: { // here you can define a typical datatable settings from http://datatables.net/usage/options 
                "lengthMenu": [
                    [20, 50, 100, 150, -1],
                    [20, 50, 100, 150, "All"] // change per page values here
                ],
                "pageLength": 20, // default record count per page
                "ajax": {
                    "url": "demo/ecommerce_order_invoices.php", // ajax source
                },
                "order": [
                    [1, "asc"]
                ] // set first column as a default sort by asc
            }
        });

        // handle group actionsubmit button click
        grid.getTableWrapper().on('click', '.table-group-action-submit', function (e) {
            e.preventDefault();
            var action = $(".table-group-action-input", grid.getTableWrapper());
            if (action.val() != "" && grid.getSelectedRowsCount() > 0) {
                grid.setAjaxParam("customActionType", "group_action");
                grid.setAjaxParam("customActionName", action.val());
                grid.setAjaxParam("id", grid.getSelectedRows());
                grid.getDataTable().ajax.reload();
                grid.clearAjaxParams();
            } else if (action.val() == "") {
                Metronic.alert({
                    type: 'danger',
                    icon: 'warning',
                    message: 'Please select an action',
                    container: grid.getTableWrapper(),
                    place: 'prepend'
                });
            } else if (grid.getSelectedRowsCount() === 0) {
                Metronic.alert({
                    type: 'danger',
                    icon: 'warning',
                    message: 'No record selected',
                    container: grid.getTableWrapper(),
                    place: 'prepend'
                });
            }
        });
    }

    var handleNotes = function () {

        var grid = new Datatable();
        
        grid.init({
            src: $("#datatable_credit_memos"),
            
            dataTable: { // here you can define a typical datatable settings from http://datatables.net/usage/options 
                
            	"serverSide": false, 
                "columnDefs": [{ // define columns sorting options(by default all columns are sortable extept the first checkbox column)
                    'orderable': true,
                    'targets': [0]
                }],
                "order": [
                    [0, "asc"]
                ] // set first column as a default sort by asc
            }
        });

    }

    var handleShipment = function () {

        var grid = new Datatable();

        grid.init({
            src: $("#datatable_shipment"),
            onSuccess: function (grid) {
                // execute some code after table records loaded
            },
            onError: function (grid) {
                // execute some code on network or other general error  
            },
            dataTable: { // here you can define a typical datatable settings from http://datatables.net/usage/options 
                "lengthMenu": [
                    [20, 50, 100, 150, -1],
                    [20, 50, 100, 150, "All"] // change per page values here
                ],
                "pageLength": 10, // default record count per page
                "ajax": {
                    "url": "demo/ecommerce_order_shipment.php", // ajax source
                },
                "columnDefs": [{ // define columns sorting options(by default all columns are sortable extept the first checkbox column)
                    'orderable': true,
                    'targets': [0]
                }],
                "order": [
                    [0, "asc"]
                ] // set first column as a default sort by asc
            }
        });
    }

    var handleHistory = function () {

        var grid = new Datatable();

        grid.init({
            src: $("#datatable_history"),
            onSuccess: function (grid) {
                // execute some code after table records loaded
            },
            onError: function (grid) {
                // execute some code on network or other general error  
            },
            dataTable: { // here you can define a typical datatable settings from http://datatables.net/usage/options 
                "lengthMenu": [
                    [20, 50, 100, 150, -1],
                    [20, 50, 100, 150, "All"] // change per page values here
                ],
                "pageLength": 20, // default record count per page
                "ajax": {
                    "url": "demo/ecommerce_order_history.php", // ajax source
                },
                "columnDefs": [{ // define columns sorting options(by default all columns are sortable extept the first checkbox column)
                    'orderable': true,
                    'targets': [0]
                }],
                "order": [
                    [0, "asc"]
                ] // set first column as a default sort by asc
            }
        });

        // handle group actionsubmit button click
        grid.getTableWrapper().on('click', '.table-group-action-submit', function (e) {
            e.preventDefault();
            var action = $(".table-group-action-input", grid.getTableWrapper());
            if (action.val() != "" && grid.getSelectedRowsCount() > 0) {
                grid.setAjaxParam("customActionType", "group_action");
                grid.setAjaxParam("customActionName", action.val());
                grid.setAjaxParam("id", grid.getSelectedRows());
                grid.getDataTable().ajax.reload();
                grid.clearAjaxParams();
            } else if (action.val() == "") {
                Metronic.alert({
                    type: 'danger',
                    icon: 'warning',
                    message: 'Please select an action',
                    container: grid.getTableWrapper(),
                    place: 'prepend'
                });
            } else if (grid.getSelectedRowsCount() === 0) {
                Metronic.alert({
                    type: 'danger',
                    icon: 'warning',
                    message: 'No record selected',
                    container: grid.getTableWrapper(),
                    place: 'prepend'
                });
            }
        });
    }

    var handleDatePickers = function () {

        if (jQuery().datepicker) {
            $('.date-picker').datepicker({
                rtl: Metronic.isRTL(),
                orientation: "left",
                autoclose: true
            });
            //$('body').removeClass("modal-open"); // fix bug when inline picker is used in modal
        }
        
        // handle input group button click
        $('.date-picker').parent('.input-group').on('click', '.input-group-btn .day', function(e){
            e.preventDefault();
            $(this).parent('.input-group').find('.date-picker').datepicker('showWidget');
        });
    }
    
    var handleClockfaceTimePickers = function () {

        if (!jQuery().clockface) {
            return;
        }


        $('#contact_clockface').clockface({
            format: 'HH:mm',
            trigger: 'manual'
        });

        $('#contact_clockface_toggle').click(function (e) {
            e.stopPropagation();
            $('#contact_clockface').clockface('toggle');
        });

    }

    var handleTimePickers = function () {

    	if (jQuery().timepicker) {
            $('.timepicker-default').timepicker({
                autoclose: true,
                showSeconds: true,
                minuteStep: 1
            });

            $('.timepicker-no-seconds').timepicker({
                autoclose: true,
                minuteStep: 5
            });

            $('.timepicker-24').timepicker({
                autoclose: true,
                minuteStep: 5,
                showSeconds: false,
                showMeridian: false
            });
            
            // handle input group button click
            $('.timepicker').parent('.input-group').on('click', '.input-group-btn .time', function(e){
            	e.preventDefault();
                $(this).parent('.input-group').find('.timepicker').timepicker('showWidget');
            });
        }
    }
    
    
    var ajaxCallApi = function(eventObj, callUrl, formId){
    	
    	 Metronic.blockUI({
            boxed:true
         });
    	
    	$.ajax({
			url: callUrl,
			data: $('#'+formId).serialize(),
			success:function(data)
			{
				
				if(data.success == 'Y'){
					alert(data.outputMsg);
					pageReload();
					
				}else{
					alert(data.errorMsg);
				}
				
			},
//			beforeSend:function(xhr, status){
//				Metronic.blockUI({
//	                boxed:true
//	             });
//        	},
			complete:function(xhr, status){
				Metronic.unblockUI();
        	}
    	});
    }
    

    return {

        //main function to initiate the module
        init: function () {
        	
        	handleDatePickers();
        	handleTimePickers();
        	handleClockfaceTimePickers();
        	
            /*
            handleNotes();
            handleInvoices();
            handleShipment();
            handleHistory();
            */
        	
        	
        // Release Order (Schedule and Release)
        $('#tool_release').click(function(e){
        	
        	if($(this).text() == '출고의뢰'){
        		confirmMsg = "부분주문취소 후 주문상태가 부분재고부족 -> 출고의뢰로 변경된 경우에만 사용하는 기능입니다. \n해당 주문을 출고의뢰 하시겠습니까? ";
        	}else{
        		confirmMsg = "해당 주문을 다시 주문확정 처리하시겠습니까?\n상품의 재고가 확보된 경우 자동으로 출고의뢰 상태로 변경되고 Cube로 출고의뢰가 됩니다. ";
        	}
        	
        	if( confirm(confirmMsg)){
        		e.preventDefault();
        		ajaxCallApi(this, '/orders/scheduleOrder.sc', 'form_action');
        	}
        });
        
            
        // Cancel Order 
        $('#btn_cancel').click(function(e){
        	//if( confirm("Are you sure cancel this order?")){
        		e.preventDefault();
        		
        		$("#form_cancel input[name='cancel_type']").val("order");
        		$("#form_cancel input[name='line_keys']").val("");
        		
        		ajaxCallApi(this, '/orders/cancelOrder.sc', 'form_cancel');
        		
        		var $modal = $('#md_cancel_order');
   		    $modal.modal("hide");
        	//}
        }); 
        
        
        // Cancel Order Request
        $('#tool_cancel_req').click(function(e){
        	if( confirm("해당 주문을 Cube로 주문취소요청 하시겠습니까?")){
        		e.preventDefault();
        		
        		$("#form_cancel input[name='cancel_type']").val("order_req");
        		$("#form_cancel input[name='line_keys']").val("");
        		
        		ajaxCallApi(this, '/orders/cancelOrderReq.sc', 'form_cancel');
        		
        	}
        }); 
        
            
        	
            // Event Handler - Save Note
        	$('#btn_save_note').click(function(event){
    			
    			$.ajax({
    				url: '/orders/addNotes.sc',
    				data: $('#form_save_note').serialize(),
    				success:function(data)
    				{
    					
    					if(data.success == 'Y'){
    						alert(data.outputMsg);
    						pageReload();
    						
    					}else{
    						alert(data.errorMsg);
    					}
    				}
    			})
    			
    			event.preventDefault();
    			
    		});
        	
        } // End init

    }; // End return

}();