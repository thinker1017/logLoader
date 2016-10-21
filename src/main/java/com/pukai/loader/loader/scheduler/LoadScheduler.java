package com.pukai.loader.loader.scheduler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.scan.LoadScan;
import com.pukai.loader.loader.task.LoadTask;
import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.LogUtil;
import com.pukai.loader.loader.util.ValidateUtil;

/**
 * 本地文件上传至hdfs调度类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class LoadScheduler implements Scheduler {
	
	/**
	 * 上传本地文件到hdfs
	 */
	public void execute() {
		Logger log = LogUtil.loaderLog;
		
		String flumeDispatchDir = ConstantUtil.flumeDispatchDir;
		
		List<String> fileNames = LoadScan.execute(flumeDispatchDir);
		
		log.info("scan metric : " + flumeDispatchDir + " finish");
		
		if(!ValidateUtil.isValid(fileNames)){
			log.info("no more new file in " + flumeDispatchDir + " or " + flumeDispatchDir + " is not exist!");
			return ;
		}
		
		ExecutorService exec = Executors.newFixedThreadPool(ConstantUtil.threadNum);
		
		for(String fileName : fileNames){
			exec.execute(new LoadTask(fileName));
		}
		exec.shutdown();
		try {
			exec.awaitTermination(ConstantUtil.timeout, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			log.error(e);
			return ;
		}
	}
}
