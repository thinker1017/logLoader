package com.pukai.loader.loader.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.exception.SqlException;
import com.pukai.loader.loader.util.ConstantUtil;
import com.pukai.loader.loader.util.LogUtil;

/**
 * 
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class HiveService {
	
	private static Logger log = LogUtil.systemLog;
	
	private static final String URLHIVE = ConstantUtil.hiveURL;
	
	private static final String DATABASE_NAME = ConstantUtil.hiveDatabaseName;
	private static final String TABLE_NAME = ConstantUtil.hiveTablename;
	
	public static void addPartition(String appkey, String ds) {
		Connection connection = null;
		PreparedStatement pstm = null;
		
		try {
			Class.forName(ConstantUtil.hiveDriver);
			
			// update--Author:ruijie Date:2015-06-05 for: 去掉username与password参数 --------
			connection = DriverManager.getConnection(URLHIVE);
			
			pstm = connection.prepareStatement("use " + DATABASE_NAME);
			pstm.execute();
			
			pstm = connection.prepareStatement("ALTER TABLE " + TABLE_NAME + " ADD IF NOT EXISTS PARTITION(appid=?,ds=?) LOCATION '" + ConstantUtil.hivePartition + "/" + appkey +"_" + ds +"'");
			pstm.setString(1, appkey);
			pstm.setString(2, ds);
			pstm.execute();
			
			pstm.close();
			connection.close();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new SqlException(e);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}
}