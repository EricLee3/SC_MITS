package com.isec.sc.intgr.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;



@Controller
public class DefaultService {

	private static final Logger logger = LoggerFactory.getLogger(DefaultService.class);
	
	
	@RequestMapping(value = {"/","/index.do"})
	public String home() throws Exception{ 
		
		logger.debug("Redirect Index Page!!!");
		
		return "redirect:index.html";
		
	}
	
	
}
