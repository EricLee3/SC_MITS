/**
 * 
 */
package com.isec.sc;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Ignore; 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/application-config.xml","classpath:quartz-config.xml"})
public class RedisTest2 {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Resource(name="maStringRedisTemplate")
    private ValueOperations<String, String> valueOps;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private HashOperations<String, String, Object> hashOps;
	
	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	
	@Value("${channel.ma.jns.order}")
	private String ch_ma_jns_order;
	
	
	@Test
	public void TestSetList() {
		
		String errorJSON = "{ \"docType\":\"0001\", \"entCode\":\"DA\", \"sellerCode\":\"OUTRO\", \"orderId\":\"0001\", "
				+ " \"orderXML\":\"<xml>\","
				+ " \"errorMsg\":\"Error Message\","
				+ " \"errorDetail\":\"Error Detail Message\","
				+ " \"errorDate\":\"2013-11-23 14:30\" "
				+ "}";
		
		String key = "DA:OUTRO:order:error";
		List<String> orderList = listOps.range(key, 0, -1);
		
		for( int i=0; i<orderList.size(); i++){
			listOps.rightPop(key);
		}
		
		for( int i=0; i<100; i++){
			listOps.leftPush(key, errorJSON);
		}
	}
	
	
	@Ignore
	public void TestHashSet() {
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3.00");

		hashOps.putAll("HKey", map);
		
		List<Object> hashList = hashOps.multiGet("Hkey*", map.keySet());
		System.out.println("[hashList.size]"+ hashList.size());
		
		
	}
	
	
	@Test
    public void TestGetOrderListMagento() {
		
		
		// OUTRO
		List<String> orderList = listOps.range("DA:OUTRO:order:update:S2M", 0, -1);
		System.out.println("##### [orderlist - Outro]"+orderList.size());
		for(int i=0; i<orderList.size(); i++){
			System.out.println("["+i+"]"+orderList.get(i));
		}
		
		// JNS
		List<String> orderWList = listOps.range("ISEC:JNS:order:update:S2M", 0, -1);
		System.out.println("##### [orderlist - JNS]"+orderWList.size());
		for(int i=0; i<orderWList.size(); i++){
			System.out.println("["+i+"]"+orderWList.get(i));
		}
		
		
		
		List<String> orderErrList = listOps.range("DA:OUTRO:order:error", 0, -1);
		System.out.println("##### [orderErrorlist]"+orderErrList.size());
		
		
		//listOps.rightPop("ISEC:JNS:order:error"); 
		
		for(int i=0; i<orderErrList.size(); i++){
			System.out.println("["+i+"]"+orderErrList.get(i));
		}
		
    }
}
