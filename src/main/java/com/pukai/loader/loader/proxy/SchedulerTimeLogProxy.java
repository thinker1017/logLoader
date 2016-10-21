package com.pukai.loader.loader.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.pukai.loader.loader.util.LogUtil;

/**
 * 方法计时代理类
 * @author thinker
 * @date 2015-3-24
 * @version V1.0
 */
public class SchedulerTimeLogProxy implements InvocationHandler {

	private Object target;

	/**
	 * 绑定委托对象并返回一个代理类
	 * 
	 * @param target
	 * @return
	 */
	public Object bind(Object target) {
		this.target = target;
		// 取得代理对象
		return Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(), this);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		String name = getName(target);
		Logger log = LogUtil.getLogger(name);
		long start = System.currentTimeMillis();
		
		LogUtil.timeLog.info(name + " start");
		
		// 执行方法
		result = method.invoke(target, args);
		
		long time = (System.currentTimeMillis() - start) / 1000;
		LogUtil.timeLog.info("The running time of this " + name + " is : " + time + "seconds");
		log.info("The running time of this " + name + " is : " + time + "seconds");
		return result;
	}
	
	private String getName(Object obj){
		return obj.getClass().getSimpleName().replaceFirst("Scheduler", "").toLowerCase();
	}
	
}
