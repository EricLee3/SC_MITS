var EcommerceIndex = function () {
	
	// Chart Show Tooltip
    function showTooltip(x, y, labelX, labelY, lastLabel) {
        $('<div id="tooltip" class="chart-tooltip">' + (labelY.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')) + lastLabel+'<\/div>').css({
            position: 'absolute',
            display: 'none',
            top: y - 40,
            left: x - 60,
            border: '0px solid #ccc',
            padding: '2px 6px',
            'background-color': '#fff'
        }).appendTo("body").fadeIn(200);
    }
    
    
    var orderCountChart = function (dataSet) {
    	
	    	var lineColors = [ "#f89f9f","#BAD9F5" ];
	    	var seriesDataSet = new Array();
	    	
	    	// 
	    	for( var i=0; i<dataSet.length; i++){
	    		
	    		seriesDataSet[i] = {
					data:dataSet[i].chData.count,
	                label: dataSet[i].chName,
	                lines: {
	                    fill: 0.0,
	                    lineWidth: 1.5
	                },
	                color: lineColors[i]	
	    		}
	    		
	    	}
	    	
	    	var seriesPointSet = new Array();
	    	for( var i=0; i<dataSet.length; i++){
	    		
	    		seriesPointSet[i] = {
	              data: dataSet[i].chData.count,
	              points: {
	                  show: true,
	                  fill: true,
	                  radius: 5,
	                  fillColor: "#f89f9f",
	                  lineWidth: 3
	              },
	              color: '#fff',
	              shadowSize: 0
	    		}
	    		
	    	}
    	
        var plot_statistics = $.plot(
            $("#statistics_2"), 
            seriesDataSet,
            {
            	series: {
                    lines: {
                        show: true,
//                            lineWidth: 2,
//                            fill: true,
//                            fillColor: {
//                                colors: [{
//                                        opacity: 0.5
//                                    }, {
//                                        opacity: 0.1
//                                    }
//                                ]
//                            }
                    },
                    points: {
                        show: true,
                        radius: 3,
                        lineWidth: 1.5
                    },
                    shadowSize: 0
                },
                	
                xaxis: {
                	tickLength: 0,
                    tickDecimals: 0,                        
                    mode: "categories",
                    //min: 0,
                    font: {
                        lineHeight: 15,
                        style: "normal",
                        variant: "small-caps",
                        color: "#6F7B8A"
                    }
                },
                yaxis: {
                    ticks: 5,
                    tickDecimals: 0,
                    tickColor: "#f0f0f0",
                    font: {
                        lineHeight: 15,
                        style: "normal",
                        variant: "small-caps",
                        color: "#6F7B8A"
                    }
                },
                grid: {
                    backgroundColor: {
                        colors: ["#fff", "#fff"]
                    },
                    borderWidth: 1,
                    borderColor: "#f0f0f0",
                    margin: 0,
                    minBorderMargin: 0,
                    labelMargin: 20,
                    hoverable: true,
                    clickable: true,
                    mouseActiveRadius: 6
                },
                legend: {
                    show: true
                }
            }
        );

        var previousPoint = null;

        $("#statistics_2").bind("plothover", function (event, pos, item) {
            $("#x").text(pos.x.toFixed(2));
            $("#y").text(pos.y.toFixed(2));
            if (item) {
                if (previousPoint != item.dataIndex) {
                    previousPoint = item.dataIndex;

                    $("#tooltip").remove();
                    var x = item.datapoint[0].toFixed(2),
                        y = item.datapoint[1].toFixed(2);

                    showTooltip(item.pageX, item.pageY, item.datapoint[0], item.datapoint[1], '건');
                }
            } else {
                $("#tooltip").remove();
                previousPoint = null;
            }
        });

    }

    var totalChart = function (totCount, totAmount) {
    	
	    	var lineColors = [ "#f89f9f","#BAD9F5" ];
	    	var seriesDataSet = new Array();
		seriesDataSet[0] = {
			data:totCount,
            label: "오더건수",
            lines: {
                fill: 0.0,
                lineWidth: 1.5
            },
            color: lineColors[0]	
		};
		seriesDataSet[1] = {
			data:totAmount,
            label: "오더금액",
            lines: {
                fill: 0.0,
                lineWidth: 1.5
            },
            color: lineColors[1]	
		};
    		
    	
        var plot_statistics = $.plot(
            $("#statistics_0"), 
            seriesDataSet,
            {
            	series: {
                    lines: {
                        show: true,
                    },
                    points: {
                        show: true,
                        radius: 3,
                        lineWidth: 1.5
                    },
                    shadowSize: 0
                },
            	
                xaxis: {
                	tickLength: 0,
                    tickDecimals: 0,                        
                    mode: "categories",
                    font: {
                        lineHeight: 15,
                        style: "normal",
                        variant: "small-caps",
                        color: "#6F7B8A"
                    }
                },
                yaxis: {
                    ticks: 5,
                    tickDecimals: 0,
                    tickColor: "#f0f0f0",
                    font: {
                        lineHeight: 15,
                        style: "normal",
                        variant: "small-caps",
                        color: "#6F7B8A"
                    }
                },
                grid: {
                    backgroundColor: {
                        colors: ["#fff", "#fff"]
                    },
                    borderWidth: 1,
                    borderColor: "#f0f0f0",
                    margin: 0,
                    minBorderMargin: 0,
                    labelMargin: 20,
                    hoverable: true,
                    clickable: true,
                    mouseActiveRadius: 6
                },
                legend: {
                    show: true
                }
            }
        );

        var previousPoint = null;

        $("#statistics_0").bind("plothover", function (event, pos, item) {
            $("#x").text(pos.x.toFixed(2));
            $("#y").text(pos.y.toFixed(2));
            if (item) {
                if (previousPoint != item.dataIndex) {
                    previousPoint = item.dataIndex;

                    $("#tooltip").remove();
                    var x = item.datapoint[0].toFixed(2),
                        y = item.datapoint[1].toFixed(2);

                    showTooltip(item.pageX, item.pageY, item.datapoint[0], item.datapoint[1], '');
                }
            } else {
                $("#tooltip").remove();
                previousPoint = null;
            }
        });
	}	

        
	var orderAmountChart = function (dataSet) {
		
		
		var lineColors = ["#f89f9f","#BAD9F5"];
		var seriesDataSet = new Array();
		
		for( var i=0; i<dataSet.length; i++){
			
			seriesDataSet[i] = {
				data:dataSet[i].chData.amount,
	            label: dataSet[i].chName,
	            lines: {
	                fill: 0.0,
	                lineWidth: 1.5
	            },
	            color: lineColors[i]	
			}
			
		}
		
	    var plot_statistics = $.plot(
	        $("#statistics_1"), 
	        seriesDataSet,
	        {
	        	series: {
	                lines: {
	                    show: true,
	                },
	                points: {
	                    show: true,
	                    radius: 3,
	                    lineWidth: 1.5
	                },
	                shadowSize: 0
	            },
	        	
	            xaxis: {
	            	tickLength: 0,
	                tickDecimals: 0,                        
	                mode: "categories",
	                font: {
	                    lineHeight: 15,
	                    style: "normal",
	                    variant: "small-caps",
	                    color: "#6F7B8A"
	                }
	            },
	            yaxis: {
	                ticks: 5,
	                tickDecimals: 0,
	                tickColor: "#f0f0f0",
	                font: {
	                    lineHeight: 15,
	                    style: "normal",
	                    variant: "small-caps",
	                    color: "#6F7B8A"
	                }
	            },
	            grid: {
	                backgroundColor: {
	                    colors: ["#fff", "#fff"]
	                },
	                borderWidth: 1,
	                borderColor: "#f0f0f0",
	                margin: 0,
	                minBorderMargin: 0,
	                labelMargin: 20,
	                hoverable: true,
	                clickable: true,
	                mouseActiveRadius: 6
	            },
	            legend: {
	                show: true
	            }
	        }
	    );
	
	    var previousPoint = null;
	    $("#statistics_1").bind("plothover", function (event, pos, item) {
	        $("#x").text(pos.x.toFixed(2));
	        $("#y").text(pos.y.toFixed(2));
	        if (item) {
	            if (previousPoint != item.dataIndex) {
	                previousPoint = item.dataIndex;
	
	                $("#tooltip").remove();
	                var x = item.datapoint[0].toFixed(2),
	                    y = item.datapoint[1].toFixed(2);
	
	                showTooltip(item.pageX, item.pageY, item.datapoint[0], item.datapoint[1], '');
	            }
	        } else {
	            $("#tooltip").remove();
	            previousPoint = null;
	        }
	    });
        
	};
    
    // Get DashBoard Data Using Ajax - Top Pannel ( 총결제금액, 오더건수, 비용, 환불금액)
    var getOrderOverAll = function(startDate, endDate, term){
    	
    		Metronic.blockUI({
                target: '.dashboard-stat',
                iconOnly: true,
                boxed: false
             });
    	
    		$.ajax({
    			url: '/reports/getOrderOverAll.sc',
    			data: "startDate="+startDate+"&endDate="+endDate+"&term="+term,
    			success:function(jsonData)
    			{
    				
    				// $('#ds_tot_amaunt').html(String.fromCharCode('0x20A9')+jsonData.tot_order_amount.toFixed(2)).digits();
//    				$('#ds_tot_amount').html(jsonData.tot_order_amount.toFixed(2)).digits();
    				$('#ds_tot_amount').html(jsonData.tot_order_amount).digits();
    				$('#ds_tot_orders').html(jsonData.tot_order_count).digits();
//    				$('#ds_tot_charge').html( (jsonData.tot_charge_amount+jsonData.tot_tax_amount+jsonData.tot_discount_amount).toFixed(2)).digits(); // 할인금액은 (-)로 관리됨
    				$('#ds_tot_charge').html( (jsonData.tot_charge_amount+jsonData.tot_tax_amount+jsonData.tot_discount_amount)).digits(); // 할인금액은 (-)로 관리됨
//    				$('#ds_tot_cancel_amt').html(jsonData.tot_cancel_amount.toFixed(2)).digits();
    				$('#ds_tot_cancel_amt').html(jsonData.tot_cancel_amount).digits();
    //				$('#ds_tot_average').html(jsonData.tot_order_avg_amount.toFixed(2)).digits();
    				
    			},
    			complete:function(xhr, status){
    				window.setTimeout(function () {
    	                Metronic.unblockUI('.dashboard-stat');
    	            }, 200);
        		}
    		}); // End Ajax
    	
    	
    }; // End Func getOrderOverAll
    
    
    var initTable_common = function(id, tableData) {
    	
    		var common_colDef = [ 
		                      {
		                   	   "render": function(data, type, row){
		                   		   
		                   		   return '<a href="/orders/orderDetail.do?docType=0001&entCode='+row['enterPrise']+'&orderNo='+row['orderNo']+'" class="btn default btn-xs blue-stripe"><i class="fa fa-search"></i> View</a>';
		                   	   },
		                   	   "targets": 7	// View Button
		                      },
		                      { "visible": false,  "targets": [2] }
		                      
		                  ];
    		var common_cols = [
	                           { "data": "orderNo" },
	                           { "data": function render(data, type)
	                     	  			{
	   		                    	    return moment(data["orderDate"], moment.ISO_8601).format("YYYY-MM-DD HH:mm");
	   		                    	} 
	                           },
	                           { "data": "enterPrise" },
	                           { "data": "sellerOrg" },
	                           { "data": "currency" },
	                           { "data": function render(data, type)
	                 	  				{
	                   					//return '<span class="pull-right">'+data["currency"] + '&nbsp;&nbsp;' + data["totalAmount"]+'</span>';
	                   					return '<span class="pull-right">'+data["totalAmount"].replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,")+'</span>';
	                 	  				} 
	                           },
	                           { "data": function render(data, type)
	   		      	  				{
	   		        						
	                        	   		   var cancelResLabel = "";
			                        	   if(data["res_status_code"] != undefined && data["res_status_code"] != ""){
			                        		   cancelResLabel = '<span class="label label-sm label-danger" data-toggle="tooltip" title="'+data["res_status_text"]+'">결과-'+data["res_status_code"]+'</span> ';
			                   	      }
	                        	   
	                        	   		  return '<span class="label label-sm label-'+data['status_class']+' ">'+data['status_text']+'</span>&nbsp;'+cancelResLabel;
	   		      	  				} 
		   		                },
		   		                { "data": null, "orderable":false }
	                       ];
    	
	    	var new_table = $(id);
	    	
	    	new_table.dataTable({
	        	"paging":   false,
            "ordering": false,
            "info":     false,
	        	"processing": false,
            "serverSide": false,
            "dom":"",
            "data": tableData,
            "columnDefs": common_colDef,
            "columns": common_cols
        });
    	
    }
    
    // Orders Overview - Error List
    var initTable_overviews = function(data) {
    	
    	initTable_common('#table_new_list', data.newList);
    	initTable_common('#table_releaseConfirm_list', data.releaseConfirmList);
    	initTable_common('#table_createShipment_list', data.createShipmentList);
    	initTable_common('#table_shipped_list', data.shippedList);
//    	initTable_common('#table_cancelled_list', data.cancelReqList); // 주문취소요청건
    	initTable_common('#table_cancelled_list', data.cancelList); // 주문취소건
    	initTable_common('#table_pending_list', data.releaseList);
    	
    	// Error List - TODO: 향후 수정
//    	var err_table = $('#table_error_list');
//    	err_table.dataTable({
//        	"paging":   false,
//            "ordering": false,
//            "info":     false,
//        	"processing": false,
//            "serverSide": false,
//            "dom":"",
//            "data": data.errList,
//            "columns": [
//                        { "data": "orderId" },
//                        { "data": "" },
//                        { "data": "" },
//                        { "data": "" },
//                        { "data": function(data, type){
//                 		   			return '<a href="#" class="btn default btn-xs blue-stripe ajaxify"><i class="fa fa-search"></i> View</a>';
//                 	   			  }
//                 	   },
//                    ]
//        });
    };
    
    // Order Overview DataTable Reload 처리
	var reload_table_overview = function(table_id, data){
		
		
		var newTable = $(table_id).dataTable();
		
	    	if(data == null || data == "" || data.length == 0){
	    		
	    		newTable.fnClearTable();
	    		return;
	    	}
				
		newTable.fnClearTable();
		newTable.fnAddData(data);
		newTable.fnDraw();
	};
	
	
	
	// 월별 오더리포트 차트데이타 조회
    var setOrderReportChart = function(startMonth) {
    	
    	$.ajax({
			url: '/reports/getOrderReportByCh.sc',
			data: "startMonth="+startMonth+"&term="+(term+1),
			success:function(jsonData)
			{
				chartData = jsonData;
				
				// 현재 활성환 된 차트갱신
				var chart_index = $('#ptl_bd_orderChart ul > li.active').index();
				
				if( chart_index == 0){
					orderCountChart(chartData.data);
				}else if( chart_index == 1){
					orderAmountChart(chartData.data);
					
				}else if( chart_index == 2){
					totalChart(chartData.tot_cnt_data, chartData.tot_amt_data);
				}
				
				// 4가지 항목 집계데이타 표시 - 총결제금액, 총주문건수, 총비용, 환불금액 
                $('#tot_amount_month h3').html(chartData.tot_amount).digits();
                $('#tot_count_month h3').html(chartData.tot_count).digits();
                $('#tot_charge_month h3').html(chartData.tot_shipping_charge).digits();
                $('#tot_cancel_month h3').html(chartData.tot_cancel_amount).digits();

			}
    	});
    	
    };
    
    // Order Overview DataTable Set
    var setOrderOverviewList = function(mode, ch) {
    	Metronic.blockUI({
    		
            target: '#pt_order_overview',
            imageOnly:true
         });
    	
    	
    	$.ajax({
			url: '/orders/orderOverviewList.sc?ch='+ch,
			success:function(data)
			{
				if(mode == 'reload'){
					reload_table_overview('#table_new_list', data.newList);
					reload_table_overview('#table_releaseConfirm_list', data.releaseConfirmList);
					reload_table_overview('#table_createShipment_list', data.createShipmentList);
					reload_table_overview('#table_shipped_list', data.shippedList);
//					reload_table_overview('#table_cancelled_list', data.cancelReqList); // 주문취소요청건
					reload_table_overview('#table_cancelled_list', data.canceList); // 주문취소건
					reload_table_overview('#table_pending_list', data.releaseList);
					
				}else{
					initTable_overviews(data);
				}
				
				
			},
			complete:function(xhr, status){
				window.setTimeout(function () {
	                Metronic.unblockUI('#pt_order_overview');
	            }, 100);
			}
		}); // End Ajax
        
    }// End Function setOrderOverviewList
	
    return {
    	
        //main function
        init: function () {
        	
        	// 상단 4개지표항목 조회
            getOrderOverAll(moment().format('YYYYMMDD'), moment().format('YYYYMMDD'), 0);
            
            // 차트 지표 조회 (최근 3개월)
            setOrderReportChart(moment().subtract('month', term).format('YYYYMM'));
            
            // 상태별 최근 오더목록 조회 (전체 채널)
    		setOrderOverviewList('','*');
            
            
        	// Chart Tab 이동 이벤트 핸들러
    		$('#statistics_amounts_tab0').on('click', function (e) {
    			$(this).tab("show");
                totalChart(chartData.tot_cnt_data, chartData.tot_amt_data);
            });
        	
    		$('#statistics_amounts_tab1').on('click', function (e) {
    			$(this).tab("show");
    			orderAmountChart(chartData.data);
    			
            });
        	
            $('#statistics_amounts_tab2').on('click', function (e) {
            	$(this).tab("show");
            	orderCountChart(chartData.data);
            });
            
            
            // Chart Reload Button Click Event Handler
    		$("#ptl_tt_orderChart .portlet-title a.reload").click(function(e){
    			
    			 e.preventDefault();	// prevent default event
    			 e.stopPropagation();   // stop event handling here(cancel the default reload handler)
    			 
    			 setOrderReportChart(moment().subtract('month', term).format('YYYYMM'));
    		 });
            
    		
    		 // Order Overview Reload Event Handler
	   		 $("#pt_order_overview .portlet-title .tools a.reload").click(function(e){
	   				
	   			 e.preventDefault();	// prevent default event
	   			 e.stopPropagation();   // stop event handling here(cancel the default reload handler)
	   			 
	   			 setOrderOverviewList('reload', '*');
	   		 });
	   		 
	   		 // Order Overview 'More' Button Click Event Handler
			 $('#btn_more_order').click(function(){
				
				 var tab_index = $('#tab_overview .nav.nav-tabs li[class="active"]').index();
				 
				 var status = "";
				 var mn = ""; //menu_index
				 switch(tab_index) {
				 
					  case 0: status = "1100"; mn="0"; break;
					  case 1: status = "3200"; mn="1"; break;
					  case 2: status = "3350"; mn="2"; break;
					  case 3: status = "3700"; mn="3"; break;
					  case 4: status = "9000"; mn="4"; break;
					  case 5: status = "1300"; mn="5"; break;
				  }
				 
				 $(location).attr('href','/orders/order_list.do?status='+status+'&action=true&mn_d1=3&mn_d2='+mn);
				 
			 });
        },
        
        // 기간별 차트 데이타 조회
        getOrderReport: function(termMon){
        	
        	$('#ptl_tt_orderChart .portlet-title .caption span').html("최근 "+(termMon+1)+"개월");
        	
        	term = termMon;
    		var sMonth = moment().subtract('month', term).format('YYYYMM');
    		setOrderReportChart(sMonth);
    	},
	    
    	// 채널별 오더목록 조회
		getOrderListByCh: function(ch){
			setOrderOverviewList('reload', ch);
		},
	    
	    
	    // DashBoard ToDate ~ FromDate Range Setup
	    initDashboardDaterange: function () {

	    		$('#dashboard-report-range').daterangepicker(
	    			{
	                opens: (Metronic.isRTL() ? 'right' : 'left'),
	                //startDate: moment().subtract('days', 29),
	                startDate: moment(),
	                endDate: moment(),
	                minDate: moment().subtract('month', 3).startOf('month'),
	                maxDate: moment(),
	                dateLimit: {
	                    days: 60
	                },
	                showDropdowns: false,
	                showWeekNumbers: true,
	                timePicker: false,
	                timePickerIncrement: 1,
	                timePicker12Hour: true,
	                ranges: {
	                    'Today': [moment(), moment()],
	                    'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
	                    'Last 7 Days': [moment().subtract('days', 6), moment()],
	                    //'Last 30 Days': [moment().subtract('days', 29), moment()],
	                    'This Month': [moment().startOf('month'), moment().endOf('month')],
	                    'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')],
	                },
	                buttonClasses: ['btn btn-sm'],
	                applyClass: ' blue',
	                cancelClass: 'default',
	                //format: 'MM/DD/YYYY',
	                format: 'YYYY-MM-DD',
	                separator: ' to ',
	                locale: {
	                    applyLabel: 'Apply',
	                    fromLabel: 'From',
	                    toLabel: 'To',
	                    //customRangeLabel: 'Custom Range',
	                    daysOfWeek: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
	                    //monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
	                    monthNames: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
	                    firstDay: 1
	                }
	    			},
	            function (start, end, lable) {
    				
//	                $('#dashboard-report-range span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
	                $('#dashboard-report-range span').html(start.format('YYYY-MM-DD') + ' - ' + end.format('YYYY-MM-DD'));
	                
	                var termDate = Math.floor( (end-start)/1000/60/60/24 );
	                getOrderOverAll(start.format('YYYYMMDD'), end.format('YYYYMMDD'), termDate);
	            }
	    		);
	
	        //$('#dashboard-report-range span').html(moment().subtract('days', 29).format('MMMM D, YYYY') + ' - ' + moment().format('MMMM D, YYYY'));
	        $('#dashboard-report-range span').html(moment().format('YYYY-MM-DD') + ' - ' + moment().format('YYYY-MM-DD'));
	        $('#dashboard-report-range').show();
	        
	        $('.ranges li:last').hide();	// Custom Range Hidden
	        $('.ranges .range_inputs').hide() // Date Input Hidden
	        
	        
	        
	    } // End Func initDashboardDaterange

    }; // End Init

}();