/*
 *  
 *  * Revision History
 *  * Author              Date                  Description
 *  * ------------------   --------------       ------------------
 *  *  beyondj2ee          2014.01.02              
 *  
 */

package com.isec.sc.intgr.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;



public class OrderCreateTask {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderCreateTask.class);


	@Autowired	private StringRedisTemplate maStringRedisTemplate;

	@Autowired	private SterlingApiDelegate sterlingApiDelegate;


	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;

	
    public void createOrder(String redisKey, String redisPushKey, String redisErrKey){
        
//        System.out.println("Spring 4.0 + Quartz 1.8.6 ");
    	logger.debug("##### createOrder["+redisKey+"] Task Excuted!!!");
    	logger.debug("##### redisPushKey ["+redisPushKey+"]");
    	logger.debug("##### redisErrKey["+redisErrKey+"]");
    	
//    	String pushKey = redisKey + ":update:S2M";
//    	String errKey = redisKey + ":error";
    	
    	Map<String,String> sendMsgMap = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
    	
		
		
    	long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		try{
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				String xmlData = listOps.rightPop(redisKey);
				
				// SC API 호출
				HashMap<String, Object> result = sterlingApiDelegate.createOrder(xmlData);
				String status = (String)result.get("status");
				
				
				// Create 성공
				if("1100".equals(status)){
					
					String orderSuccJSON = mapper.writeValueAsString(result);
					logger.debug("[Create Order Successful]");
					logger.debug("[orderSucc JSON]"+orderSuccJSON);
					
					// 결과데이타 저장
					listOps.leftPush(redisPushKey, orderSuccJSON);
				
				// Create 실패
				}else if("0000".equals(status)){
					
					sendMsgMap.put("data", xmlData);
					sendMsgMap.put("occure_date", cuurentDate());
					
					
					// Java Object(Map) to JSON	
					String orderErrJSON = mapper.writeValueAsString(sendMsgMap);
					logger.debug("Create Order Error occured");
					logger.debug("[orderErr JSON]"+orderErrJSON);
					
					// 실패데이타 저장
					listOps.leftPush(redisErrKey, orderErrJSON);
				}
			}
			
		}catch(Exception e){
			logger.debug("##### Create Order Task Exeption Occured");
			e.printStackTrace();
		}
		
    }
    
    
    private String cuurentDate(){
		
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
		Date currentTime = new Date ( );
		String mTime = mSimpleDateFormat.format ( currentTime );
		
		return mTime;
	}

}
