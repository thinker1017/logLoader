package com.pukai.loader.loader.task;

import java.io.File;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.FileUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 原始文件按时删除备份任务
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class RawdataArchiveMoveTask implements Runnable {

	private static Logger log = LogUtil.archiveLog;

	private String filePath;
	private Set<String> dirs;
	
	public RawdataArchiveMoveTask(String filePath, Set<String> dirs){
		this.filePath = filePath;
		this.dirs = dirs;
	}
	
	public void run() {
		File file = new File(filePath);
		if(file.isDirectory() && file.listFiles().length < 1){
			file.delete();
		} else if(file.isFile()) {
			// /data/collect-data/total/archive/rawdata目录
			File rawdataArchivedir = new File(ConstantUtil.flumeRawdataArchiveDir);
			
			Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
			
			Matcher matcher = pattern.matcher(file.getName());
			
			File metafile = new File(file.getParent() + File.separator + "." + file.getName() + ".meta");
			
			if(metafile.exists() && matcher.find()){// 元数据文件存在且文件中包含日期
				
				String dataStr = matcher.group();
				
				// 获得/data/collect-data/total/archive/rawdata目录下形如rawdata.yyyy-MM-dd_x.tar.gz的文件个数
				int num = getFileNum(rawdataArchivedir, dataStr);
				
				// 创建形如/data/collect-data/total/archive/rawdata/rawdata.yyyy-MM-dd_x的目录
				File tmpfile = new File(ConstantUtil.flumeRawdataArchiveDir + File.separator + "rawdata." + dataStr + "_" + (num + 1));
				if(!tmpfile.exists()){
					tmpfile.mkdirs();
					log.info("mkdir [" + tmpfile.getAbsolutePath() + "]");
				}
				
				// 将待归档文件移动到/data/collect-data/total/archive/rawdata/rawdata.yyyy-MM-dd_x目录下
				FileUtil.copyFile(file, new File(tmpfile.getAbsolutePath() + File.separator + file.getName()));
				file.delete();
				
				log.info("move file [" + file.getAbsolutePath() + "] to dir [" + tmpfile.getAbsolutePath() + "]");
				
				// 删除meta文件
				metafile.delete();
				
				dirs.add(tmpfile.getAbsolutePath());
			}
		}
	}
	
	/**
	 * 获得一个目录下包含某个字符串的文件个数
	 * @param file
	 * @param path
	 * @return
	 */
	private int getFileNum(File file, String path){
		if(!file.exists()){
			return 0;
		}
		
		int result = 0;
		for(File f : file.listFiles()){
			if(f.isFile() && StringUtils.contains(f.getName(), path)){
				result ++;
			}
		}
		return result;
	}
}
