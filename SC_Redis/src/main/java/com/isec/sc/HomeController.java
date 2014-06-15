package com.isec.sc;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/orderUpdates")
	public void home(@RequestParam(required=false) String param, 
					Model model,
					HttpServletResponse res) throws Exception{
		
		logger.info("[param]"+param);
		
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><a>aaa</a>");
		//return "home";
	}
	
}
