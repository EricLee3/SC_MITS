package com.isec.sc.intgr.api.handler;

import java.io.Reader;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value="/sc")
public class ScInventoryHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(ScInventoryHandler.class);
	
	
	@RequestMapping(value = "/changeInventory.do")
	public void changeInventory(@RequestParam(required=false) String returnXML, 
								  @RequestParam String status,
								  Model model,
								  HttpServletResponse res) throws Exception{
		
		// logger.debug("[returnXML]"+returnXML);
		// logger.debug("[status]"+status);
		
		
		// 1. Sterling Status   aaa
		
		
		// 2. Receive Message(retrunXML) Parsing
		
		
		// 3. Make sendData (JSON) 
		
		
		
		// 4. Store sendData in RedisDB
		
		
		
		
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
		//return "home";
		
	}
	
	
}
