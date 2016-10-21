package com.pukai.loader.loader.util;

public class StringUtil {

	/**
	 * 首字母大写
	 * 
	 * @author thinker
	 * @date 2015-3-24
	 * @version V1.0
	 */
	public static String toUpperCaseInitial(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(
					Character.toUpperCase(s.charAt(0))).append(s.substring(1))
					.toString();
	}
}
