package com.pukai.loader.loader.scan;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.DateUtil;
import com.pukai.loader.loader.util.FileUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 本地文件上传至hdfs遍历工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class LoadScan {
	
	/**
	 * 遍历本地目录文件并返回待上传文件列表
	 * @param dir 本地目录
	 * @return
	 */
	public static List<String> execute(String dir){
		Logger log = LogUtil.loaderLog;
		Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
		
		File root = new File(dir);
		if(!root.exists()){
			return null;
		}
		
		List<String> dirs = new ArrayList<String>();
		
		File[] files = root.listFiles();
		for(File file : files){
			if(file.isDirectory()){
				Matcher matcher = datePattern.matcher(file.getAbsolutePath());
				if(matcher.find() && DateUtil.isInDateRange(matcher.group()) ){//目录中包含日期并且在指定时间范围内
					log.info("current dir : " + file.getAbsolutePath());
					dirs.addAll(execute(file.getAbsolutePath()));
				}else{
					dirs.addAll(execute(file.getAbsolutePath()));
				}
				matcher = null;
			}else if(file.isFile()){
				if(file.getName().indexOf("meta") == -1){
					
					File metafile = new File(file.getParent() + File.separator + "." + file.getName() + ".meta");
					
					if(metafile.exists()){//元数据文件存在，读取内容并比较大小
						LineNumberReader reader = null;
						String str = "";
						try {
							reader = new LineNumberReader(new FileReader(metafile));
							
							if(Long.parseLong(str = reader.readLine()) != file.length()){
								dirs.add(file.getAbsolutePath());
							}
							reader.close();
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							log.error("metafile : [" + metafile.getAbsolutePath() + "], " + str);
							FileUtil.copyFile(metafile, new File(metafile.getAbsolutePath() + ".bak"));
							dirs.add(file.getAbsolutePath());
						}
						
					}else{//元数据文件不存在，将此文件添加至hdfs拷贝列表
						dirs.add(file.getAbsolutePath());
					}
				}
			}
		}
		
		return dirs;
	}
}
