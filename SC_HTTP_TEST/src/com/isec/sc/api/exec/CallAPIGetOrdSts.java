package com.isec.sc.api.exec;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;

import com.isec.sc.api.cm.ManageItem;
import com.isec.sc.api.core.util.FileContentReader;
import com.isec.sc.api.inv.GetInventorySupply;
import com.isec.sc.api.omp.CreateOrder;
import com.isec.sc.api.omp.GetOrderStatus;

public class CallAPIGetOrdSts {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		
		// Order Status 조회
		GetOrderStatus os = new GetOrderStatus();
		os.setEnterpriseCd("Matrix");
		os.setOrderNo("ORDER_TEST_3");
		String status = os.run();
		
		System.out.println("Order Status: " + status);
		
	}

}

