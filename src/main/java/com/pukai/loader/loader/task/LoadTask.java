package com.pukai.loader.loader.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.Logger;

import com.pukai.loader.loader.hive.HiveService;
import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 上传文件到hdfs任务
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class LoadTask implements Runnable {
	
	private static Logger log = LogUtil.loaderLog;

	private String filePath;
	
	public LoadTask(String filePath){
		this.filePath = filePath;
	}
	
	public void run() {
		String hdfsLzoFilePath = null;
		try {
			FileSystem hdfs = FileSystem.get(new Configuration());
			
			File localFile = new File(filePath);
			
//			hdfsLzoFilePath = filePath.replace(ConstantUtil.flumeDispatchDir, ConstantUtil.hdfsDir) + ConstantUtil.compressSuffix;
			hdfsLzoFilePath = filePath.replace(ConstantUtil.flumeDispatchDir, ConstantUtil.hdfsDir);
			
//			Class<?> codecClass = Class.forName(ConstantUtil.compressClass);
//			CompressionCodec codec = (CompressionCodec)ReflectionUtils.newInstance(codecClass, new Configuration());
			
			//获得hdfs的文件输出流
			FSDataOutputStream os = hdfs.create(new Path(hdfsLzoFilePath), true);
//			CompressionOutputStream out = codec.createOutputStream(os);
			
			//获得本地文件的文件输入流
			FileInputStream fis = new FileInputStream(localFile);
			
			//将本地文件拷贝至hdfs并用lzo压缩算法压缩
//			IOUtils.copyBytes(fis, out, 4096, true);
			IOUtils.copyBytes(fis, os, 4096, true);
			
			if(hdfs.exists(new Path(hdfsLzoFilePath))){
				log.info("load local file SUCCESS from: [" + filePath + "] to: [" + hdfsLzoFilePath + "]");
				
				//将本地文件大小写入meta文件中
				File metafile = new File(localFile.getParent() + File.separator + "." + localFile.getName() + ".meta");
				fileWrite(metafile, localFile.length() + "");
				
				// 添加hive partition
				String args[] = localFile.getParentFile().getName().split("_");
				HiveService.addPartition(args[0], args[1]);
				log.info("partition : [ appkey = " + args[0] + ", ds = " + args[1] + "]");
				
			}else{//文件拷贝结束但文件在hdfs上不存在
				log.error("can't find the file [" + hdfsLzoFilePath + "] in hdfs");
			}
			
		} catch (IOException e) {
			log.error("load local file FAILD from: [" + filePath + "] to: [" + hdfsLzoFilePath + "]", e);
			return ;
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * 将内容写入文件中
	 * @param file
	 * @param str
	 */
	private static void fileWrite(File file, String str){
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(str);
			writer.close();
		} catch (IOException e) {
			log.error("local meta file write error", e);
		}
	}

}
