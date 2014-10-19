package com.isec.sc.intgr.api.util;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;


@Component
public class RedisCommonService {
	
private static final Logger logger = LoggerFactory.getLogger(RedisCommonService.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;	
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	
	
	public void saveErrDataByOrderId(String errKey, String orderId, String errMsg){
		

		String keyName = errKey + ":" + orderId;
		
		logger.debug("[Error orderId]"+orderId);
		logger.debug("[Error Data]"+errMsg);
		logger.debug("[Error Key]"+keyName);
		
		// RedisDB에 메세지 저장
		valueOps.set(keyName, errMsg);
		
		
		logger.debug("[Error Data Saved!]"+valueOps.get(keyName));
  	}
	
	
}
