package com.pukai.loader.loader.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;


/**
 * conf.properties配置文件读取类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class Config {
	
	private static Logger log = LogUtil.systemLog;
	
	private static Configuration configuration = null;
	
	private static Config config = null;

	static{
		try {
			configuration = new PropertiesConfiguration("./conf/conf.properties");
		} catch (ConfigurationException e) {
			log.error("file not found:conf.properties", e);
		}
	}
	
	private Config(){
		
	}
	
	public static Config newInstance(){
		if(null == config){
			config = new Config();
		}
		return config;
	}
	
	public String[] getStringArray(String key){
		return configuration.getStringArray(key);
	}
	
	public String getString(String key){
		return configuration.getString(key, "");
	}
	
	public String getString(String key, String defaultValue){
		return "".equals(configuration.getString(key, defaultValue)) ? defaultValue : configuration.getString(key, defaultValue);
	}
	
}
