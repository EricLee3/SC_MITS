package com.isec.sc.intgr.http;

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
public class ScOrderStatusController {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderStatusController.class);
	
	
	@Value("${redis.magento.key.orderUpdate.S2M}")
	private String redis_M_key_ord_upd_S2M;
	
	@Value("${redis.magento.key.orderUpdate.M2S}")
	private String redis_M_key_ord_upd_M22;
	
	
	@RequestMapping(value = "/orderUpdate")
	public void updateOrderStatus(@RequestParam(required=false) String returnXML, 
								  @RequestParam String status,
								  Model model,
								  HttpServletResponse res) throws Exception{
		
		logger.info("[returnXML]"+returnXML);
		logger.info("[status]"+status);
		
		
		// 1. Sterling Status 
		
		
		// 2. Receive Message(retrunXML) Parsing
		
		
		// 3. Make sendData (JSON) 
		
		
		
		// 4. Store sendData in RedisDB
		
		
		
		
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
		//return "home";
		
	}
	
	
}