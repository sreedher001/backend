package com.mindoot.onlinestore.controllers;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mindoot.onlinestore.dto.AdminOrderSummaryDto;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.payload.response.MessageResponse;
import com.mindoot.onlinestore.service.OrderService;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminOrderController.class);

	@Autowired
	private OrderService orderService;

	@GetMapping("/all-orders")
	public ResponseEntity<org.springframework.data.domain.Page<AdminOrderSummaryDto>> getOrdersByDateRange(
		@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size
	) {
		return ResponseEntity.ok(orderService.getOrdersForAdminByDateRange(startDate, endDate, page, size));
	}

	@GetMapping("/all-orders/filtered")
	public ResponseEntity<org.springframework.data.domain.Page<AdminOrderSummaryDto>> getOrdersByDateRangeAndPurchaseType(
		@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@RequestParam(name = "purchaseType", required = false) String purchaseType,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size
	) {
		return ResponseEntity.ok(orderService.getOrdersForAdminByDateRangeAndPurchaseType(startDate, endDate, purchaseType, page, size));
	}

	@PostMapping("/{id}/details")
	public ResponseEntity<AdminOrderSummaryDto> getOrderDetails(@PathVariable("id") Long id) {
		return ResponseEntity.ok(orderService.getOrderDetails(id));
	}

	@PostMapping("/{id}/status")
	public ResponseEntity<MessageResponse> updateOrderStatus(@PathVariable("id") Long id,
			@RequestParam("value") OrderStatus value) {
		OrderStatus updated = orderService.updateOrderStatus(id, value);
		return ResponseEntity.ok(new MessageResponse("Order status updated to " + updated, HttpStatus.OK));
	}

	@PostMapping("/{orderNumber}/reject")
	public ResponseEntity<MessageResponse> rejectOrder(@PathVariable String orderNumber) {
		orderService.rejectOrder(orderNumber);
		return ResponseEntity.ok(new MessageResponse("Order rejected successfully.", HttpStatus.OK));
	}
}
