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
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	@RequestMapping(value = "/getOrderList")
	@ResponseBody
	public ModelAndView getOrderList( @RequestParam String apiName, @RequestParam String inputXML ) throws Exception{ 
		
		
		logger.debug("apiName: "+apiName); 
		logger.debug("inputXML: "+inputXML); 
		
		String outputXML = sterlingApiDelegate.comApiCall(apiName, inputXML);
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject(outputXML);
		return mav;   
		
	}
	
	@RequestMapping(value = "/home.do")
	public ModelAndView home( @RequestParam(required = false) String param ) throws Exception{ 
		
		
		logger.debug("param: "+param);
		
		ModelAndView mav = new ModelAndView("jspView");
		mav.addObject("message", param);
		mav.setViewName("showMessage");
		return mav;
		
	}
}
