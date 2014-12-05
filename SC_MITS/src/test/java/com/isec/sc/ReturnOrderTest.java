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
	
	
	@Value("${sc.api.returnOrder.template}")
	private String RETURN_ORDER_TEMPLATE;
	
	@Value("${sc.api.returnOrderLine.template}")
	private String RETURN_ORDER_LINE_TEMPLATE;
	
	/**
	 * 반품오더 생성 테스트 - 원주문
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateReturnOrder() throws Exception{
		
		String returnOrderXML = FileContentReader.readContent(getClass().getResourceAsStream(RETURN_ORDER_TEMPLATE));
		String returnOrderLineXML = FileContentReader.readContent(getClass().getResourceAsStream(RETURN_ORDER_LINE_TEMPLATE));
		
		/**
		 * 	<OrderLine ReturnReason="{0}" ShipNode="{1}" >
		        <OrderLineTranQuantity OrderedQty="{2}"/>
		      	<DerivedFrom DocumentType="{3}" EnterpriseCode="{4}" OrderNo="{5}"  PrimeLineNo="{6}" SubLineNo="{7}"/>
		    </OrderLine>
		 */
		String returnReason = "A01";
		String shipNode = "WH001";
		String orderedQty = "1";
		String ori_docType = "0001";
		String ori_entCode = "KOLOR";
		String ori_orderNo = "100000418";
		String ori_orderLineNo = "1";
		String ori_orderLineSubNo = "1";
		
		
		MessageFormat msg = new MessageFormat(returnOrderLineXML);
		String lineXML = msg.format(new String[] {
				returnReason, shipNode, orderedQty, ori_docType, ori_entCode, ori_orderNo, ori_orderLineNo, ori_orderLineSubNo
			  } );
		
		String orderLineText = lineXML;
		
		/**
		 * <?xml version="1.0" encoding="UTF-8"?>
			<Order DocumentType="0003" EnterpriseCode="{0}" SellerOrganizationCode="{1}" OrderNo="{2}">
				<OrderLines>
			        {3}
			     </OrderLines>
				<PersonInfoShipTo FirstName="{4}" LastName="{5}" DayPhone="{6}" MobilePhone="{7}" EMailID="{8}"
			                       AddressLine1="{9}" AddressLine2="{10}" AddressLine3="{11}" AddressLine4="{12}" City="{13}" Country="{14}" ZipCode="{15}"/>
			    <PersonInfoBillTo  FirstName="{4}" LastName="{5}" DayPhone="{6}" MobilePhone="{7}" EMailID="{8}"
			                       AddressLine1="{9}" AddressLine2="{10}" AddressLine3="{11}" AddressLine4="{12}" City="{13}" Country="{14}" ZipCode="{15}" />
			</Order>
		 */
		
		String entCode = "KOLOR";
		String sellerCode = "ASPB";
		String orderNo = "";  //반품주문정보는 마젠토에서 생성
		String fName = "jang";
		String lName = "yk";
		String phone = "010-1111-2222";
		String mPhone = "010-1111-3333";
		String email = "a@test.com";
		String addr1 = "aaaaaaa";
		String addr2 = "bbbbbbb";
		String addr3 = "ccccccc";
		String addr4 = "ddddddd";
		String city = "seoul";
		String country = "KR";
		String zipCode = "123-456";
		
		
		msg = new MessageFormat(returnOrderXML);
		String inputXML = msg.format(new String[] {
			    entCode, sellerCode, orderNo,
			    orderLineText,
				fName,lName,phone, mPhone, email, addr1, addr2, addr3, addr4, city, country, zipCode
			  } );
		
		
		
		System.out.println(inputXML);
		
		String outputXML = sterlingApiDelegate.comApiCall("createOrder", inputXML);
		
		System.out.println(outputXML);
		
	}
}
