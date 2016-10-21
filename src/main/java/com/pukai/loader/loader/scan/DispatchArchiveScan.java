package com.pukai.loader.loader.scan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.DateUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 已切割文件归档遍历工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class DispatchArchiveScan {
	
	private static final Logger log = LogUtil.archiveLog;
	
	/**
	 * 文件归档遍历方法，用于每日的常规删除与备份
	 * @param dir
	 * @return
	 */
	public static List<String> execute(String dir){
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
					Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
					Matcher matcher = pattern.matcher(file.getAbsolutePath());
					
					if(matcher.find() && DateUtil.isBeforeDelDate(matcher.group()) && whetherDel(file)){
						// 目录中包含日期  && 日期在待删除日期之前  && 目录可归档
						log.info("current dir : " + file.getAbsolutePath());
						// 将此目录加入目录列表
						dirs.add(file.getAbsolutePath());
					}
				}
			}
		}
		
		return dirs;
	}
	
	/**
	 * 判断此目录是否可归档
	 * @param dir
	 * @return
	 */
	private static boolean whetherDel(File dir){
		boolean result = true;
		
		File[] files = dir.listFiles();
		
		for(File file : files){
			if(file.getName().indexOf(".meta") < 0){
				File metafile = new File(file.getParent() + File.separator + "." + file.getName() + ".meta");
				
				if(metafile.exists()){// 元数据文件存在，读取内容并比较大小
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(metafile));
						if(Long.parseLong(reader.readLine()) < file.length()){
							result = false;
						}
					} catch (Exception e) {
						log.error(e);
						result = false;
					}
				} else {
					result = false;
				}
			}
		}
		
		return result;
	}
}
