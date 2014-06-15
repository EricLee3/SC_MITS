package com.isec.sc.api.exec;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;

import com.isec.sc.api.cm.ManageItem;
import com.isec.sc.api.core.util.FileContentReader;
import com.isec.sc.api.inv.GetInventorySupply;
import com.isec.sc.api.omp.CreateOrder;
import com.isec.sc.api.omp.GetOrderStatus;

public class CallAPIGetInv {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		// 아이템 재고정보 조회
		GetInventorySupply is = new GetInventorySupply();
		is.setItem("GRA122A010BLML");
		
		
		int result = is.run();
		System.out.println(result);

	}
}

