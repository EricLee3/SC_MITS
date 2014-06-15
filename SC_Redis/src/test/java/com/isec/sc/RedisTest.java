/**
 * 
 */
package com.isec.sc;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore; 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:root-context.xml"})
public class RedisTest {

	
	@Autowired	private StringRedisTemplate stringRedisTemplate;
	
	
	@Resource(name="stringRedisTemplate")
    private ValueOperations<String, String> valueOps;
	
	@Resource(name="stringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	
	@Value("${redis.default.dbindex}")
	private String redis_db_index;
	
	@Value("${redis.channel.magento.product}")
	private String redis_M_ch;
	
	
	@Value("${redis.magento.key.product}")
	private String redis_M_key_product;
	
	@Value("${redis.magento.key.order}")
	private String redis_M_key_order;
	
	@Value("${redis.magento.key.inventory}")
	private String redis_M_key_inv;
	
	@Value("${redis.magento.key.orderUpdate.S2M}")
	private String redis_M_key_ordUpdate_S2M;
	
	@Value("${redis.magento.key.orderUpdate.M2S}")
	private String redis_M_key_ordUpdate_M2S;
	
	@Test
    public void TestGetProductErrList() {
		
		List<String> errProductList = listOps.range(redis_M_key_ordUpdate_S2M, 0, -1);
		System.out.println("[errlist_Count]"+errProductList.size());
		
		for(int i=0; i<errProductList.size(); i++){
			System.out.println("["+i+"]"+errProductList.get(i));
		}
		
    }
	
	
	
	@Ignore
	public void TestGetRedisData() {
		
//		listOps.rightPush("1", "a");
//		listOps.rightPush("1", "b");
//		listOps.rightPush("1", "c");
		stringRedisTemplate.getConnectionFactory().getConnection().select(Integer.parseInt(redis_db_index));
		
		
		List<String> orderList = listOps.range("com:scteam:magento:order", 0, -1);
		System.out.println("[orderList.size]"+orderList.size());
		
		
		List<String> productList = listOps.range("com:scteam:magento:product", 0, -1);
		System.out.println("[productList.size]"+productList.size());
		
		
//		while(listOps.size("1")>0){
//			System.out.println(""+listOps.leftPop("1"));
//		}
		
	}
	
	
	@Ignore
	public void TestRedisItemPub(){
		
		try{

//			for(int i=0; i < 10; i++){
//				stringRedisTemplate.convertAndSend("com:scteam:magento:channel", "hello -" + i);
//				Thread.sleep(300);
//			}
			// String sendMsg = "{"db":"10","key":"com:scteam:magento:product"}";
			Map<String, String> sendMsgMap = new HashMap<String, String>();
			sendMsgMap.put("db", redis_db_index);
			sendMsgMap.put("type", "product");
			sendMsgMap.put("key", redis_M_key_product);
			
			String sendMsg = "";
			
			
			// Java Object(Map) to JSON
			ObjectMapper mapper = new ObjectMapper();
			sendMsg = mapper.writeValueAsString(sendMsgMap);
			
			System.out.println("[sendMsg]"+sendMsg);
			System.out.println("[redis_M_ch]"+redis_M_ch);
			
			stringRedisTemplate.convertAndSend(redis_M_ch, sendMsg);
			
			
			// Set Database Index
			stringRedisTemplate.getConnectionFactory().getConnection().select(Integer.parseInt(redis_db_index));
						
			listOps.leftPush(redis_M_key_product, "{\"item_id\":\"00001\",\"qty\":\"100\"}");
			listOps.leftPush(redis_M_key_product, "{\"item_id\":\"00002\",\"qty\":\"200\"}");
			listOps.leftPush(redis_M_key_product, "{\"item_id\":\"00003\",\"qty\":\"300\"}");
			
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Ignore
	public void TestRedisOrderStatusPub(){
		
		try{

//			for(int i=0; i < 10; i++){
//				stringRedisTemplate.convertAndSend("com:scteam:magento:channel", "hello -" + i);
//				Thread.sleep(300);
//			}
			// String sendMsg = "{"db":"10","key":"com:scteam:magento:product"}";
			Map<String, String> sendMsgMap = new HashMap<String, String>();
			sendMsgMap.put("db", redis_db_index);
			sendMsgMap.put("type", "orderUpdate");
			sendMsgMap.put("key", redis_M_key_ordUpdate_S2M);
			
			String sendMsg = "";
			
			
			// Java Object(Map) to JSON
			ObjectMapper mapper = new ObjectMapper();
			sendMsg = mapper.writeValueAsString(sendMsgMap);
			
			System.out.println("[sendMsg]"+sendMsg);
			System.out.println("[redis_M_ch]"+redis_M_ch);
			
			stringRedisTemplate.convertAndSend(redis_M_ch, sendMsg);
			
			listOps.leftPush(redis_M_key_inv, "{\"item_id\":\"00001\",\"qty\":\"100\"}");
			listOps.leftPush(redis_M_key_inv, "{\"item_id\":\"00002\",\"qty\":\"200\"}");
			listOps.leftPush(redis_M_key_inv, "{\"item_id\":\"00003\",\"qty\":\"300\"}");
			
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
