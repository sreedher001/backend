package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.utility.UserInfo;

@Component
public interface EmailService {

	public void sendInvoiceWithPdf(Order order) throws Exception;

	public void sendOrderCancellationNotificationToAdmin(Order order, UserInfo userInfo, String cancellationReason);
	
	public void sendOrderPlacedNotificationToUser(Order order);
	
	public void sendOrderPlacedNotificationToAdmin(Order order);

	public void orderStatusNotification(Order order);

	public void sendOtpEmail(String value, String otp, User user);

	void sendMail(Order order, String htmlContent, String from, List<String> toEmailAddressList, String subject);
	
}
