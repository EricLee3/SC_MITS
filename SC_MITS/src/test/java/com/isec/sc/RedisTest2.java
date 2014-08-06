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
import org.springframework.data.redis.core.ZSetOperations;
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
	
	@Resource(name="maStringRedisTemplate")
	private ZSetOperations<String, String> zSetOps;
	

	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	
	@Value("${channel.ma.jns.order}")
	private String ch_ma_jns_order;
	
	
	
	@Test
	public void insertPlotChartDataMonth(){
		
		String key = "count:ISEC:ASPB:orders:";
		
		/**
		데이타유형: {"yyyymm":"201408", "count":"150", "amount":"1500.00"}
		**/
/*		
		['01/2013', 4],
        ['02/2013', 8],
        ['03/2013', 10],
        ['04/2013', 12],
        ['05/2013', 2125],
        ['06/2013', 324],
        ['07/2013', 1223],
        ['08/2013', 1365],
        ['09/2013', 250],
        ['10/2013', 999],
        ['11/2013', 390]
		*/
		
		valueOps.set(key+"201301", "4");
		valueOps.set(key+"201302", "8");
		valueOps.set(key+"201303", "10");
		valueOps.set(key+"201304", "12");
		valueOps.set(key+"201305", "2125");
		valueOps.set(key+"201306", "324");
		valueOps.set(key+"201307", "1223");
		valueOps.set(key+"201308", "1365");
		valueOps.set(key+"201309", "250");
		valueOps.set(key+"201310", "999");
		valueOps.set(key+"201311", "390");
		valueOps.set(key+"201312", "210");
		
		
		String outro_key = "count:DA:OUTRO:orders:";
		
		
		valueOps.set(outro_key+"201301", "2");
		valueOps.set(outro_key+"201302", "4");
		valueOps.set(outro_key+"201303", "5");
		valueOps.set(outro_key+"201304", "6");
		valueOps.set(outro_key+"201305", "1000");
		valueOps.set(outro_key+"201306", "524");
		valueOps.set(outro_key+"201307", "1000");
		valueOps.set(outro_key+"201308", "700");
		valueOps.set(outro_key+"201309", "150");
		valueOps.set(outro_key+"201310", "555");
		valueOps.set(outro_key+"201311", "290");
		valueOps.set(outro_key+"201312", "200");
		
		//System.out.println("amount:"+valueOps.increment(key, 150.00));
		
	}
	
	
	@Ignore
	public void insertPlotChartDataMonth2(){
		
		String key = "amount:ISEC:ASPB:orders:";
		
		/**
		데이타유형: {"yyyymm":"201408", "count":"150", "amount":"1500.00"}
		**/
/*		
		['01/2013', 4],
        ['02/2013', 8],
        ['03/2013', 10],
        ['04/2013', 12],
        ['05/2013', 2125],
        ['06/2013', 324],
        ['07/2013', 1223],
        ['08/2013', 1365],
        ['09/2013', 250],
        ['10/2013', 999],
        ['11/2013', 390]
		*/
		
		valueOps.set(key+"201301", "100");
		valueOps.set(key+"201302", "80");
		valueOps.set(key+"201303", "30");
		valueOps.set(key+"201304", "150");
		valueOps.set(key+"201305", "200");
		valueOps.set(key+"201306", "300");
		valueOps.set(key+"201307", "180");
		valueOps.set(key+"201308", "100");
		valueOps.set(key+"201309", "120");
		valueOps.set(key+"201310", "200");
		valueOps.set(key+"201311", "75");
		valueOps.set(key+"201312", "50");
		
		
		String outro_key = "amount:DA:OUTRO:orders:";
		
		
		valueOps.set(outro_key+"201301", "200");
		valueOps.set(outro_key+"201302", "160");
		valueOps.set(outro_key+"201303", "60");
		valueOps.set(outro_key+"201304", "300");
		valueOps.set(outro_key+"201305", "400");
		valueOps.set(outro_key+"201306", "600");
		valueOps.set(outro_key+"201307", "360");
		valueOps.set(outro_key+"201308", "200");
		valueOps.set(outro_key+"201309", "140");
		valueOps.set(outro_key+"201310", "555");
		valueOps.set(outro_key+"201311", "290");
		valueOps.set(outro_key+"201312", "200");
		
		//System.out.println("amount:"+valueOps.increment(key, 150.00));
		
	}
	
	
	@Ignore
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
