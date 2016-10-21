package com.pukai.loader.loader.main;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.pukai.loader.loader.exception.SqlException;
import com.pukai.loader.loader.mail.SendMailClient;
import com.pukai.loader.loader.scheduler.Scheduler;
import com.pukai.loader.loader.schedulerfactory.SchedulerFactory;
import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.LogUtil;
import com.pukai.loader.loader.util.ValidateUtil;

/**
 * 程序入口
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class Main {
	
	private static Set<Exception> exceptions = new HashSet<Exception>();
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("./conf/log4j.properties");
		
		Logger log = LogUtil.systemLog;
		
		if (!ConstantUtil.validate()) {
			log.error("conf.properties config error, please check!");
			return ;
		}
		
		if (args.length == 0) {// 分发文件并将本地数据load至hdfs
			try {
				Scheduler dispatchScheduler = SchedulerFactory.create(SchedulerFactory.DISPATCH);
				
				if (ValidateUtil.isValid(dispatchScheduler)) {
					dispatchScheduler.execute();
				} else {
					log.error("get scheduler error, program is going to stop!");
				}
				
				Scheduler loadScheduler = SchedulerFactory.create(SchedulerFactory.LOAD);
				
				if (ValidateUtil.isValid(loadScheduler)) {
					loadScheduler.execute();
				} else {
					log.error("get scheduler error, program is going to stop!");
				}
			} catch (SqlException e) {
				if (!exceptions.contains(e)) {
					exceptions.add(e);
					StringBuilder sb = new StringBuilder();
			    	sb.append(e.toString()).append("\n");
			        for (StackTraceElement elem : e.getStackTrace()) {
			        	sb.append("at ").append(elem).append("\n");
			        }
			        SendMailClient.sendMail(sb.toString());
				}
			}
		} else if (args[0].equals("-load")) {// 本地数据load至hdfs
			
			Scheduler scheduler = SchedulerFactory.create(SchedulerFactory.LOAD);
			
			if (ValidateUtil.isValid(scheduler)) {
				scheduler.execute();
			} else {
				log.error("get scheduler error, program is going to stop!");
			}
		} else if (args[0].equals("-dispatch")) {// 分发文件
			
			Scheduler scheduler = SchedulerFactory.create(SchedulerFactory.DISPATCH);
			
			if (ValidateUtil.isValid(scheduler)) {
				scheduler.execute();
			} else {
				log.error("get scheduler error, program is going to stop!");
			}
		} else if (args[0].equals("-archive")) {// 归档
			
			Scheduler scheduler = SchedulerFactory.create(SchedulerFactory.ARCHIVE);
			
			if (ValidateUtil.isValid(scheduler)) {
				scheduler.execute();
			} else {
				log.error("get scheduler error, program is going to stop!");
			}
		} else {
			System.out.println("Command params error!");
			System.out.println("-dispatch\tdispatch rawdata");
			System.out.println("-load\tload the dispatched file to hdfs");
			System.out.println("-archive\tarchive local file");
		}
	}
}
