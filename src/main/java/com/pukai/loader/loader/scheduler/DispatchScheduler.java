package com.pukai.loader.loader.scheduler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.scan.DispatchScan;
import com.pukai.loader.loader.task.DispatchTask;
import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.LogUtil;
import com.pukai.loader.loader.util.ValidateUtil;

/**
 * 文件分发调度类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class DispatchScheduler implements Scheduler {

	public void execute() {
		Logger log = LogUtil.dispatchLog;
		
		List<String> fileNames = DispatchScan.execute(ConstantUtil.flumeDir);
		
		log.info("scan dispatch file finish");
		
		if(!ValidateUtil.isValid(fileNames)){
			log.info("no more new files needs dispatch!");
			return ;
		}
		
		ExecutorService exec = Executors.newFixedThreadPool(ConstantUtil.threadNum);
		
		for(String fileName : fileNames){
			exec.execute(new DispatchTask(fileName));
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
