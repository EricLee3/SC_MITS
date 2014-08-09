package com.isec.sc.intgr.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.isec.sc.intgr.scheduler.OrderProcessTask;

@Component
public class OrderReportService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(OrderProcessTask.class);
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	
	public OrderReportService(){
		
	}
	
	public void saveOrderReport(String key){
		
		logger.debug("[]"+reportStringRedisTemplate.hasKey("count:ISEC:ASPB:orders:201301"));
	}
}
