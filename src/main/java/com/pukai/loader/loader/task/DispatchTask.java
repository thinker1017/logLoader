package com.pukai.loader.loader.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.DateUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 文件分发任务
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class DispatchTask implements Runnable {

	private static Logger log = LogUtil.dispatchLog;

	private String filePath;

	public DispatchTask(String filePath) {
		this.filePath = filePath;
	}

	public void run() {
		try {
			createMetaFile(filePath);

			FileReader reader = new FileReader(filePath);
			BufferedReader br = new BufferedReader(reader);

			String str = null;

			Map<String, Object> writers = new HashMap<String, Object>();

			while ((str = br.readLine()) != null) {// 逐行读取待切割文件
				String arr[] = StringUtils.split(str, "\t");

				// 根据文件内容生成待写入文件所在目录名
				String folderName = "";
				if (arr.length == 7) {
					folderName = arr[5] + "_" + arr[6];
				} else {
					folderName = "trash_" + DateUtil.getToday();
				}

				File folder = new File(ConstantUtil.flumeDispatchDir + File.separator + folderName);

				str = cleanData(str);
				
				if (!folder.exists()) {
					folder.mkdirs();
				}
				
				FileWriter fw = null;
				
				if ((fw = (FileWriter) writers.get(folderName)) == null) {// 判断在writers中是否有待写入文件的句柄
					File currentFile = getLastModifiedFromFolder(folder);
					
					FileWriter writer = new FileWriter(currentFile.getAbsolutePath(), true);

					writers.put(folderName, writer);
					writers.put(folderName + "path", currentFile.getAbsolutePath());
					
					appendFile(writer, str);
				} else {
					File currentFile = new File((String) writers.get(folderName	+ "path"));

					if (currentFile.length() > ConstantUtil.dispatchFileMaxLen) {
						fw.close();
						String fileName = ConstantUtil.flumeDispatchDir
								+ File.separator + folderName + File.separator
								+ "rawdata." + System.currentTimeMillis();
						fw = new FileWriter(fileName, true);
						writers.put(folderName, fw);
						writers.put(folderName + "path", fileName);
					}
					
					appendFile(fw, str);
				}
			}

			for (Map.Entry<String, Object> entry : writers.entrySet()) {
				if( entry.getValue() instanceof FileWriter){
					((FileWriter)entry.getValue()).close();
				}
			}

			log.info("dispatch the file [" + filePath + "] success");

			br.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 尾加文件
	 * @param out
	 * @param conent
	 * @throws IOException
	 */
	private void appendFile(FileWriter out, String content) throws IOException {
		try {
			out.write(content + "\r\n");
		} finally {
			out.flush();
		}
	}

	/**
	 * 获得一个目录下最后修改的文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private File getLastModifiedFromFolder(File file) throws IOException {
		File fileList[] = file.listFiles();

		File result = null;

		if (fileList.length == 0) {
			result = new File(file.getAbsolutePath() + File.separator + "rawdata." + System.currentTimeMillis());
			result.createNewFile();
		} else {

			long lastModified = 0;

			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].getName().indexOf("meta") == -1	&& fileList[i].lastModified() > lastModified) {
					result = fileList[i];
					lastModified = fileList[i].lastModified();
				}
			}
		}

		return result;
	}

	/**
	 * 生成meta文件
	 * @param fileName
	 */
	private void createMetaFile(String fileName) {
		File file = new File(fileName);

		File metafile = new File(file.getParent() + File.separator + "." + file.getName() + ".meta");

		try {
			metafile.createNewFile();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 清洗数据，将serverid和channelid小写
	 * @param str
	 * @return
	 */
	private String cleanData(String str) {
		String result = "";

		String tmp[] = StringUtils.split(str, "\t");

		if(tmp.length == 7){
			String context = tmp[4];

			String start = StringUtils.substringBefore(str, context);
			String end = StringUtils.substringAfter(str, context);

			context = lowerIdFromContext(lowerIdFromContext(context, "serverid"), "channelid");

			context = StringUtils.replaceEach(context, ConstantUtil.special, ConstantUtil.replace);

			result = start + context + end;
		} else {
			result = StringUtils.replaceEach(str, ConstantUtil.special,	ConstantUtil.replace);
		}

		return result;
	}

	/**
	 * 将对应名字的id转为小写
	 * @param context
	 * @param name
	 * @return
	 */
	private String lowerIdFromContext(String context, String name) {
		String result = "";

		if(StringUtils.contains(context, name)){
			String id = name + "\001" + StringUtils.trim(StringUtils.substringBefore(StringUtils.substringAfter(context, name + "\001"), "\002"));

			result = StringUtils.substringBefore(context, id) + id.toLowerCase() + StringUtils.substringAfter(context, id);
		} else {
			result = context;
		}

		return result;
	}
}
