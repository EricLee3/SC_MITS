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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;



public class ProductSyncTask {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductSyncTask.class);


	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;

	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
    public void syncProduct(String redisKey, String redisErrKey){
        
    	try {
    		 
    		logger.debug("##### [syncProduct] Job Task Started!!!");
        	logger.debug("     ----- Read Key ["+redisKey+"]");
        	logger.debug("     ----- Error Key ["+redisErrKey+"]");
    	  	
    			
    	  	long dataCnt =  listOps.size(redisKey);
    	  	logger.debug("["+redisKey+"] data length: "+dataCnt);
			
			
			for(int i=0; i<dataCnt; i++){
				
				String xmlData = listOps.rightPop(redisKey);
				
				// SC API 호출
				String result = sterlingApiDelegate.manageItem(xmlData);
				
				// 에러발생시 다른 key로 해당xml저장
				if("0".equals(result)){
					
					
					System.out.println("##### Error Occured!!!");
					
					Map<String, String> errMsgMap = new HashMap<String, String>();
					errMsgMap.put("type", "product");
					errMsgMap.put("key", redisKey);
					errMsgMap.put("data", xmlData);
					errMsgMap.put("date", cuurentDate());
					
					// Java Object(Map) to JSON	
					String sendMsg = "";
					ObjectMapper resultMapper = new ObjectMapper();
					sendMsg = resultMapper.writeValueAsString(errMsgMap);
					
					listOps.leftPush(redisErrKey, sendMsg);
				}
			}

			
		} catch (Exception e) {
			
			// 예외처리
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
