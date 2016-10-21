package com.pukai.loader.loader.util;

import java.util.Collection;

/**
 * 校验工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class ValidateUtil {
	/**
	 * 判断string是否有效
	 */
	public static boolean isValid(String str){
		if(str == null || "".equals(str.trim())){
			return false ;
		}
		return true ;
	}
	
	/**
	 * 判断集合的有效性
	 */
	public static boolean isValid(Collection col){
		if(col == null || col.isEmpty()){
			return false ;
		}
		return true ;
	}
	
	/**
	 * 判断数组有效性
	 */
	public static boolean isValid(Object[] arr){
		if(arr == null || arr.length == 0){
			return false ;
		}
		return true ;
	}
	
	/**
	 * 判断对象有效性
	 * @param obj
	 * @return
	 */
	public static boolean isValid(Object obj){
		if(obj == null ){
			return false ;
		}
		return true ;
	}
}
