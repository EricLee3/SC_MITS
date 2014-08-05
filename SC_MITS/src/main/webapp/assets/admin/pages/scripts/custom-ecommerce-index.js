var EcommerceIndex = function () {

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
    
    
    function gd(year, month, day) {
        return new Date(year, month - 1, day).getTime();
    }

    
    var orderCountChart = function (data1, data2) {
    	

            var plot_statistics = $.plot(
                $("#statistics_1"), 
                [
                    {
                        data:data1,
                        label: "AspenBay",
                        lines: {
                            fill: 0.6,
                            lineWidth: 0
                        },
                        color: ['#f89f9f']
                    },
                    {
                        data:data2,
                        label: "Outro",
                        lines: {
                            fill: 0.6,
                            lineWidth: 0
                        },
                        color: ['#BAD9F5']
                    },
                    
                    {
                        data: data1,
                        points: {
                            show: true,
                            fill: true,
                            radius: 5,
                            fillColor: "#f89f9f",
                            lineWidth: 3
                        },
                        color: '#fff',
                        shadowSize: 0
                    },
                    {
                        data: data2,
                        points: {
                            show: true,
                            fill: true,
                            radius: 5,
                            fillColor: "#BAD9F5",
                            lineWidth: 3
                        },
                        color: '#fff',
                        shadowSize: 0
                    }
                   
                ], 
                
                
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
//                            show: true,
//                            radius: 3,
//                            lineWidth: 1
                        },
                        shadowSize: 2
                    },
                	
                    xaxis: {
                    	tickLength: 0,
                        tickDecimals: 0,                        
                        mode: "categories",
                        min: 2,
                        font: {
                            lineHeight: 15,
                            style: "normal",
                            variant: "small-caps",
                            color: "#6F7B8A"
                        }
                    },
                    yaxis: {
                        ticks: 3,
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

                        showTooltip(item.pageX, item.pageY, item.datapoint[0], item.datapoint[1], '건');
                    }
                } else {
                    $("#tooltip").remove();
                    previousPoint = null;
                }
            });

    }

    var orderAmountChart = function (data1, data2) {
    	

        var plot_statistics = $.plot(
            $("#statistics_2"), 
            [
                {
                    data:data1,
                    label: "AspenBay",
                    lines: {
                        fill: 0.6,
                        lineWidth: 0
                    },
                    color: ['#f89f9f']
                },
                {
                    data:data2,
                    label: "Outro",
                    lines: {
                        fill: 0.6,
                        lineWidth: 0
                    },
                    color: ['#BAD9F5']
                },
                
                {
                    data: data1,
                    points: {
                        show: true,
                        fill: true,
                        radius: 5,
                        fillColor: "#f89f9f",
                        lineWidth: 3
                    },
                    color: '#fff',
                    shadowSize: 0
                },
                {
                    data: data2,
                    points: {
                        show: true,
                        fill: true,
                        radius: 5,
                        fillColor: "#BAD9F5",
                        lineWidth: 3
                    },
                    color: '#fff',
                    shadowSize: 0
                }
               
            ], 
            
            
            {
            	
            	
            	series: {
                    lines: {
                        show: true,
//                        lineWidth: 2,
//                        fill: true,
//                        fillColor: {
//                            colors: [{
//                                    opacity: 0.5
//                                }, {
//                                    opacity: 0.1
//                                }
//                            ]
//                        }
                    },
                    points: {
//                        show: true,
//                        radius: 3,
//                        lineWidth: 1
                    },
                    shadowSize: 2
                },
            	
                xaxis: {
                	tickLength: 0,
                    tickDecimals: 0,                        
                    mode: "categories",
                    min: 2,
                    font: {
                        lineHeight: 15,
                        style: "normal",
                        variant: "small-caps",
                        color: "#6F7B8A"
                    }
                },
                yaxis: {
                    ticks: 3,
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

                    showTooltip(item.pageX, item.pageY, item.datapoint[0], item.datapoint[1], '');
                }
            } else {
                $("#tooltip").remove();
                previousPoint = null;
            }
        });

}
    
    
    // Order Chart Data
    var chartData = new Object();
    

    return {
    	
    	
        //main function
        init: function () {
        	
        	$.ajax({
				url: '/reports/getOrderReportByCh.sc',
				data: $('#form_regist').serialize(),
				success:function(jsonData)
				{
					chartData = jsonData;
					
					orderCountChart(chartData.data.ASPB.count, chartData.data.OUTRO.count);
				}
			});
        	
        	
        	$('#statistics_amounts_tab1').on('shown.bs.tab', function (e) {
                orderCountChart(chartData.data.ASPB.count, chartData.data.OUTRO.count);
            });
        	
            $('#statistics_amounts_tab2').on('shown.bs.tab', function (e) {
                orderAmountChart(chartData.data.ASPB.amount, chartData.data.OUTRO.amount);
            });
            
            
        },
    
    	initDashboardDaterange: function () {

    		$('#dashboard-report-range').daterangepicker(
    			{
	                opens: (Metronic.isRTL() ? 'right' : 'left'),
	                startDate: moment().subtract('days', 29),
	                endDate: moment(),
	                minDate: '01/01/2012',
	                maxDate: '12/31/2014',
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
	                    'Last 30 Days': [moment().subtract('days', 29), moment()],
	                    'This Month': [moment().startOf('month'), moment().endOf('month')],
	                    'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
	                },
	                buttonClasses: ['btn btn-sm'],
	                applyClass: ' blue',
	                cancelClass: 'default',
	                format: 'MM/DD/YYYY',
	                separator: ' to ',
	                locale: {
	                    applyLabel: 'Apply',
	                    fromLabel: 'From',
	                    toLabel: 'To',
	                    customRangeLabel: 'Custom Range',
	                    daysOfWeek: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
	                    monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
	                    firstDay: 1
	                }
    			},
	            function (start, end) {
	                $('#dashboard-report-range span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
	            }
    		);
	
	        $('#dashboard-report-range span').html(moment().subtract('days', 29).format('MMMM D, YYYY') + ' - ' + moment().format('MMMM D, YYYY'));
	        $('#dashboard-report-range').show();
	    },
	    
	    
	    getOrderOverviewData: function() {
	    	var table = $('#table_error_order');

	        // begin first table
	        table.dataTable({
	        	"paging":   false,
	            "ordering": false,
	            "info":     false,
	        	"processing": false,
	            "serverSide": true,
	            "dom":"",
	            "ajax": "/orders/errorList.sc",
                "columns": [
                            { "data": "orderId" },
                            { "data": "entCode" },
                            { "data": "sellerCode" },
                            { "data": "errorDate" }
                        ]
	        });
	    },
	    
	    
	    setOrderChartData: function(data) {
	    	chartData = data;
	    },
	    
	    // Tab Chart Ajax Refresh
	    refreshChart: function(index){
	    	
	    	switch(index) {
	    		case 0: orderAmountChart(chartData.data.ASPB.amount, chartData.data.OUTRO.amount);
	    		case 1: orderCountChart(chartData.data.ASPB.count, chartData.data.OUTRO.count);
	    	}
	    	
	    }

    };

}();