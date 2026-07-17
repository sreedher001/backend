package com.mindoot.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.PaymentOrderRequest;
import com.mindoot.onlinestore.dto.PaymentVerificationRequest;
import com.mindoot.onlinestore.dto.RazorpayOrderResponse;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.service.OrderService;
import com.mindoot.onlinestore.service.RazorpayService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	@Autowired
    private OrderService orderService;
	
	/** The token utils. */
	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private RazorpayService razorpayService;

	@PostMapping("/create-order-poc")
	public String createOrder(@RequestParam("amount") int amount, @RequestParam("currency") String currency,
			@RequestParam(name = "receptId", required = false) String receptId) {
		try {
			return razorpayService.createOrder(amount, currency, "receptId_100");
		} catch (RazorpayException e) {
			throw new ApplicationException("create order payment error", HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/create-order")
	public ResponseEntity<RazorpayOrderResponse> createOrder(@RequestBody PaymentOrderRequest request,
			@RequestHeader("Authorization") String authorization) throws RazorpayException {
		Long userId = this.tokenUtils.getUserId(authorization);
		Order order = orderService.findByOrderNumberAndUserId(request.getOrderNumber(), userId);
		System.out.println(order.toString());
		if (order == null || order.getStatus() != OrderStatus.PLACED) {
			throw new ApplicationException("Invalid or already paid order", HttpStatus.BAD_REQUEST);
		}

		RazorpayOrderResponse response = razorpayService.createRazorpayOrder(order.getOrderNumber(), order.getUser(),
				order.getTotalAmount().intValue());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/verify")
	public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerificationRequest request) {
		System.out.println("api invoked");
		System.out.println(request.toString());
	    razorpayService.verifyAndCapturePayment(request);
	    return ResponseEntity.ok("Payment verified and order marked as PAID");
	}

}
