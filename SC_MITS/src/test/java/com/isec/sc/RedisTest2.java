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
	
	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	
	@Value("${channel.ma.jns.order}")
	private String ch_ma_jns_order;
	

	
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
		
		
		
		List<String> orderErrList = listOps.range("ISEC:JNS:order:error", 0, -1);
		System.out.println("##### [orderErrorlist]"+orderErrList.size());
		
		
		//listOps.rightPop("ISEC:JNS:order:error"); 
		
		for(int i=0; i<orderErrList.size(); i++){
			System.out.println("["+i+"]"+orderErrList.get(i));
		}
		
		
		
    }

}
