var EcommerceOrders = function () {

    var initPickers = function () {
        //init date pickers
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
                // execute some code after table records loaded
            },
            onError: function (grid) {
                // execute some code on network or other general error  
            },
            dataTable: { // here you can define a typical datatable settings from http://datatables.net/usage/options 
                "lengthMenu": [
                    [10, 20, 50, 100, 150, -1],
                    [10, 20, 50, 100, 150, "All"] // change per page values here
                ],
                "pageLength": 10, // default record count per page
                "serverSide": true, 
                "ajax": {
                    "url": "/orders/orderList.sc"  // ajax source
                	
                },
                "fnRowCallback"  : function(nRow,aData,iDisplayIndex) {
                	//$('td:eq(8)', nRow).addClass( "pull-right");
                },
                "columnDefs": [ 
                               {
                                   "render": function ( data, type, row ) {
                                	   
                                	   //console.debug(row);
                                       return '<span class="label label-sm label-'+row['status_class']+' ">'+data+'</span>';
                                   },
                                   "targets": 9  // Status Icon
                               },
                               {
                            	   "render": function(data, type, row){
                            		   
                            		   return '<a href="/orders/orderDetail.do?docType=0001&entCode='+row['enterPrise']+'&orderNo='+row['orderNo']+'" class="btn default btn-xs blue-stripe"><i class="fa fa-search"></i> View</a>';
                            	   },
                            	   "targets": 10	// View Button
                               },
                               { "visible": false,  "targets": [] }
                               
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
                            { "data": "orderNo" },
                            { 
                        	  "data": function render(data, type, row)
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
                        					return '<span class="pull-right">'+data["currency"] + '&nbsp;&nbsp;' + data["totalAmount"]+'</span>';
                      	  				}, "orderable":false },
                            { "data": "status_text", "orderable":false },
                            { "data": null, "orderable":false }
                        ],
                "order": [
                          [2, "desc"]
                      ], // set first column as a default sort by asc
                
                //"dom": "<'row'<'col-md-8 col-sm-12'pli><'col-md-4 col-sm-12'<'table-group-actions pull-right'>>r><'table-scrollable't><'row'<'col-md-8 col-sm-12'pli><'col-md-4 col-sm-12'>>", // datatable layout
                "dom":"<'row'<'col-md-4 col-sm-12'l><'col-md-8 col-sm-12'<'table-group-actions pull-right'>>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",
                
                
//                "tableTools": {
//                    "sSwfPath": "../../assets/global/plugins/datatables/extensions/TableTools/swf/copy_csv_xls_pdf.swf",
//                    "aButtons": [
//     	                        "copy",
//     	                        {
//     	                        	
//     	                        	"sExtends": "print",
//     		                        "sButtonText": "Print",
//     		                        "sInfo": 'Please press "CTR+P" to print or "ESC" to quit',
//     		                        "sMessage": "Generated by DataTables"
//     	                        	
//     	                        },
//     	                        {
//     	                            "sExtends":    "collection",
//     	                            "sButtonText": "Save",
//     	                            "aButtons":    [ "csv", "xls", "pdf" ]
//     	                        }
//     	                    ]
//                    "aButtons": [{
//                        "sExtends": "pdf",
//                        "sButtonText": "PDF"
//                    }, {
//                        "sExtends": "csv",
//                        "sButtonText": "CSV"
//                    }, {
//                        "sExtends": "xls",
//                        "sButtonText": "Excel"
//                    }, {
//                        "sExtends": "print",
//                        "sButtonText": "Print",
//                        "sInfo": 'Please press "CTRL+P" to print or "ESC" to quit',
//                        "sMessage": "Generated by DataTables"
//                    }, {
//                        "sExtends": "copy",
//                        "sButtonText": "Copy"
//                    }]
//                }
                
                
            }
        });
        
        /* Formatting function for row details */
        function fnFormatDetails(oTable, nTr) {
            var aData = oTable.fnGetData(nTr);
            var sOut = '<table width="00%">';
            
            
//            sOut += '<tr><td colspan="18">&nbsp;</td>';
//            sOut += '<td>Payment Type:</td><td >' + aData['paymentType'] + '</td>';
//        	sOut += '<td>Total Amount:</td><td class="pull-right">' + aData['currency'] +' '+ aData['totalAmount'] + '</td>';
//            sOut += "</tr>";
            
            
            for(var i=0; i<aData['lineList'].length; i++){
            	
            	/*
            	 * orderLineMap.put("qty", qty);
				orderLineMap.put("lineTatal", lineTatal);
				orderLineMap.put("UnitPrice", UnitPrice);
				orderLineMap.put("lineShipCharge", lineShipCharge);
				orderLineMap.put("lineDisount", -lineDisountCharge);
				orderLineMap.put("lineTax", lineTax);
            	 * 
            	 */
            	
            	sOut += '<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>';
            	sOut += '<td>' + aData['lineList'][i]['PrimeLineNo'] + '</td>';
            	sOut += '<td>ItemID:</td><td>' + aData['lineList'][i]['itemId'] + '</td>';
            	sOut += '<td></td><td>' + aData['lineList'][i]['itemShortDesc'] + '</td>';
            	sOut += '<td>UnitPrice:</td><td>' + aData['lineList'][i]['UnitPrice'] + '</td>';
            	sOut += '<td>Qty:</td><td>' + aData['lineList'][i]['qty'] + '</td>';
            	sOut += '<td>Status:</td><td>'+aData['lineList'][i]['status_text']+'</td>';
            	sOut += '<td>&nbsp;&nbsp;&nbsp;</td>';
            	sOut += '<td>Charge:</td><td>' + aData['lineList'][i]['lineShipCharge'] + '</td>';
            	sOut += '<td>Tax:</td><td>' + aData['lineList'][i]['lineTax'] + '</td>';
            	sOut += '<td>Discount:</td><td>' + aData['lineList'][i]['lineDisount'] + '</td>';
            	sOut += '<td>&nbsp;&nbsp;&nbsp;</td>';
            	sOut += '<td>LineTotal:</td><td class="pull-right">' + aData['lineList'][i]['lineTotal'] + '</td>';
            	sOut += "</tr>";
            }
            
            sOut += '</table>';

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
                $(this).addClass("row-details-close").removeClass("row-details-open");
                table.fnClose(nTr);
            } else {
                /* Open this row */
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