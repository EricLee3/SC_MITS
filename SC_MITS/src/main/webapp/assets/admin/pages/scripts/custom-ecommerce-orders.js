var EcommerceOrders = function () {

    var initPickers = function () {
        $('.date-picker').datepicker({
            rtl: Metronic.isRTL(),
            autoclose: true
        });
    }

    var handleOrders = function (table_name) {
    	
        var grid = new Datatable();
        
        $.extend(true, $.fn.DataTable.TableTools.classes, {
            "container": "btn-group tabletools-dropdown-on-portlet",
            "buttons": {
                "normal": "btn btn-sm default",
                "disabled": "btn btn-sm default disabled"
            },
            "collection": {
                "container": "DTTT_dropdown dropdown-menu tabletools-dropdown-menu"
            }
        });

        grid.init({
            src: $(table_name),
            onSuccess: function (grid) {
            },
            onError: function (grid) {
            },
            dataTable: { // here you can define a typical datatable settings from http://datatables.net/usage/options 
                
            		"lengthMenu": [
                    [10, 20, 50, 100, -1],
                    [10, 20, 50, 100, "All"]
                ],
                "pageLength": 10,
                "serverSide": true, 
                "ajax": {
                    "url": "/orders/orderList.sc" 
                	
                },
                "columnDefs": [ 
                               {
                            	   	  "render": function(data, type, row){
                         	          return '<a href="/orders/orderDetail.do?docType=0001&entCode='+row['enterPrise']+'&orderNo='+data+'">'+data+'</a>';
                         	      },
                         	      "targets": 1	// Order#
                               },
                               {
                                   "render": function ( data, type, row ) {
//                                	   		  var cacelReqLabel = "";
//                                	   		  var cacelResLabel = "";
//	                                	      if(row['cancelReq'] == 'Y'){
//	                                	    	  		cacelReqLabel = '<span class="label label-sm label-danger">취소요청</span> '
//	                                	      }
//	                                	      if(row['cancelRes'] == 'Y'){
//	                                	    	  		cacelResLabel = '<span class="label label-sm label-danger" data-toggle="tooltip" title="'+row["cancelRes_text"]+'">결과-'+row['cancelRes_code']+'</span> ';
//	                                	      }
	                                	      
	                                	      return '<span class="label label-sm label-'+row['status_class']+' ">'+data+'</span><br>';
                                   },
                                   "targets": 9  // Status Icon
                               },
                               {
                            	   	  "render": function(data, type, row){
                            	          return '<a href="/orders/orderDetail.do?docType=0001&entCode='+row['enterPrise']+'&orderNo='+row['orderNo']+'" class="btn default btn-xs blue-stripe"><i class="fa fa-search"></i> View</a>';
                            	      },
                            	      "targets": 11	// View Button
                               },
                               { "type": "decimal", "class": "right", "targets": [ 8 ] }
                               
                           ],
                "columns": [
//                            { 
//                            	"data": function render(data, type, row)
//		                            	{
//		                            		return '<input type="checkbox" name="orderNo[]" value=""+orderNo+"">';
//		                            	},
//                              "orderable":false,
//                            },
                            { 
	                            "class":          'details-control',
	                            "orderable":      false,
	                            "data":           null,
	                            "defaultContent": '<span class="row-details row-details-close"></span>'
	                        },
                            { "data": "orderNo"},
                            { "data": function render(data, type, row)
                            	  {
                            		return moment(data["orderDate"], moment.ISO_8601).format("YYYY-MM-DD HH:mm");
                            	  }
                            },
                            { "data": "enterPrise" },
                            { "data": "sellerOrg" },
                            { "data": "billName", "orderable":false},
                            { "data": "phone", "orderable":false },
                            { "data": "emailId", "orderable":false },
//                            { "data": "paymentType", "orderable":false },
                            { "data": function render(data, type, row)
                      	  				{
                        						return '<span class="pull-right">'+data["currency"] + '&nbsp;&nbsp;' + data["totalAmount"].replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,")+'</span>';
                      	  				}, "orderable":false },
                            { "data": "status_text", "orderable":false },
                            { "data": "vendor_id", "orderable":false },
                            { "data": null, "orderable":false }
                        ],
                "order": [
                          [2, "desc"]
                      ], // set first column as a default sort by asc
                
                //"dom": "<'row'<'col-md-8 col-sm-12'pli><'col-md-4 col-sm-12'<'table-group-actions pull-right'>>r><'table-scrollable't><'row'<'col-md-8 col-sm-12'pli><'col-md-4 col-sm-12'>>", // datatable layout
                "dom":"<'row'<'col-md-4 col-sm-12'l><'col-md-8 col-sm-12'<'table-group-actions pull-right'>>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",
                
            }
        });
        
        /* Formatting function for row details */
        function fnFormatDetails(oTable, nTr) {
            var aData = oTable.fnGetData(nTr);
            var sOut = '<div>';
            	    sOut += '<table class="table" width="80%">';
            
            
            for(var i=0; i<aData['lineList'].length; i++){
            
            	
	            	sOut += '<tr><td width="2%"></td>';
	            	sOut += '<td width="3%">' + aData['lineList'][i]['PrimeLineNo'] + '</td>';
	            	sOut += '<td width="40%"><span class="label label-sm label-default"> 상품 </span>&nbsp;&nbsp;[' + aData['lineList'][i]['itemId'] +'] '+aData['lineList'][i]['itemShortDesc']+'</td>';
	            	sOut += '<td width="6%"><span class="label label-sm label-default"> 수량 </span>&nbsp;&nbsp;' + aData['lineList'][i]['qty'] + '</td>';
	            	sOut += '<td width="10%"><span class="label label-sm label-default"> 개별판매가격 </span>&nbsp;&nbsp;' + aData['lineList'][i]['UnitPrice'] + '</td>';
//	            	sOut += '<td width="10%"><span class="label label-sm label-default"> 배송비 </span>&nbsp;&nbsp;' + aData['lineList'][i]['lineShipCharge'] + '</td>';
//	            	sOut += '<td>Tax:</td><td>' + aData['lineList'][i]['lineTax'] + '</td>';
//	            	sOut += '<td width="10%"><span class="label label-sm label-default"> 할인금액 </span>&nbsp;&nbsp;' + aData['lineList'][i]['lineDisount'] + '</td>';
	            	sOut += '<td width="10%"><span class="label label-sm label-default"> 라인합계 </span>&nbsp;&nbsp;' + aData['lineList'][i]['lineTotal'] + '</td>';
	            	sOut += '<td><span class="label label-sm label-default"> 주문상태 </span>&nbsp;&nbsp;'+aData['lineList'][i]['status_text']+'</td>';
	            	sOut += "</tr>";
            }
            
            sOut += '</table>';
            sOut += '</div>';

            return sOut;
        }
        
        var tableWrapper = $(table_name+'_wrapper'); // datatable creates the table wrapper by adding with id {your_table_jd}_wrapper
        tableWrapper.find('.dataTables_length select').select2(); // initialize select2 dropdown
        
        var table = $(table_name).dataTable();
        
        /* Add event listener for opening and closing details
         * Note that the indicator for showing which row is open is not controlled by DataTables,
         * rather it is done here
         */
        table.on('click', ' tbody td .row-details', function () {
            var nTr = $(this).parents('tr')[0];
            if (table.fnIsOpen(nTr)) {
                /* This row is already open - close it */
            		$('#row_all_exp').addClass("row-details-close").removeClass("row-details-open");
                $(this).addClass("row-details-close").removeClass("row-details-open");
                
                table.fnClose(nTr);
            } else {
                /* Open this row */
            		$('#row_all_exp').addClass("row-details-open").removeClass("row-details-close");
                $(this).addClass("row-details-open").removeClass("row-details-close");
                table.fnOpen(nTr, fnFormatDetails(table, nTr), 'details');
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

    return {

        //main function to initiate the module
        init: function (table_name) {

            initPickers();
            handleOrders(table_name);
                       
        }

    };

}();