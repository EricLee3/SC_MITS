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


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/application-config.xml","classpath:quartz-config.xml"})
public class ProductSyncTest {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private HashOperations<String, String, Object> hashOps;
	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	
	@Value("${key.ca.KOLOR.product.C2S}")
	private String key_aspb_product_c2s;
	
	@Value("${key.ca.KOLOR.inventory.C2S}")
	private String key_aspb_inventory_c2s;
	
	// 현재고, 가용재고 조회테스트
	@Test
	public void testCalcInv(){
		
		try{
			Double qty = sterlingApiDelegate.getCalcQtyBeforeAdjustInv("Auro_Store_1", "ASPB_ITEM_0001", "", "EACH", "S");
			
			System.out.print(qty);
		}catch(Exception e){
			
		}
	}
	
	
	// Cube 상품등록 테스트
	@Test
	public void sendProductFromCube(){
		
		
		/*
		 * {
		    "list": [
		        {
		            "org_code": "사업부코드",
		            "prodinc": "스타일코드",
		            "brand_id": "브랜드ID",
		            "brand_name": "브랜드명",
		            "nation": "원산지",
		            "sale_price": "최초판매가",
		            "optioninfo": [
		                {
		                    "item_color": "컬러",
		                    "item_size": "사이즈",
		                    "bar_code": "상품바코드"
		                },
		                {
		                    "item_color": "컬러",
		                    "item_size": "사이즈",
		                    "bar_code": "상품바코드"
		                }
		            ]
		        }
		    	]
			}
		 */
		
		
		HashMap<String, ArrayList<HashMap<String,Object>>> itemMap = new HashMap<String, ArrayList<HashMap<String,Object>>>();
		ArrayList<HashMap <String,Object>> itemList = new ArrayList<HashMap <String,Object>>();
		
		for(int i=1; i<=1; i++){
			
			String stylecd = "AYB5CL501";
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("org_code", "80");
			map.put("prodinc", stylecd);
			map.put("pname", "Capri Blue Travel Tin VOLCANO");
			map.put("sale_price", "5000");
			map.put("brand_id", "000072");
			map.put("brand_name", "aspen_bay");
			map.put("tran_date", CommonUtil.cuurentDateFromFormat("yyyyMMdd"));
			map.put("tran_seq", ""+i);
			
			ArrayList<HashMap <String,String>> barCodeList = new ArrayList<HashMap <String,String>>();
			for(int j=38; j<=38; j++){
				
				
				HashMap<String, String> barCodeMap = new HashMap<String, String>();
				barCodeMap.put("bar_code", stylecd+"03L");
				barCodeMap.put("item_color", "03");
				barCodeMap.put("item_size", "L");
				
				barCodeList.add(barCodeMap);
			}
			map.put("optioninfo", barCodeList);
			itemList.add(map);
		}
		
		itemMap.put("list", itemList);
		
		try{
			// Java Object(Map) to JSON
			ObjectMapper mapper = new ObjectMapper();
			String jsonData = mapper.writeValueAsString(itemMap);
			
			System.out.println("[jsonData]"+jsonData);
			System.out.println("[key]"+key_aspb_product_c2s);
			
			listOps.leftPush(key_aspb_product_c2s, jsonData);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// Cube 재고연동 테스트
	@Test
	public void sendInventoryFromCube(){
		
		
		/*
		 * {
			"list": [
				        {
				            "org_code":"사업부코드"
				            "ship_node": "창고코드",
				            "bar_code": "상품코드",
				            "qty": "수량",
							"uom": "측정단위"
				        }
				    ]
			}
		 */
		HashMap<String, ArrayList<HashMap<String,String>>> itemMap = new HashMap<String, ArrayList<HashMap<String,String>>>();
		ArrayList<HashMap <String,String>> itemList = new ArrayList<HashMap <String,String>>();
		
		for(int i=38; i<=38; i++){
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("org_code", "80");
			map.put("ship_node", "WH001");
			
			
			String stylecd = "AYB5CL501";
			
			map.put("bar_code", stylecd+"03L");
			map.put("qty", "50");
			map.put("uom", "EACH");
			map.put("tran_date", CommonUtil.cuurentDateFromFormat("yyyyMMdd"));
			map.put("tran_seq", ""+i);
			
			itemList.add(map);
		}
		
		
		itemMap.put("list", itemList);
		
		try{
			// Java Object(Map) to JSON
			ObjectMapper mapper = new ObjectMapper();
			String jsonData = mapper.writeValueAsString(itemMap);
			
			System.out.println("[jsonData]"+jsonData);
			System.out.println("[key]"+key_aspb_inventory_c2s);
			
			listOps.leftPush(key_aspb_inventory_c2s, jsonData);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void getKeyProductSync() {
		
		String C2S = "80:ON9999:product:C2S";
		
	    String key = "SLV:ASPB:product:S2M";
	    String key1 = "80:ON9999:product:S2C";
	    String key2 = "80:ON9999:product:error";
	    
	    List<String> productC2S = listOps.range(C2S, 0, -1);
	    System.out.println( "["+productC2S+"]" +productC2S.size());
	    
	    for(int i=0; i<productC2S.size(); i++){
    		System.out.println( "[S2M Data]" +productC2S.get(i));
	    }
	    
	    List<String> product = listOps.range(key, 0, -1);
	    System.out.println( "["+key+"]" +product.size());
	    
	    List<String> product1 = listOps.range(key1, 0, -1);
	    System.out.println( "["+key1+"]" +product1.size());
	    
	    List<String> error = listOps.range(key2, 0, -1);
	    System.out.println( "["+key2+"]" +error.size());
	    
	    
	    
	    maStringRedisTemplate.delete(C2S);
	    
//	    maStringRedisTemplate.delete(key);
//	    maStringRedisTemplate.delete(key1);
//	    maStringRedisTemplate.delete(key2);
	}
	
	
	
	@Ignore
	public void deleteKeyDataVM() {
	    
	    String errorKey = "*:*:order:error";
	    
	    maStringRedisTemplate.delete(errorKey);
	}
	
	@Ignore
	public void pushPopTest(){
		
		listOps.leftPush("a", "1");
		listOps.leftPush("a", "2");
		listOps.leftPush("a", "3");
		
		System.out.println(listOps.rightPop("a"));
		System.out.println(listOps.rightPop("a"));
		System.out.println(listOps.rightPop("a"));
	}
}
