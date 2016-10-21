package com.pukai.loader.loader.exception;

/**
 * hive sql异常
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class SqlException extends RuntimeException {

	private static final long serialVersionUID = 42629348300604028L;

	public SqlException(){
		super();
	}
	
	public SqlException(String str){
		super(str);
	}
	
	public SqlException(String message, Throwable cause){
		super(message, cause);
	}
	
	public SqlException(Throwable cause){
		super(cause);
	}
}
