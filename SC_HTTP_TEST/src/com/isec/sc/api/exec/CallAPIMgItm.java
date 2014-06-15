package com.isec.sc.api.exec;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;

import com.isec.sc.api.cm.ManageItem;
import com.isec.sc.api.core.util.FileContentReader;
import com.isec.sc.api.inv.GetInventorySupply;
import com.isec.sc.api.omp.CreateOrder;
import com.isec.sc.api.omp.GetOrderStatus;

public class CallAPIMgItm {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		
		
		/* 
		 * Cube -> SC 상품정보 연동(등록,수정)
		 * 
		 * 1. 동기화할 상품정보 조회 (DB, File)
		 * 2. ManageItem객체형 ArrayList로 저장
		 * 3. manageItem API invoke
		 */
		// Sample Data
		String[] arr_bar_code = {"GRA122A010BLML","GRA122A010BLXSS","GRA122A01GORML", "GRA122A01GORXSS"};
		String[] arr_brand_id = {"SD","AE","RL","TH"};
		String[] arr_brand_name = {"SuperDry","American Eagle","Ralph Rauren","Tommy"};
		Double[] arr_sale_price = {15000.00, 16000.00, 17000.00, 18000.00};
		String[] arr_pname = {"Jacket","Shirts","Pants","Polo"};
		String[] arr_item_color = {"BL","BL","OR", "OR"};
		String[] arr_item_size = {"ML","XSS","ML", "XSS"};
		
		
		ArrayList<ManageItem> itemList = new ArrayList<ManageItem>(); 
		for(int i=0; i<arr_bar_code.length; i++){
			ManageItem obj = new ManageItem();
			obj.setAction("Manage");					// 고정값
			obj.setGlobalItemID(""); 					// 현재는 ""처리
			obj.setItemID(arr_bar_code[i]);				// bar_code
			obj.setOrganizationCode("Matrix");			// 고정값(조직코드)
			obj.setUnitOfMeasure("EACH");				// 고정값
			obj.setEffectiveStartDate("2014-03-12");		// 현재일
			obj.setEffectiveEndDate("9999-12-31");		// 고정값
			obj.setManufacturerItem(arr_brand_id[i]);	// brand_id
			obj.setManufacturerName(arr_brand_name[i]);	// brand_name
			obj.setUnitCost(arr_sale_price[i]);			// sale_price(최초판매가)
			obj.setShortDescription(arr_pname[i]);		// pname(상품명)
			obj.setColorCode(arr_item_color[i]);			// item_color
			obj.setSizeCode(arr_item_size[i]);			// item_size
			obj.setStatus("3000");						// 고정값
			
			itemList.add(obj);
		}
		
		ManageItem mi = new ManageItem();
		mi.setItemList(itemList);
		mi.run();
	}
}

