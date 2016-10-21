package com.pukai.loader.loader.scheduler;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.scan.DispatchArchiveScan;
import com.pukai.loader.loader.scan.RawdataArchiveScan;
import com.pukai.loader.loader.task.ArchiveCompressTask;
import com.pukai.loader.loader.task.BackupUploadTask;
import com.pukai.loader.loader.task.DispatchArchiveMoveTask;
import com.pukai.loader.loader.task.RawdataArchiveMoveTask;
import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.LogUtil;
import com.pukai.loader.loader.util.ValidateUtil;

/**
 * 文件归档调度类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class ArchiveScheduler implements Scheduler {
	
	/**
	 * 将本地文件移动至备份目录做备份
	 */
	public void execute(){
		Logger log = LogUtil.archiveLog;
		
		// ---------------------------------原始文件归档--------------------------------
		Set<String> rawdataDirs = new ConcurrentSkipListSet<String>();
		
		List<String> rawdataFileNames = RawdataArchiveScan.execute(ConstantUtil.flumeDir);
		
		log.info("scan rawdata dir finish");
		
		if(!ValidateUtil.isValid(rawdataFileNames)){
			log.info("there is no more rawdata files need to archive");
		} else {
			// 运行移动归档文件线程
			ExecutorService rawdataMoveExec = Executors.newFixedThreadPool(ConstantUtil.threadNum);
			
			for(String fileName : rawdataFileNames){
				rawdataMoveExec.execute(new RawdataArchiveMoveTask(fileName, rawdataDirs));
			}
			
			rawdataMoveExec.shutdown();
			try {
				rawdataMoveExec.awaitTermination(ConstantUtil.timeout, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				log.error(e);
			}
			
			// 运行压缩归档目录线程
			ExecutorService rawdataCompressExec = Executors.newFixedThreadPool(ConstantUtil.threadNum);
			
			for(String dir : rawdataDirs){
				rawdataCompressExec.execute(new ArchiveCompressTask(dir));
			}
			
			rawdataCompressExec.shutdown();
			
			try {
				rawdataCompressExec.awaitTermination(ConstantUtil.timeout, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
		
		// ------------------------------上传归档文件至hdfs-----------------------------
		File rawdataArchiveDir = new File(ConstantUtil.flumeRawdataArchiveDir);
		
		// 运行上传归档文件至hdfs线程
		ExecutorService backupUploadExec = Executors.newFixedThreadPool(ConstantUtil.threadNum);
		
		for(File file : rawdataArchiveDir.listFiles()){
			backupUploadExec.execute(new BackupUploadTask(file.getAbsolutePath()));
		}
		
		backupUploadExec.shutdown();
		
		try {
			backupUploadExec.awaitTermination(ConstantUtil.timeout, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			log.error(e);
		}
		
		// ---------------------------------切割文件归档--------------------------------
		Set<String> dispatchDirs = new ConcurrentSkipListSet<String>();
		
		List<String> dispatchDirNames = DispatchArchiveScan.execute(ConstantUtil.flumeDispatchDir);
		
		log.info("scan dispatch dir finish");
		
		if(!ValidateUtil.isValid(dispatchDirNames)){
			log.info("there is no more dispatch files need to archive");
		} else {
			// 运行移动归档文件线程
			ExecutorService dispatchMoveExec = Executors.newFixedThreadPool(ConstantUtil.threadNum);
			
			for(String dir : dispatchDirNames){
				dispatchMoveExec.execute(new DispatchArchiveMoveTask(dir, dispatchDirs));
			}
			
			dispatchMoveExec.shutdown();
			try {
				dispatchMoveExec.awaitTermination(ConstantUtil.timeout, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				log.error(e);
			}
			
			// 运行压缩归档目录线程
			ExecutorService dispatchCompressExec = Executors.newFixedThreadPool(ConstantUtil.threadNum);
			
			for(String dir : dispatchDirs){
				dispatchCompressExec.execute(new ArchiveCompressTask(dir));
			}
			
			dispatchCompressExec.shutdown();
			
			try {
				dispatchCompressExec.awaitTermination(ConstantUtil.timeout, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}
}
