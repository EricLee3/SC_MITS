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
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persist;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import scala.annotation.meta.setter;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.api.xml.beans.InventoryList;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/application-config.xml","classpath:quartz-config.xml"})
public class ReturnOrderTest {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	@Autowired	private Environment env;
	
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valOps;
	
	@Resource(name="maStringRedisTemplate")
	private HashOperations<String, String, Object> hashOps;
	
	@Value("${ui.status.text.kr.1000}")
	private String ui1000;
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	@Value("${redis.port}")
	private String redis_port;
	
	@Value("${sc.order.type.sales}")
	private String SC_ORDER_TYPE_SALES;
	
	
	@Value("${sc.api.createOrder.template}")
	private String CREATE_ORDER_TEMPLATE;
	
	/**
	 * 반품오더 생성 테스트 - 원주문
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateReturnOrder() throws Exception{
		
		String orderXML = FileContentReader.readContent(getClass().getResourceAsStream(CREATE_ORDER_TEMPLATE));
		
		
		
		
		MessageFormat msg = new MessageFormat(orderXML);
		String inputXML = msg.format(new String[] {
//			    docType, entCode, sellerCode, orderNo, billToId,
//			    shipNode,orderLineText,
//				fName,lName,phone, mPhone, email, addr1, addr2, city, zipCode
			  } );
		
		
	}
}
