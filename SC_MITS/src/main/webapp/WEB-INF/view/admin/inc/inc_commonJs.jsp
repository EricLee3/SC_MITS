<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<!-- BEGIN CORE PLUGINS -->
<!--[if lt IE 9]>
<script src="../../assets/global/plugins/respond.min.js"></script>
<script src="../../assets/global/plugins/excanvas.min.js"></script> 
<![endif]-->
<script src="../../assets/global/plugins/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
<!-- IMPORTANT! Load jquery-ui-1.10.3.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
<script src="../../assets/global/plugins/jquery-ui/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
<script src="../../assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->

<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="../../assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="../../assets/admin/layout/scripts/layout.js" type="text/javascript"></script>
<script src="../../assets/admin/layout/scripts/quick-sidebar.js" type="text/javascript"></script>
<script src="../../assets/admin/pages/scripts/custom-main.js" type="text/javascript"></script>

<script>
	jQuery(document).ready(function() {    
			
		Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
		QuickSidebar.init() // init quick sidebar
		
		Custom.init();
		
		$.extend({
		  getUrlVars: function(){
		    var vars = [], hash;
		    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
		    for(var i = 0; i < hashes.length; i++)
		    {
		      hash = hashes[i].split('=');
		      vars.push(hash[0]);
		      vars[hash[0]] = hash[1];
		    }
		    return vars;
		  },
		  getUrlVar: function(name){
		    return $.getUrlVars()[name];
		  }
		});
		
		
		/* 
		$('.page-sidebar .ajaxify.start').click() // load the content for the dashboard page.
		
   		// URL History Manage
   		$.address.change(function(event) {  
   		    // do something depending on the event.value property, e.g.  
   		    // $('#content').load(event.value + '.xml');  
   		    
   		    console.debug(event.value);
   		    
   		    $('.page-sidebar .ajaxify[href="'+event.value+'"]').parent().parent('a').click();
   		 	$('.page-sidebar .ajaxify[href="'+event.value+'"]').click();
   		 
   		});
   		
   		$('a').click(function() {  
   			
   			console.debug("a.click. "+$(this).attr('href'));
   			if($(this).attr('href').indexOf("#") > -1 || $(this).attr('href') == "javascript:;" ) return;
   			
   		    $.address.value($(this).attr('href'));  
   		}); */
   		
   		
   		// left menu 처리
   		var currLoc = window.location.pathname;
   		
   		if(currLoc == "/" || currLoc == "/index.do"){
   			$('#menu_home').addClass("active open");
   		}else{
   			
   			var submenu_idx = $.getUrlVar("mn");
			if( submenu_idx == undefined){
				submenu_idx = 0;
			}
   			
   			// 오더메뉴
   			if(currLoc.indexOf("/orders/") > -1){
   				
   				$('#menu_order').addClass("active open");
   		   		$('#menu_order .sub-menu li').eq(submenu_idx).addClass("active");
   			
   		   		// 
   			}if(currLoc.indexOf("/system/") > -1){
   				$('#menu_system').addClass("active open");
   		   		$('#menu_system .sub-menu li').eq(submenu_idx).addClass("active");
   			}
   		}
   		
   		
   		
	});
</script>