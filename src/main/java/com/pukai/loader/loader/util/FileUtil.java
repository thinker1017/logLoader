package com.pukai.loader.loader.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.log4j.Logger;

/**
 * 文件工具类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class FileUtil {

	private static Logger log = LogUtil.archiveLog;

	/**
	 * gzip 压缩，跟源文件在相同目录中生成.gz文件
	 * @param source 源文件
	 */
	public static void gzip(File source) {
		log.info("start compress the sourse file [" + source.getName() + "]");
		String sourcePath = source.getAbsolutePath();
		int lastIndexOf = sourcePath.lastIndexOf(File.separator);
		String dir = sourcePath.substring(0, lastIndexOf);
		File target = new File(dir + File.separator + source.getName() + ".gz");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		GZIPOutputStream gzipOS = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			gzipOS = new GZIPOutputStream(fos);
			int count = -1;
			byte[] buffer = new byte[1024];
			while ((count = fis.read(buffer, 0, buffer.length)) != -1) {
				gzipOS.write(buffer, 0, count);
			}
			gzipOS.flush();
			gzipOS.close();
			log.info("compress SUCCESS from source file [" + source.getName() + "] to .gz file [" + target.getName() + "]");
		} catch (Exception e) {
			log.error("compress FAILD from source file [" + source.getName() + "] to .gz file [" + target.getName() + "]", e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (gzipOS != null) {
					gzipOS.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * tar 打包
	 * @param source
	 * @param dest
	 */
	public static void tar(File source, File dest) {
		log.info("start tar the source file [" + source.getName() + "]");
		FileOutputStream out = null;
		TarArchiveOutputStream tarOut = null;
		
		try {
			out = new FileOutputStream(dest);
			tarOut = new TarArchiveOutputStream(out);
			// 解决文件名过长
			tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			tarPack(source, tarOut, "");
			tarOut.flush();
			tarOut.close();
			log.info("tar SUCCESS from source file [" + source.getName() +  "] to tar file : [" + dest.getName() + "]");
			
		} catch (Exception e) {
			log.error("tar FAILD from source file [" + source.getName() +  "] to tar file : [" + dest.getName() + "]", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (tarOut != null) {
					tarOut.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 归档
	 * @param source 源文件或者目录
	 * @param tarOut 归档流
	 * @param parentPath 归档后的目录或者文件路径
	 */
	private static void tarPack(File source, TarArchiveOutputStream tarOut, String parentPath) {
		if (source.isDirectory()) {
			tarDir(source, tarOut, parentPath);
		} else if (source.isFile()) {
			tarFile(source, tarOut, parentPath);
		}
	}

	/**
	 * 归档文件(非目录)
	 * @param source 源文件
	 * @param tarOut 归档流
	 * @param parentPath 归档后的路径
	 */
	private static void tarFile(File source, TarArchiveOutputStream tarOut, String parentPath) {
		TarArchiveEntry entry = new TarArchiveEntry(parentPath
				+ source.getName());
		BufferedInputStream bis = null;
		FileInputStream fis = null;
		try {
			entry.setSize(source.length());
			tarOut.putArchiveEntry(entry);
			fis = new FileInputStream(source);
			bis = new BufferedInputStream(fis);
			int count = -1;
			byte[] buffer = new byte[1024];
			while ((count = bis.read(buffer, 0, 1024)) != -1) {
				tarOut.write(buffer, 0, count);
			}
			bis.close();
			tarOut.closeArchiveEntry();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 归档目录
	 * @param sourceDir 原目录
	 * @param tarOut 归档流
	 * @param parentPath 归档后的父目录
	 */
	private static void tarDir(File sourceDir, TarArchiveOutputStream tarOut,
			String parentPath) {
		// 归档空目录
		if (sourceDir.listFiles().length < 1) {
			TarArchiveEntry entry = new TarArchiveEntry(parentPath
					+ sourceDir.getName() + File.separator);
			try {
				tarOut.putArchiveEntry(entry);
				tarOut.closeArchiveEntry();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		// 递归 归档
		for (File file : sourceDir.listFiles()) {
			tarPack(file, tarOut, parentPath + sourceDir.getName() + File.separator);
		}
	}
	
	/**
	 * 复制目录到目标目录下
	 * @param sourceDir
	 * @param targetDir
	 */
	public static void copyDir2Dir(String sourceDir, String targetDir) {
		File source = new File(sourceDir);
		copyDirFiles(sourceDir, targetDir + File.separator + source.getName());
	}
	
	/**
	 * 复制目录下的文件或目录到目标目录
	 * @param sourceDir
	 * @param targetDir
	 */
	private static void copyDirFiles(String sourceDir, String targetDir) {
		// 新建目标目录
		File target = new File(targetDir);
		target.mkdirs();
		
		File source = new File(sourceDir);
		// 获取源文件夹当前下的文件或目录
		File[] file = source.listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(target.getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
				sourceFile = null;
				targetFile = null;
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + File.separator + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + File.separator + file[i].getName();
				copyDirFiles(dir1, dir2);
			}
		}
		target = null;
		source = null;
	}
	
	/**
	 * 复制文件
	 * @param sourceFile
	 * @param targetFile
	 */
    public static void copyFile(File sourceFile, File targetFile) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
            // 关闭流
            if (inBuff != null)
				try {
					inBuff.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
            if (outBuff != null)
				try {
					outBuff.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
        }
    }
    
    /**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * @param sPath 要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}
    
	/**
	 * 删除单个文件
	 * @param sPath 被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	private static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
    
	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * @param sPath 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	private static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
}
