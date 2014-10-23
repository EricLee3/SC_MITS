package com.isec.sc.intgr.web;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class DefaultController {

	private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);
	
	@RequestMapping(value = {"/","/index.do"})
	public String home() throws Exception{ 
		
		logger.debug("Redirect Index Page!!!");
		
//		return "redirect:index.html";
		return "index";
		
	}
	
	@RequestMapping(value = "/login.sc")
	public ModelAndView login(@RequestParam String userid, @RequestParam String password){
		
		logger.debug("[userid]"+userid);
		logger.debug("[password]"+password.substring(0,2));
		
		
		// userid,password체크
		String succ = "Y"; 
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("succ", succ);
		return mav;
	}
}
