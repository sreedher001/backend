package com.mindoot.onlinestore.service.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.mindoot.onlinestore.dto.MailDto;
import com.mindoot.onlinestore.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class NotificationServiceImpl.
 *
 * @author Chandhru
 */
@Service
public class NotificationServiceImpl {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

	/** The from. */
	@Value("${spring.mail.username}")
	private String from;
	
	/** The email service. */
	//private final JavaMailSender emailService;
	
	@Autowired
	private EmailService emailService;
	
	/** The template engine. */
	private final SpringTemplateEngine templateEngine;

	/**
	 * Instantiates a new notification service impl.
	 *
	 * @param emailSender the email sender
	 * @param templateEngine the template engine
	 */
	public NotificationServiceImpl(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
		super();
		//this.emailService = emailSender;
		this.templateEngine = templateEngine;
	}

	/**
	 * Send email.
	 *
	 * @param mailDto the mail dto
	 * @throws MessagingException the messaging exception
	 */
	
//	public void sendEmail(MailDto mailDto) throws MessagingException {
//
//		
//		MimeMessage message = emailService.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//				StandardCharsets.UTF_8.name());
//		Context context = new Context();
//		context.setVariable("data", mailDto);
//
////		helper.setFrom(mailDto.getFrom());
////		helper.setTo(mailDto.getSendToAddress());
////		helper.setSubject(mailDto.getSubject());
//
//		String html = templateEngine.process(mailDto.getTemplateName(), context);
////		helper.setText(html, true);
//		List<String> mailList = new ArrayList<>();
//		mailList.add(mailDto.getSendToAddress());
//		sendMail(null, html,mailDto.getFrom(),mailList,mailDto.getSubject());
//		//emailService.send(message);
//	}
	
public void sendEmail(MailDto mailDto) throws MessagingException {


		Context context = new Context();
		context.setVariable("data", mailDto);


		String html = templateEngine.process(mailDto.getTemplateName(), context);
		List<String> mailList = new ArrayList<>();
		mailList.add(mailDto.getSendToAddress());
		emailService.sendMail(null, html,mailDto.getFrom(),mailList,mailDto.getSubject());
		//emailService.send(message);
	}

}
