package com.pukai.loader.loader.util;

import org.apache.log4j.Logger;

/**
 * 配置文件常量工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class ConstantUtil {
	
	private static Logger log = LogUtil.systemLog;

	/**
	 * flume原始数据目录
	 */
	public final static String flumeDir = Config.newInstance().getString("flume.rawdata.dir");
	
	/**
	 * hdfs目录
	 */
	public final static String hdfsDir = Config.newInstance().getString("hdfs.dir");
	
	/**
	 * flume分发后文件目录
	 */
	public final static String flumeDispatchDir = Config.newInstance().getString("flume.dispatch.dir");
	
	/**
	 * flume原始数据归档目录
	 */
	public final static String flumeRawdataArchiveDir = Config.newInstance().getString("flume.archive.rawdata.dir");
	
	/**
	 * flume切割文件归档目录
	 */
	public final static String flumeDispatchArchiveDir = Config.newInstance().getString("flume.archive.dispatch.dir");
	
	/**
	 * hdfs备份目录
	 */
	public final static String hdfsBackupDir = Config.newInstance().getString("hdfs.backup.dir");
	
	/**
	 * 文件滚动时间
	 */
	public final static int rollInterval = getRollInterval();
	
	/**
	 * 分发写入文件最大值
	 */
	public final static long dispatchFileMaxLen = getDispatchFileMaxLen();
	
	/**
	 * 线程数
	 */
	public final static int threadNum = getThreadNum();
	
	/**
	 * 超时时间（单位：小时）
	 */
	public final static int timeout = getTimeout();
	
	/**
	 * 从今天起往前加载数据的天数
	 */
	public final static int loadForwardDays = getLoadForwardDays();
	
	/**
	 * 删除备份本地几天前的数据
	 */
	public final static int delForwardDays = getDelForwardDays();
	
	/**
	 * lzo文件后缀
	 */
	public final static String compressSuffix = Config.newInstance().getString("compress.suffix");
	
	/**
	 * lzo压缩算法类
	 */
	public final static String compressClass = Config.newInstance().getString("compress.class");
	
	/**
	 * hive连接url
	 */
	public final static String hiveURL = Config.newInstance().getString("hive.url");
	
	/**
	 * hive连接url
	 */
	public final static String hiveDriver = Config.newInstance().getString("hive.driver");
	
	/**
	 * hive添加partition
	 */
	public final static String hivePartition = Config.newInstance().getString("hive.partition.hdfs");
	
	/**
	 * hive数据库名
	 */
	public final static String hiveDatabaseName = Config.newInstance().getString("hive.database.name");
	
	/**
	 * hive表名
	 */
	public final static String hiveTablename = Config.newInstance().getString("hive.table.name");
	
	// 邮件相关配置
	public final static String mailSendUsername = Config.newInstance().getString("mail.send.username");
		
	public final static String mailSendPassword = Config.newInstance().getString("mail.send.password");
		
	public final static String[] mailReceiveList = Config.newInstance().getStringArray("mail.receive.list");
	
	public final static String[] special = {"[", "]", "\\x", "\\", "\"", "'", "NULL", "null", "%", "，", "。", "￥", "……", "、", "♥"};
	
	public final static String[] replace = {"_", "_", "_", "_", "_", "_", "_", "_", "_", "_", "_", "_", "_", "_", "_"};
	
	/**
	 * 获取文件滚动时间
	 * @return
	 */
	private static int getRollInterval() {
		int result = 600;
		
		try{
			result = Integer.parseInt(Config.newInstance().getString("rollInterval"));
		}catch (NumberFormatException e){
			log.error("rollInterval config error, use default value 600", e);
		}catch (Exception e){
			log.error(e);
		}
		
		return result;
	}
	
	/**
	 * 分发写入文件最大值
	 * @return
	 */
	private static long getDispatchFileMaxLen() {
		long result = 524288000;
		
		try{
			result = Long.parseLong(Config.newInstance().getString("dispatch.file.maxlen"));
		}catch (NumberFormatException e){
			log.error("rollInterval config error, use default value 524288000", e);
		}catch (Exception e){
			log.error(e);
		}
		
		return result;
	}

	/**
	 * 获取线程数
	 * @return
	 */
	private static int getThreadNum() {
		int result = 32;
		
		try{
			result = Integer.parseInt(Config.newInstance().getString("threadNum"));
		}catch (NumberFormatException e){
			log.error("threadNum config error, use default value 32", e);
		}catch (Exception e){
			log.error(e);
		}
		
		return result;
	}
	
	/**
	 * 获取超时时间
	 * @return
	 */
	private static int getTimeout() {
		
		int result = 1;
		
		try{
			result = Integer.parseInt(Config.newInstance().getString("timeout"));
		}catch (NumberFormatException e){
			log.error("timeout config error, use default value 1", e);
		}catch (Exception e){
			log.error(e);
		}
		
		return result;
	}
	
	/**
	 * 获取从今天起往前加载数据的天数
	 * @return
	 */
	private static int getLoadForwardDays() {
		
		int result = 3;
		
		try{
			result = Integer.parseInt(Config.newInstance().getString("loadForwardDays"));
		}catch (NumberFormatException e){
			log.error("loadForwardDays config error, use default value 3", e);
		}catch (Exception e){
			log.error(e);
		}
		
		return result;
	}

	/**
	 * 获取删除备份本地几天前数据的天数
	 * @return
	 */
	private static int getDelForwardDays() {
		
		int result = 7;
		
		try{
			result = Integer.parseInt(Config.newInstance().getString("delForwardDays"));
		}catch (NumberFormatException e){
			log.error("delForwardDays config error, use default value 7", e);
		}catch (Exception e){
			log.error(e);
		}
		
		return result;
	}
	
	/**
	 * 验证配置的有效性
	 * @return
	 */
	public static boolean validate(){
		boolean result = false;
		
		if(ValidateUtil.isValid(flumeDir) && ValidateUtil.isValid(hdfsDir) && ValidateUtil.isValid(flumeDispatchDir)
				&& ValidateUtil.isValid(flumeRawdataArchiveDir) && ValidateUtil.isValid(flumeDispatchArchiveDir)
				&& ValidateUtil.isValid(hdfsBackupDir) && ValidateUtil.isValid(compressSuffix) && ValidateUtil.isValid(compressClass)
				&& ValidateUtil.isValid(hiveURL) && ValidateUtil.isValid(hivePartition) && ValidateUtil.isValid(hiveTablename)
				&& ValidateUtil.isValid(hiveDatabaseName) ) {
			result = true;
		}
		
		return result;
	}
	
}
