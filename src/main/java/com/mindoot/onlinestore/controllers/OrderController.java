package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.CancelOrderRequest;
import com.mindoot.onlinestore.dto.CheckoutRequest;
import com.mindoot.onlinestore.dto.OrderDetailResponse;
import com.mindoot.onlinestore.dto.OrderResponse;
import com.mindoot.onlinestore.dto.OrderSummaryDto;
import com.mindoot.onlinestore.security.jwt.JwtUtils;
import com.mindoot.onlinestore.service.OrderService;
import com.mindoot.onlinestore.utility.TokenUtils;
import com.mindoot.onlinestore.utility.UserInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderController.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

	/** The order service. */
	@Autowired
	private OrderService orderService;
	
	/** The token utils. */
	@Autowired
	private TokenUtils tokenUtils;

	/**
	 * Performs the checkout operation for a user.
	 * 
	 * <p>
	 * This method follows these steps:
	 * </p>
	 * <ol>
	 * <li>User sends a {@link CheckoutRequest} containing shipping address and
	 * payment mode.</li>
	 * <li>User is authenticated via JWT and their user ID is extracted.</li>
	 * <li>The cart associated with the user is fetched from the database.</li>
	 * <li>Each cart item is validated and converted into {@link OrderItem}
	 * entities.</li>
	 * <li>A new {@link Order} is created and populated with order items, total
	 * amount, and user info.</li>
	 * <li>The cart is cleared to ensure it is empty after successful checkout.</li>
	 * <li>The order is saved to the database and an {@link OrderResponse} is
	 * returned to the user.</li>
	 * </ol>
	 *
	 * @param checkoutRequest the checkout request
	 * @param authorization the authorization
	 * @return An {@link OrderResponse} object containing summary of the placed
	 *         order.
	 * @throws RuntimeException If user or cart is not found, or cart is empty.
	 */

	@PostMapping("/checkout")
	public ResponseEntity<OrderResponse> checkout(@RequestBody CheckoutRequest checkoutRequest,
			@RequestHeader("Authorization") String authorization) {
		Long userId = this.tokenUtils.getUserId(authorization);
		OrderResponse response = orderService.checkout(userId, checkoutRequest);
		return ResponseEntity.ok(response);
	}

	/**
	 * Gets the order history.
	 *
	 * @param authorization the authorization
	 * @return the order history
	 */
	@GetMapping("/history")
	public ResponseEntity<List<OrderSummaryDto>> getOrderHistory(@RequestHeader("Authorization") String authorization) {
		Long userId = this.tokenUtils.getUserId(authorization);
		List<OrderSummaryDto> history = orderService.getOrderHistory(userId);
		return ResponseEntity.ok(history);
	}

	/**
	 * Gets the order details.
	 *
	 * @param orderNumber the order number
	 * @param authorization the authorization
	 * @return the order details
	 */
	@GetMapping("/details/{orderNumber}")
	public ResponseEntity<OrderDetailResponse> getOrderDetails(@PathVariable("orderNumber") String orderNumber,
			@RequestHeader("Authorization") String authorization) {
		Long userId = this.tokenUtils.getUserId(authorization);
		OrderDetailResponse response = orderService.getOrderByOrderNumber(orderNumber, userId);
		return ResponseEntity.ok(response);
	}

	/**
	 * Cancel order.
	 *
	 * @param orderNumber the order number
	 * @param authorization the authorization
	 * @return the response entity
	 */
	@PostMapping("/cancel")
	public ResponseEntity<String> cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest,
			@RequestHeader("Authorization") String authorization) {
		//Long userId = this.tokenUtils.getUserId(authorization);
		UserInfo userInfo = this.tokenUtils.getUserInfo(authorization);
		orderService.cancelOrder(cancelOrderRequest, userInfo);
		return ResponseEntity.ok("Order cancelled successfully.");
	}

}
