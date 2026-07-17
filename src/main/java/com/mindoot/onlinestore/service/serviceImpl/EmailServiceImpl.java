package com.mindoot.onlinestore.service.serviceImpl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.ERole;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.service.EmailService;
import com.mindoot.onlinestore.utility.PdfUtils;
import com.mindoot.onlinestore.utility.UserInfo;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	private final SpringTemplateEngine templateEngine;

	@Value("${app.mail.from}")
	private String mailFrom;

	@Value("${app.redirect.url}")
	private String appRedirectUrl;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserRepository userRepository;

	public EmailServiceImpl(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@Override
	public void sendInvoiceWithPdf(Order order) throws Exception {
		Context context = new Context();
		context.setVariables(Map.of(
			"orderId", order.getOrderNumber(),
			"customerName", order.getUser().getUsername(),
			"paymentId", order.getPaymentId(),
			"email", order.getUser().getEmail(),
			"paymentDate", order.getPaymentDate(),
			"totalAmount", order.getTotalAmount(),
			"items", order.getItems()));
		String htmlContent = templateEngine.process("invoice.html", context);

		PdfUtils pdfUtils = new PdfUtils();
		byte[] pdfBytes = pdfUtils.generatePdf(order);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(mailFrom);
		helper.setTo(order.getUser().getEmail());
		helper.setSubject("Invoice for your order #" + order.getOrderNumber());
		helper.setText(htmlContent, true);
		helper.addAttachment("invoice.pdf", new org.springframework.core.io.ByteArrayResource(pdfBytes));

		mailSender.send(message);
	}

	@Override
	public void sendOrderCancellationNotificationToAdmin(Order order, UserInfo userInfo, String reason) {
		Context context = new Context();
		context.setVariables(Map.of(
			"orderNumber", order.getOrderNumber(),
			"cancelledBy", userInfo.getName() + " (" + userInfo.getEmail() + ")",
			"role", userInfo.getRoles().contains("ROLE_ADMIN") ? "admin" : "user",
			"reason", reason,
			"cancelledAt", order.getCancelledAt().toString(),
			"items", order.getItems()));

		String htmlContent = templateEngine.process("cancel-order-notification", context);
		String subject = "Order #" + order.getOrderNumber() + " Cancelled Notification";
		List<String> adminMailList = getAllAdminMailIds();
		sendMail(order, htmlContent, mailFrom, adminMailList, subject);
	}

	@Override
	public void sendOrderPlacedNotificationToAdmin(Order order) {
		Context context = new Context();
		context.setVariable("order", order);
		context.setVariable("dashboardUrl", appRedirectUrl + "admin/order-details/");

		String htmlContent = templateEngine.process("order-placed-admin.html", context);
		String subject = "New Order - " + order.getOrderNumber();
		List<String> adminMailList = getAllAdminMailIds();
		sendMail(order, htmlContent, mailFrom, adminMailList, subject);
	}

	@Override
	public void sendOrderPlacedNotificationToUser(Order order) {
		String email = order.getUser().getEmail();
		if (email == null || email.isBlank()) {
			return;
		}

		String country = order.getShippingAddress() != null ? order.getShippingAddress().getCountry() : "";
		double totAmt = order.getTotalAmount();
		if ("India".equalsIgnoreCase(country) && order.getSubtotal() > 999) {
			totAmt = order.getTotalAmount() - (order.getShippingFee() != null ? order.getShippingFee() : 0);
		}

		Context context = new Context();
		context.setVariable("userName", order.getUser().getUsername());
		context.setVariable("orderNumber", order.getOrderNumber());
		context.setVariable("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
		context.setVariable("items", order.getItems());
		context.setVariable("totalAmount", totAmt);
		context.setVariable("shippingFee", order.getShippingFee());
		context.setVariable("shippingAddress", order.getShippingAddress());
		context.setVariable("trackingUrl", appRedirectUrl + "track/" + order.getOrderNumber());
		context.setVariable("appRedirectUrl", appRedirectUrl);
		context.setVariable("subtotal", order.getSubtotal());

		String htmlContent = templateEngine.process("order-placed-user.html", context);
		String subject = "Your Order has been placed - " + order.getOrderNumber();
		List<String> mailList = new ArrayList<>();
		mailList.add(order.getUser().getEmail());
		sendMail(order, htmlContent, mailFrom, mailList, subject);
	}

	@Override
	public void orderStatusNotification(Order order) {
		String email = order.getUser().getEmail();
		String status = order.getStatus().name().replace("_", " ");

		if (email != null && !email.isBlank()) {
			Context context = new Context();
			context.setVariable("userName", order.getUser().getUsername());
			context.setVariable("orderNumber", order.getOrderNumber());
			context.setVariable("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
			context.setVariable("orderStatus", status);
			context.setVariable("shippingAddress", order.getShippingAddress());
			context.setVariable("trackingUrl", appRedirectUrl + "/track/" + order.getOrderNumber());
			context.setVariable("appRedirectUrl", appRedirectUrl);

			String htmlContent = templateEngine.process("order-status-update.html", context);
			String subject = "Update: Your Order #" + order.getOrderNumber() + " is now " + status;
			List<String> mailList = List.of(email);
			sendMail(order, htmlContent, mailFrom, mailList, subject);
		}
	}

	@Override
	public void sendOtpEmail(String email, String otp, User user) {
		Context context = new Context();
		context.setVariable("userName", user.getUsername());
		context.setVariable("otp", otp);
		context.setVariable("appRedirectUrl", appRedirectUrl);

		String htmlContent = templateEngine.process("otp-verification.html", context);
		String subject = "Spice Store - Your OTP Code";
		sendMail(null, htmlContent, mailFrom, List.of(email), subject);
	}

	private List<String> getAllAdminMailIds() {
		List<String> adminMailList = new ArrayList<>();
		List<User> adminUsers = userRepository.findAllByRole(ERole.ROLE_ADMIN);
		adminUsers.forEach(user -> adminMailList.add(user.getEmail()));
		return adminMailList;
	}

	@Override
	public void sendMail(Order order, String htmlContent, String from, List<String> toEmailAddressList, String subject) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
			helper.setFrom(from);
			helper.setTo(toEmailAddressList.toArray(new String[0]));
			helper.setSubject(subject);
			helper.setText(htmlContent, true);
			mailSender.send(message);
		} catch (Exception e) {
			logger.error("Failed to send email: {}", e.getMessage(), e);
			throw new ApplicationException("Failed to send notification email", HttpStatus.BAD_REQUEST);
		}
	}
}
