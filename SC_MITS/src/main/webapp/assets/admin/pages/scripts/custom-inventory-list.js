var InventoryItemList = function () {


    var handleItems = function (table_name) {
    		
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
                "pageLength": 20,
                "serverSide": false, 
                //"scrollY": "400px",
				"deferRender": true,
                "ajax": {
                    "url": "/inventory/invenItemList.sc" ,
                    "timeout": 30000
                	
                },
                "columnDefs": [ 
                               
                               {
                            	   	  "render": function(data, type, row){
                            	          return '<a href="/orders/orderDetail.do?docType=0001&entCode='+row['enterPrise']+'&orderNo='+row['orderNo']+'" class="btn default btn-xs blue-stripe"><i class="fa fa-search"></i> View</a>';
                            	      },
                            	      "targets": 6	// View Button
                               }
                               
                           ],
                "columns": [
                            { "data": "itemID" },
                            { "data": "inventoryOrganizationCode" },
                            { "data": "item.shortDescription" },
                            { "data": "item.description", "orderable":false },
                            { "data": "productClass" },
                            { "data": "unitOfMeasure"},
                            { "data": null, "orderable":false }
                        ],
                "order": [
                          [0, "desc"]
                      ], // set first column as a default sort by asc
                "dom" : "<'row'<'col-md-4 col-sm-12'l><'col-md-8 col-sm-12'<'table-group-actions pull-right'>>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>"
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
    
    // 컬럼별 검색처리
	var filterColumn = function( table_name, filterObj ) {
	    	
		$(table_name).DataTable().column( $(filterObj).parents().index() )
        				.search( $(filterObj).val() )
        				.draw();
	}

    return {

        //main function to initiate the module
        init: function (table_name) {

            handleItems(table_name);
            $('select.form-filter, input.form-filter').on( 'keyup change', function (event) {
            	event.preventDefault();
            	filterColumn( table_name, $(this) );
		    } );         
        }

    };

}();