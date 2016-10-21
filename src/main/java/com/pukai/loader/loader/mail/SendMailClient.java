package com.pukai.loader.loader.mail;

import java.util.Arrays;
import java.util.List;

import com.pukai.loader.loader.util.ConstantUtil;

public class SendMailClient {

	private static String username;
	
	private static String password;
	
	private static List<String> toAddressList;
	
	static {
		username = ConstantUtil.mailSendUsername;
		password = ConstantUtil.mailSendPassword;
		
		toAddressList = Arrays.asList(ConstantUtil.mailReceiveList);
	}
	
	public static void sendMail(String content){
		SimpleMailSender.sendMail(username, password, toAddressList, "reyun loader 报警邮件", content);
	}
}
