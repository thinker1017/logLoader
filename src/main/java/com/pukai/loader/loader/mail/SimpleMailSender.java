package com.pukai.loader.loader.mail;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 简单邮件（不带附件的邮件）发送器
 */
public class SimpleMailSender {
	
	public final static int TEXT_MAIL = 0;
	
	public final static int HTML_MAIL = 1;
	
	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo 待发送的邮件的信息
	 */
	public static boolean sendTextMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo 待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 发送文本邮件
	 * @param mailServerHost 邮件服务器ip
	 * @param mailServerPort 邮件服务器端口
	 * @param validate 是否需要验证
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddress 邮件接收者地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendTextMail(String mailServerHost, String mailServerPort, boolean validate, String username, String password, String toAddress, String subject, String content){
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(mailServerHost);
		mailInfo.setMailServerPort(mailServerPort);
		mailInfo.setValidate(validate);
		mailInfo.setUserName(username);
		mailInfo.setPassword(password);
		mailInfo.setFromAddress(username);
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		return sendTextMail(mailInfo);
	}
	
	/**
	 * 发送html邮件
	 * @param mailServerHost 邮件服务器ip
	 * @param mailServerPort 邮件服务器端口
	 * @param validate 是否需要验证
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddress 邮件接收者地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendHtmlMail(String mailServerHost, String mailServerPort, boolean validate, String username, String password, String toAddress, String subject, String content){
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(mailServerHost);
		mailInfo.setMailServerPort(mailServerPort);
		mailInfo.setValidate(validate);
		mailInfo.setUserName(username);
		mailInfo.setPassword(password);
		mailInfo.setFromAddress(username);
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		return sendHtmlMail(mailInfo);
	}
	
	/**
	 * 发送邮件
	 * @param mailType 发送邮件类型
	 * @param mailServerHost 邮件服务器ip
	 * @param mailServerPort 邮件服务器端口
	 * @param validate 是否需要验证
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddress 邮件接收者地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendMail(int mailType, String mailServerHost, String mailServerPort, boolean validate, String username, String password, String toAddress, String subject, String content){
		if(0 == mailType){
			return sendTextMail(mailServerHost, mailServerPort, validate, username, password, toAddress, subject, content);
		}else if(1 == mailType){
			return sendHtmlMail(mailServerHost, mailServerPort, validate, username, password, toAddress, subject, content);
		}else{
			return sendTextMail(mailServerHost, mailServerPort, validate, username, password, toAddress, subject, content);
		}
	}
	
	/**
	 * 发送邮件，默认为需要验证
	 * @param mailType 发送邮件类型
	 * @param mailServerHost 邮件服务器ip
	 * @param mailServerPort 邮件服务器端口
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddress 邮件接收者地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendMail(int mailType, String mailServerHost, String mailServerPort, String username, String password, String toAddress, String subject, String content){
		return sendMail(mailType, mailServerHost, mailServerPort, true, username, password, toAddress, subject, content);
	}
	
	/**
	 * 发送邮件，默认为需要验证，端口为25
	 * @param mailType 发送邮件类型
	 * @param mailServerHost 邮件服务器ip
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddress 邮件接收者地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendMail(int mailType, String mailServerHost, String username, String password, String toAddress, String subject, String content){
		return sendMail(mailType, mailServerHost, "25", username, password, toAddress, subject, content);
	}
	
	/**
	 * 发送邮件，默认为需要验证，邮件服务器为smtp.xxx.com，端口为25
	 * @param mailType 发送邮件类型
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddress 邮件接收者地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendMail(int mailType, String username, String password, String toAddress, String subject, String content){
		
		return sendMail(mailType, "smtp." + username.substring(username.indexOf("@") + 1, username.length()), username, password, toAddress, subject, content);
	}
	
	/**
	 * 发送邮件，默认为需要验证，邮件服务器为smtp.xxx.com，端口为25，邮件类型为文本邮件
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddress 邮件接收者地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendMail(String username, String password, String toAddress, String subject, String content){
		return sendMail(TEXT_MAIL, username, password, toAddress, subject, content);
	}
	
	/**
	 * 批量发送邮件，默认为需要验证，邮件服务器为smtp.xxx.com，端口为25，邮件类型为文本邮件
	 * @param username 邮件发送者地址
	 * @param password 密码
	 * @param toAddressList 邮件接收者地址列表
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return boolean 邮件发送成功状态
	 */
	public static boolean sendMail(String username, String password, List<String> toAddressList, String subject, String content){
		boolean result = false;
		
		for(String toAddress : toAddressList){
			result = sendMail(username, password, toAddress, subject, content);
		}
		
		return result;
	}
}
