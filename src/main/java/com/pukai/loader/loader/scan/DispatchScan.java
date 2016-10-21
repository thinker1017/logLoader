package com.pukai.loader.loader.scan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.LogUtil;

/**
 * 待分发文件遍历工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class DispatchScan {

	/**
	 * 待分发文件遍历
	 * @param dir
	 * @return
	 */
	public static List<String> execute(String dir){
		
		Logger log = LogUtil.dispatchLog;
		
		File root = new File(dir);
		if(!root.exists()){
			log.error("the dir: [" + dir + "] is not exist!");
			return null;
		}
		
		List<String> dirs = new ArrayList<String>();
		
		File[] files = root.listFiles();
		
		for(File file : files){
			if(file.isDirectory()){
				dirs.addAll(execute(file.getAbsolutePath()));
			} else if(file.isFile()){
				if(file.getName().indexOf("meta") == -1){
					File metafile = new File(file.getParent() + File.separator + "." + file.getName() + ".meta");
					
					if(!metafile.exists()){// 没生成meta文件的文件
						
						if(file.length() == 0){
							file.delete();
						} else{
							log.info("current file : " + file.getAbsolutePath());
							dirs.add(file.getAbsolutePath());
						}
					}
				}
			}
		}
		
		return dirs;
	}
}
