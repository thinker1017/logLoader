package com.pukai.loader.loader.scan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.DateUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 原始文件归档遍历工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class RawdataArchiveScan {
	
	/**
	 * 原始文件归档遍历方法，用于每日的常规删除与备份
	 * @param dir
	 * @return
	 */
	public static List<String> execute(String dir){
		Logger log = LogUtil.archiveLog;
		File root = new File(dir);
		if(!root.exists()){
			return null;
		}
		
		List<String> dirs = new ArrayList<String>();
		
		File[] files = root.listFiles();
		for(File file : files){
			if(file.isDirectory()){
				if(file.listFiles().length == 0){
					dirs.add(file.getAbsolutePath());
				} else {
					dirs.addAll(execute(file.getAbsolutePath()));
				}
			} else if (file.isFile()) {
				if(file.getName().indexOf("meta") == -1){
					File metafile = new File(file.getParent() + File.separator + "." + file.getName() + ".meta");
					
					Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
					Matcher matcher = pattern.matcher(file.getName());
					
					if(metafile.exists() && matcher.find()){// 元数据文件存在且文件中包含日期
						
						if(DateUtil.isBeforeDelDate(matcher.group())){// 文件最后修改时间在待删除范围内
							dirs.add(file.getAbsolutePath());
							log.info("archive rawdata file [" + file.getAbsolutePath() + "]");
						}
					}
				}
			}
		}
		
		return dirs;
	}
	
}
