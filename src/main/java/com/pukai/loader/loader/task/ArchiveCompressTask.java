package com.pukai.loader.loader.task;

import java.io.File;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.FileUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 目录压缩任务
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class ArchiveCompressTask implements Runnable {

	private static Logger log = LogUtil.archiveLog;

	private String dir;
	
	public ArchiveCompressTask(String dir){
		this.dir = dir;
	}
	
	public void run() {
		File tarfile = new File(dir + ".tar");
		
		FileUtil.tar(new File(dir), tarfile);
		
		FileUtil.gzip(tarfile);
		
		FileUtil.DeleteFolder(dir);
		log.info("delete dir [" + dir + "]");
		
		tarfile.delete();
		log.info("delete tar file [" + tarfile.getAbsolutePath() + "]");
	}
}
