package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.PaymentVerificationRequest;
import com.mindoot.onlinestore.dto.RazorpayOrderResponse;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.OrderRepository;
import com.mindoot.onlinestore.service.EmailService;
import com.mindoot.onlinestore.service.InventoryService;
import com.mindoot.onlinestore.service.RazorpayService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class RazorpayServiceImpl implements RazorpayService {

	@Value("${razorpay.api.key}")
	private String apiKey;

	@Value("${razorpay.api.secret}")
	private String apiSecret;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private EmailService invoiceEmailService;

	@Autowired
	private InventoryService inventoryService;

	@Override
	public String createOrder(int amount, String currency, String receptId) throws RazorpayException {
		RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amount * 100);
		orderRequest.put("currency", currency);
		orderRequest.put("recept", receptId);
		com.razorpay.Order order = razorpayClient.orders.create(orderRequest);
		return order.toString();
	}

	@Override
	public RazorpayOrderResponse createRazorpayOrder(String orderNumber, User user, int amountInRupees) throws RazorpayException {
		RazorpayClient client = new RazorpayClient(apiKey, apiSecret);

		JSONObject options = new JSONObject();
		options.put("amount", amountInRupees * 100);
		options.put("currency", "INR");
		options.put("receipt", orderNumber);
		options.put("payment_capture", 1);

		com.razorpay.Order razorpayOrder = client.orders.create(options);

		return RazorpayOrderResponse.builder()
			.razorpayOrderId(razorpayOrder.get("id"))
			.currency("INR")
			.amount(amountInRupees * 100)
			.name(user.getUsername())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.key(apiKey)
			.build();
	}

	@Override
	public void verifyAndCapturePayment(PaymentVerificationRequest request) {
		try {
			String actualSignature = request.getRazorpaySignature();
			String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
			String generatedSignature = hmacSha256(payload, apiSecret);

			if (!generatedSignature.equals(actualSignature)) {
				throw new ApplicationException("Payment verification failed. Invalid signature.", HttpStatus.BAD_REQUEST);
			}

			Order order = orderRepository.findByOrderNumber(request.getOrderNumber())
				.orElseThrow(() -> new ApplicationException("Order not found", HttpStatus.BAD_REQUEST));

			order.setStatus(OrderStatus.PAID);
			order.setPaymentId(request.getRazorpayPaymentId());
			order.setPaymentDate(LocalDateTime.now());
			orderRepository.save(order);

			// Confirm inventory for each order item using variant-based stock
			order.getItems().forEach(item -> {
				if (item.getVariant() != null) {
					this.inventoryService.confirmSale(item.getVariant().getId(), item.getQuantity());
				}
			});

			this.invoiceEmailService.sendInvoiceWithPdf(order);

		} catch (RazorpayException ex) {
			throw new ApplicationException("Payment verification failed: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ApplicationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ApplicationException("Payment verification failed: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	private String hmacSha256(String payload, String secret) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
		byte[] hash = mac.doFinal(payload.getBytes("UTF-8"));
		return Hex.encodeHexString(hash);
	}
}
