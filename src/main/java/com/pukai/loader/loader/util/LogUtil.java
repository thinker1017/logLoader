package com.pukai.loader.loader.util;

import org.apache.log4j.Logger;

/**
 * 日志工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class LogUtil {

	public final static Logger systemLog = Logger.getLogger("system");
	
	public final static Logger timeLog = Logger.getLogger("time");
	
	public final static Logger loaderLog = Logger.getLogger("load");
	
	public final static Logger archiveLog = Logger.getLogger("archive");
	
	public final static Logger dispatchLog = Logger.getLogger("dispatch");
	
	public static Logger getLogger(String str) {
		return Logger.getLogger(str);
	}
}
