package com.isec.sc.intgr.web;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.http.ScOrderShipmentController;



@Controller
@PropertySource("classpath:mits.properties")
public class DefaultService {

	private static final Logger logger = LoggerFactory.getLogger(DefaultService.class);
	
	@Autowired	private Environment env;
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	
	@RequestMapping(value = {"/","/index.do"})
	public String home() throws Exception{ 
		
		logger.debug("Redirect Index Page!!!");
		
		return "redirect:index.html";
		
	}
	
}
