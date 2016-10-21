package com.pukai.loader.loader.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.FileUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 上传归档文件到hdfs任务
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class BackupUploadTask implements Runnable {

	private static Logger log = LogUtil.archiveLog;

	private String filePath;
	
	public BackupUploadTask(String filePath){
		this.filePath = filePath;
	}
	
	public void run() {
		File file = new File(filePath);
		if(!file.exists()){
			log.info("file " + filePath + " is not exists!");
			return ;
		}
		
		try {
			if(file.isFile()){
				FileSystem hdfs = FileSystem.get(new Configuration());
				
				File localFile = new File(filePath);
				Path hdfsFile = new Path(ConstantUtil.hdfsBackupDir + File.separator + localFile.getName());
				
				//获得hdfs的文件输出流
				FSDataOutputStream os = hdfs.create(hdfsFile, true);
				
				//获得本地文件的文件输入流
				FileInputStream fis = new FileInputStream(localFile);
				
				//将本地文件拷贝至hdfs
				IOUtils.copyBytes(fis, os, 4096, true);
				log.info("Upload local file [" + localFile.getAbsolutePath() + "] to hdfs [" + hdfsFile.getName() + "]");
				
				if(hdfs.exists(hdfsFile) && hdfs.getFileStatus(hdfsFile).getLen() == localFile.length()){
					localFile.delete();
					log.info("Delete local file [" + localFile.getAbsolutePath() + "]");
				}
			} else {
				log.info(filePath + " is not a file, tar -czvf it!");
				
				File tarfile = new File(file.getAbsolutePath() + ".tar");
				
				FileUtil.tar(new File(file.getAbsolutePath()), tarfile);
				
				FileUtil.gzip(tarfile);
				
				FileUtil.DeleteFolder(file.getAbsolutePath());
				log.info("delete dir [" + file.getAbsolutePath() + "]");
				
				tarfile.delete();
				log.info("delete tar file [" + tarfile.getAbsolutePath() + "]");
			}
		} catch (IOException e) {
			log.error(e);
		}
	}
}
