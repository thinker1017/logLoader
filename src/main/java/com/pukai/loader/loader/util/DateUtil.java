package com.pukai.loader.loader.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 日期工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class DateUtil {
	
	private static Logger log = LogUtil.systemLog;
	
	private static int loadForwardDays = ConstantUtil.loadForwardDays;
	
	private static int delForwardDays = ConstantUtil.delForwardDays;
	
	/**
	 * 判断一个日期字符串是否在待删除日期之前
	 * @param dateStr
	 * @return
	 */
	public static boolean isBeforeDelDate(String dateStr) {
		Date delDate = null;
		Date fileDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, - delForwardDays);
		delDate = c.getTime();
		
		try {
			fileDate = sdf.parse(dateStr);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		
		return fileDate.before(delDate);
	}

	/**
	 * 判断一个目录路径的日期是否在日期范围内
	 * @param filePath
	 * @return
	 */
	public static boolean isInDateRange(String filePath) {
		boolean result = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date now = null;
		try {
			now = sdf.parse(filePath);
			sdf = null;
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			result = false;
		}
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, - (loadForwardDays));
		Date start = c.getTime();
		
		c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 0);
		Date end = c.getTime();
		
		c = null;
		
		return result ? now.after(start) && now.before(end) : result;
	}
	
	/**
	 * 获得今天的日期
	 * @return
	 */
	public static String getToday() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
}
