package com.pukai.loader.loader.schedulerfactory;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.proxy.SchedulerTimeLogProxy;
import com.pukai.loader.loader.scheduler.Scheduler;
import com.pukai.loader.loader.util.LogUtil;
import com.pukai.loader.loader.util.StringUtil;

/**
 * Scheduler工厂类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class SchedulerFactory {
	public static final String LOAD = "load";
	public static final String ARCHIVE = "archive";
	public static final String DISPATCH = "dispatch";
	
	public static Scheduler create(String str) {
		Logger log = LogUtil.systemLog;
		
		SchedulerTimeLogProxy proxy = new SchedulerTimeLogProxy();
		
		Scheduler result = null;
		
		try {
			Scheduler scheduler = (Scheduler)Class.forName(getSchedulerFullName(str)).newInstance();
			
			result = (Scheduler)proxy.bind(scheduler);
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	private static String getSchedulerFullName(String str) {
		return Scheduler.class.getPackage().getName() + "." + StringUtil.toUpperCaseInitial(str) + Scheduler.class.getSimpleName();
	}
}
