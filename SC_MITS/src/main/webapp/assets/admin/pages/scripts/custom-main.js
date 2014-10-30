/**
Custom module for you to write your own javascript functions
**/
var Custom = function () {


	// basic form validation
    var handleValidation1 = function(formName) {
        // for more info visit the official plugin documentation:
            // http://docs.jquery.com/Plugins/Validation

            var form1 = $('#'+formName);
            var error1 = $('.alert-danger', form1);
            var success1 = $('.alert-success', form1);

            form1.validate({
                errorElement: 'span', //default input error message container
                errorClass: 'help-block help-block-error', // default input error message class
                focusInvalid: false, // do not focus the last invalid input
                ignore: "", // validate all fields including form hidden input
                messages: {
                    select_multi: {
                        maxlength: jQuery.validator.format("Max {0} items allowed for selection"),
                        minlength: jQuery.validator.format("At least {0} items must be selected")
                    }
                },
                rules: {
                    name: {
                        minlength: 2,
                        required: true
                    },
                    email: {
                        required: true,
                        email: true
                    },
                    url: {
                        required: true,
                        url: true
                    },
                    number: {
                        required: true,
                        number: true
                    },
                    digits: {
                        required: true,
                        digits: true
                    },
                    creditcard: {
                        required: true,
                        creditcard: true
                    },
                    occupation: {
                        minlength: 5,
                    },
                    select: {
                        required: true
                    },
                    select_multi: {
                        required: true,
                        minlength: 1,
                        maxlength: 3
                    }
                },

                invalidHandler: function (event, validator) { //display error alert on form submit
                    success1.hide();
                    error1.show();
                    Metronic.scrollTo(error1, -200);
                },

                highlight: function (element) { // hightlight error inputs
                    $(element)
                        .closest('.form-group').addClass('has-error'); // set error class to the control group
                },

                unhighlight: function (element) { // revert the change done by hightlight
                    $(element)
                        .closest('.form-group').removeClass('has-error'); // set error class to the control group
                },

                success: function (label) {
                    label
                        .closest('.form-group').removeClass('has-error'); // set success class to the control group
                },

                submitHandler: function (form) {
                    success1.show();
                    error1.hide();
                }
            });
    }


    // public functions
    return {

        //main function
        init: function () {
	         
        	
	        	$.ajaxSetup({
	        		
				beforeSend: function(xhr) {
					xhr.setRequestHeader("AJAX", true);
				},
				 cache: true,
				 dataType: 'json',
				 error: function(xhr, status, error){
				     
					if(xhr.responseText != undefined && xhr.responseText == 'timeout'){
						alert("로그인 세션시간이 만료되어 자동로그아웃 되었습니다.\n 로그인페이지로 이동합니다.");
					    location.href = "/";
					    
					}else {
					
					    alert("예기치 못한 에러가 발생했습니다.\n다시 시도하시고 계속 에러가 발생하면 시스템 관리자에게 문의하세요.");
					
			        }
				 },
				 timeout: 10000,
				 type: 'POST',
				 url:''
            });
		        
	        
	        	$.fn.digits = function(){ 
		    	    return this.each(function(){ 
		    	        $(this).text( $(this).text().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") ); 
		    	    });
		    	}
        },
	
        setFormValidation: function (formName) {
        		handleValidation1(formName);
        
        }
	        
    };

}();

/***
Usage
***/
//Custom.init();
//Custom.doSomeStuff();