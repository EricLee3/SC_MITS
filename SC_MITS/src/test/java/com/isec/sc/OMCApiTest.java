/**
 * 
 */
package com.isec.sc;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Ignore; 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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

import scala.annotation.meta.setter;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.scheduler.OrderProcessTask;
import com.isec.sc.intgr.scheduler.ProductSyncTask;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/application-config.xml","classpath:quartz-config.xml"})
public class OMCApiTest {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	@Autowired	private Environment env;
	
	@Autowired	private OrderProcessTask orderTask;
	@Autowired	private ProductSyncTask productTask;
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valOps;
	
	@Resource(name="maStringRedisTemplate")
	private HashOperations<String, String, Object> hashOps;
	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	@Value("${redis.port}")
	private String redis_port;
	
	
	@Test
	public void test_date_parser() {
	   
		CommonUtil.getDateTimeByTimeZone("2014-11-04T05:00:00+09:00");
	}
	
	
	
	
	@Test
	public void testConfirmShipment() throws Exception{
		
		
		/**
		   {
			    "org_code": "80",
			    "sell_code": "ASPB",
			    "orderId": "Y100001100",
			    "orderHeaderKey": "20141010191200239236",
			    "tranDt": "20141016",
			    "status": "3700",
			    "list": [
			        {
			            "ship_node": "WH001",
			            "orderLineNo": "1",
			            "orderLineKey": "20141010191200239237",
			            "orderReleaseKey": "20141010191300239277",
			            "shipmentNo": "341015007",
			            "expnm": "11111",  -- 송장번호
			            "expNo": "00",     -- 택배사코드
			            "outDt": "20141016",
			            "outTime": "165021"
			        },
			        {
			            "ship_node": "WH001",
			            "orderLineNo": "2",
			            "orderLineKey": "20141010191200239238",
			            "orderReleaseKey": "20141010191300239277",
			            "shipmentNo": "341015007",
			            "expnm": "11111",
			            "expNo": "00",
			            "outDt": "20141016",
			            "outTime": "165021"
			        }
			    ]
			}

		 */
		
		
		String jsonString = "{"
							+"	    \"org_code\": \"80\","
							+"	    \"sell_code\": \"ASPB\","
							+"	    \"orderId\": \"100001415\","
							+"	    \"orderHeaderKey\": \"\","
							+"	    \"tranDt\": \"20141016\","
							+"	    \"status\": \"3700\","
							+"	    \"list\": ["
							  +"      {"
							    +"        \"ship_node\": \"WH001\","
							      +"      \"orderLineNo\": \"1\","
							        +"    \"orderLineKey\": \"\","
							          +"  \"orderReleaseKey\": \"\","
							            +"\"shipmentNo\": \"341015007\","
							            +"\"expnm\": \"11111\","  
							            +"\"expNo\": \"00\",   " 
							            +"\"outDt\": \"20141016\","
							            +"\"outTime\": \"165021\""
						            +"},"
						            +"{"
							            +"\"ship_node\": \"WH001\","
							            +"\"orderLineNo\": \"2\","
							            +"\"orderLineKey\": \"\","
							            +"\"orderReleaseKey\": \"\","
							            +"\"shipmentNo\": \"341015007\","
							            +"\"expnm\": \"11111\","
							            +"\"expNo\": \"00\","
							            +"\"outDt\": \"20141016\","
							            +"\"outTime\": \"165021\""
						            +"}"
					            +"]"
				            +"}";
		
		
		
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> dataMap = mapper.readValue(jsonString, new TypeReference<HashMap<String,Object>>(){});
		
		
		orderTask.processConfirmShipment(dataMap, "80:ASPB:order:update:C2S", "KOLOR:ASPB:order:update:S2M", "KOLOR:ASPB:order:update:error:3700");
		
	}
}
