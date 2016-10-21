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
 * 已切割文件按时删除备份任务
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class DispatchArchiveMoveTask implements Runnable {

	private static Logger log = LogUtil.archiveLog;

	private String filePath;
	private Set<String> dirs;
	
	public DispatchArchiveMoveTask(String filePath, Set<String> dirs){
		this.filePath = filePath;
		this.dirs = dirs;
	}
	
	public void run() {
		
		File file = new File(filePath);
		if(file.isDirectory()){
			if(file.listFiles().length < 1){
				file.delete();
			} else {
				// /data/collect-data/total/archive/dispatch目录
				File dispatchArchiveDir = new File(ConstantUtil.flumeDispatchArchiveDir);
				
				Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
				
				Matcher matcher = pattern.matcher(file.getName());
				
				if(matcher.find()){// 目录名中包含日期
					
					String dataStr = matcher.group();
					
					// 获得/data/collect-data/total/archive/dispatch目录下形如dispatch.yyyy-MM-dd_x.tar.gz的文件个数
					int num = getFileNum(dispatchArchiveDir, dataStr);
					
					// 创建形如/data/collect-data/total/archive/dispatch/dispatch.yyyy-MM-dd_x的目录
					File tmpfile = new File(ConstantUtil.flumeDispatchArchiveDir + File.separator + "dispatch." + dataStr + "_" + (num + 1));
					if(!tmpfile.exists()){
						tmpfile.mkdirs();
					}
					
					// 将xxxxxx_yyyy-MM-dd目录拷贝至/data/collect-data/total/archive/dispatch/dispatch.yyyy-MM-dd_x目录
					FileUtil.copyDir2Dir(file.getAbsolutePath(), tmpfile.getAbsolutePath());
					log.info("copy dir from [" + file.getAbsolutePath() + "] to [" + tmpfile.getAbsolutePath() + "]");
					
					dirs.add(tmpfile.getAbsolutePath());
					
					FileUtil.DeleteFolder(file.getAbsolutePath());
					log.info("delete local dir [" + file.getAbsolutePath() + "]");
				}
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
