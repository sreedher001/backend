package com.mindoot.onlinestore.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.StockInterest;
import com.mindoot.onlinestore.repository.StockInterestRepository;
import com.mindoot.onlinestore.service.EmailService;

import jakarta.transaction.Transactional;

@Service
public class RestockEventHandler {

	@Value("${app.mail.from}")
	private String mailFrom;

	@Value("${app.baseUrl}")
	private String baseUrl;

	@Autowired
	private StockInterestRepository stockInterestRepository;

	@Autowired
	private EmailService emailService;

	private final SpringTemplateEngine templateEngine;

	public RestockEventHandler(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@Transactional
	public void handleRestock(ProductVariant variant) {
		List<StockInterest> pendingList =
			stockInterestRepository.findByVariantIdAndNotifiedFalse(variant.getId());

		if (pendingList.isEmpty()) {
			return;
		}

		for (StockInterest interest : pendingList) {
			try {
				this.sendRestockEmail(interest.getEmail(), variant);
				interest.setNotified(true);
			} catch (Exception ex) {
				throw new ApplicationException("Failed to send restock mail", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		stockInterestRepository.saveAll(pendingList);
	}

	public void sendRestockEmail(String mail, ProductVariant variant) {
		try {
			Context context = new Context();
			context.setVariable("variantName", variant.getVariantName());
			context.setVariable("weight", variant.getWeight() + " " + variant.getUnit());

			String redirectUrl = baseUrl + "/product-details/" + variant.getId();
			context.setVariable("productRedirectUrl", redirectUrl);

			String htmlContent = templateEngine.process("restock-notification.html", context);
			String subject = "It's Back! | Spice Store";

			List<String> mailList = List.of(mail);
			emailService.sendMail(null, htmlContent, mailFrom, mailList, subject);
		} catch (Exception ex) {
			throw new ApplicationException("Failed to send restock email", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
