package com.isec.sc.api.exec;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;

import com.isec.sc.api.cm.ManageItem;
import com.isec.sc.api.core.util.FileContentReader;
import com.isec.sc.api.inv.AdjustInventory;
import com.isec.sc.api.inv.GetInventorySupply;
import com.isec.sc.api.omp.CreateOrder;
import com.isec.sc.api.omp.GetOrderStatus;

/**
 * Cube -> SC 재고정보 연동
 * 
 * 큐브의 상품재고수량(판매가능수량)을 전달받아 SC의 현재 재고수량과의 차이분을
 * 계산하여 SC의 adjustInventory(재고조정) API를 호출한다.
 * SC 현재의 재고수량은 getInventorySupply API를 통해 알 수 있다.
 * 
 * 이런 처리기 필요한 이유는 
 * Cube 와의 재고연동이 변경분에 대한 실시간 동기화방식이 아니라
 * 주기에 따른 현 재고수량에 대한 동기화 방식이기 때문.
 * 
 * SC의 API는 변경분에 대한 재고조정방식이라 별도의 계산처리가 필요. 
 * 한글 테스
 * 
 * @author ykjang
 */
public class CallAPIAdInv {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		// 조직코드(큐브 사업부코드)
		String[] arr_org_code = {"Matrix","Matrix","Matrix","Matrix"};
		// 상품코드(SKU단위)
		String[] arr_bar_code = {"GRA122A010BLML","GRA122A010BLXSS","GRA122A01GORML", "GRA122A01GORXSS"};
		// 재고수량(판매가능수량)
		Integer[] arr_stockQty = {10,10,10,10};
		// 출하노드(창고코)
		String[] arr_ship_node = {"Matrix_WH1","Matrix_WH1","Matrix_WH1","Matrix_WH1"};
		
		ArrayList<AdjustInventory> itemList = new ArrayList<AdjustInventory>(); 
		for(int i=0; i<arr_bar_code.length; i++){
			
			AdjustInventory obj = new AdjustInventory();
			obj.setItemID(arr_bar_code[i]);
			obj.setOrganizationCode(arr_org_code[i]);
			obj.setUnitOfMeasure("EACH");
			// obj.setProductClass("");
			obj.setSupplyType("ONHAND");
			obj.setAdjustmentType("ADJUSTMENT");
			obj.setShipNode(arr_ship_node[i]);
			
			
			// SC 현 재고정보(현재고수량) 조회 API호출 
			GetInventorySupply is = new GetInventorySupply();
			is.setItem(arr_bar_code[i]);
			is.setOrganizationCode(arr_org_code[i]);
			is.setShipNode(arr_ship_node[i]);
		
			int curr_inv_cnt = is.run();
			System.out.println("[item_id]"+arr_bar_code[i]);
			System.out.println("[current quantity]"+curr_inv_cnt);
			
			// Cube의 재고수량과 SC의 재고수량이 같은 경우 Skip
			if(curr_inv_cnt == arr_stockQty[i]) continue;
			
			// Cube 재고수량- SC 현재수량을 차감한 수량을 적용 
			obj.setQuantity(arr_stockQty[i] - curr_inv_cnt);
			
						
			itemList.add(obj);
		}
		
		AdjustInventory ai = new AdjustInventory();
		ai.setItemList(itemList);
		
		// API 호출
		String rtnXml = ai.invoke();
		
		System.out.println("========== Retrun Message ==========");
		System.out.println(rtnXml);
		
	}
}

