package com.increff.pos.util;

import java.util.List;
import java.util.UUID;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String toLowerCase(String s) {
		return s == null ? null : s.trim().toLowerCase();
	}

	public static String generateBarCode(){
		String barcode="",helper="0123456789qwertyuiopasdfghjklzxcvbnm";
		for(int index=0;index<8;index++){
			barcode+=helper.charAt((int)(helper.length()*Math.random()));
		}
		return barcode;
	}

	public static String generateOrderCode() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static <T> T getSingle(List<T> list) {
		return list.stream().findFirst().orElse(null);
	}
}
