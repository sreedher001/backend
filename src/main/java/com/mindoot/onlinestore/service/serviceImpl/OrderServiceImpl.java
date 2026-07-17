package com.mindoot.onlinestore.service.serviceImpl;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.AdminOrderSummaryDto;
import com.mindoot.onlinestore.dto.CancelOrderRequest;
import com.mindoot.onlinestore.dto.CheckoutRequest;
import com.mindoot.onlinestore.dto.OrderDetailResponse;
import com.mindoot.onlinestore.dto.OrderItemDto;
import com.mindoot.onlinestore.dto.OrderResponse;
import com.mindoot.onlinestore.dto.OrderSummaryDto;
import com.mindoot.onlinestore.dto.ShippingAddressDTO;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.enums.PurchaseType;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.Cart;
import com.mindoot.onlinestore.model.CartItem;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.OrderItem;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.ShippingAddress;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.CartRepository;
import com.mindoot.onlinestore.repository.OrderRepository;
import com.mindoot.onlinestore.repository.ShippingAddressRepository;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.service.EmailService;
import com.mindoot.onlinestore.service.InventoryService;
import com.mindoot.onlinestore.service.OrderService;
import com.mindoot.onlinestore.utility.UserInfo;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ShippingAddressRepository shippingRepository;

	@Value("${gst.cgst.rate:9}")
	private double cgstRate;

	@Value("${gst.sgst.rate:9}")
	private double sgstRate;

	@Value("${razorpay.api.key}")
	private String razorpayKey;

	@Value("${razorpay.api.secret}")
	private String razorpaySecret;

	@Override
	@Transactional
	public OrderResponse checkout(Long userId, CheckoutRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

		Cart cart = cartRepository.findByUserId(userId)
			.orElseThrow(() -> new ApplicationException("Cart not found", HttpStatus.NOT_FOUND));

		if (cart.getItems() == null || cart.getItems().isEmpty()) {
			throw new ApplicationException("Cannot checkout empty cart", HttpStatus.BAD_REQUEST);
		}

		PurchaseType purchaseType = cart.getPurchaseType() != null ? cart.getPurchaseType() : PurchaseType.RETAIL;

		// Validate purchase type matches request
		if (request.getPurchaseType() != null && request.getPurchaseType() != purchaseType) {
			throw new ApplicationException("Purchase type mismatch", HttpStatus.BAD_REQUEST);
		}

		// Process order items
		List<OrderItem> orderItems = new ArrayList<>();
		double subtotal = 0;
		int totalQuantity = 0;

		for (CartItem cartItem : cart.getItems()) {
			ProductVariant variant = cartItem.getVariant();
			int quantity = cartItem.getQuantity();

			// Validate stock
			if (variant.getInventory() == null || variant.getInventory().getAvailableQuantity() < quantity) {
				throw new ApplicationException(
					"Insufficient stock for: " + variant.getVariantName(),
					HttpStatus.BAD_REQUEST);
			}

			// Get price based on purchase type
			double price;
			if (purchaseType == PurchaseType.WHOLESALE) {
				if (!Boolean.TRUE.equals(variant.getWholesaleEnabled())) {
					throw new ApplicationException(
						"Wholesale not available for: " + variant.getVariantName(),
						HttpStatus.BAD_REQUEST);
				}
				price = variant.getWholesalePrice();
			} else {
				price = variant.getRetailPrice();
			}

			double itemTotal = Math.ceil(price * quantity);

			OrderItem orderItem = OrderItem.builder()
				.variant(variant)
				.productName(variant.getProduct().getName())
				.price(Math.ceil(price))
				.quantity(quantity)
				.total(itemTotal)
				.variantName(variant.getVariantName())
				.sku(variant.getSku())
				.purchaseType(purchaseType)
				.build();

			orderItems.add(orderItem);
			subtotal += itemTotal;
			totalQuantity += quantity;

			// Reserve stock
			inventoryService.reserveStock(variant.getId(), quantity);
		}

		subtotal = Math.ceil(subtotal);

		// Calculate shipping (flat rate, configurable)
		double shippingFee = 0.0;
		// For now: free shipping for orders above a threshold
		if (subtotal < 999) {
			shippingFee = 50.0;
		}

		// Calculate GST (only for retail, configurable)
		double cgst = 0;
		double sgst = 0;
		if (purchaseType == PurchaseType.RETAIL) {
			cgst = Math.ceil(subtotal * cgstRate / 100);
			sgst = Math.ceil(subtotal * sgstRate / 100);
		}

		double totalAmount = subtotal + shippingFee + cgst + sgst;

		ShippingAddress shippingAddress = shippingRepository.findById(request.getShippingAddressId())
			.orElseThrow(() -> new ApplicationException("Shipping address not found", HttpStatus.NOT_FOUND));

		Order order = Order.builder()
			.orderNumber(generateOrderNumber())
			.orderDate(LocalDateTime.now())
			.paymentMode(request.getPaymentMode())
			.shippingAddress(shippingAddress)
			.subtotal(subtotal)
			.shippingFee(shippingFee)
			.cgstAmount(cgst)
			.sgstAmount(sgst)
			.totalAmount(totalAmount)
			.status(OrderStatus.PLACED)
			.purchaseType(purchaseType)
			.user(user)
			.items(orderItems)
			.build();

		orderItems.forEach(item -> item.setOrder(order));

		Order savedOrder = orderRepository.save(order);

		// Create Razorpay order
		try {
			RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);

			Map<String, Object> options = new HashMap<>();
			options.put("amount", (int)(totalAmount * 100));
			options.put("currency", "INR");
			options.put("receipt", savedOrder.getOrderNumber());
			options.put("payment_capture", 1);

			Map<String, String> notes = new HashMap<>();
			notes.put("internal_order_id", savedOrder.getId().toString());
			notes.put("user_id", userId.toString());
			options.put("notes", notes);

			JSONObject optionsJson = new JSONObject(options);

			com.razorpay.Order razorpayOrder = razorpayClient.orders.create(optionsJson);
			String razorpayOrderId = razorpayOrder.get("id");
			savedOrder.setPaymentId(razorpayOrderId);
			orderRepository.save(savedOrder);

			return buildOrderResponse(savedOrder, razorpayOrderId);
		} catch (RazorpayException e) {
			logger.error("Error creating Razorpay order", e);
			throw new ApplicationException("Razorpay order creation failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String generateOrderNumber() {
		return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	@Override
	public List<OrderSummaryDto> getOrderHistory(Long userId) {
		List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);

		return orders.stream()
			.map(order -> OrderSummaryDto.builder()
				.id(order.getId())
				.orderNumber(order.getOrderNumber())
				.orderDate(order.getOrderDate())
				.totalAmount(order.getTotalAmount())
				.paymentMode(order.getPaymentMode())
				.status(order.getStatus() != null ? order.getStatus().toString() : "UNKNOWN")
				.purchaseType(order.getPurchaseType())
				.items(getOrderItemsForSummary(order))
				.build())
			.collect(Collectors.toList());
	}

	private List<OrderItemDto> getOrderItemsForSummary(Order order) {
		if (order.getItems() == null) return new ArrayList<>();
		return order.getItems().stream()
			.map(item -> OrderItemDto.builder()
				.variantId(item.getVariant() != null ? item.getVariant().getId() : null)
				.productName(item.getProductName())
				.quantity(item.getQuantity())
				.price(item.getPrice())
				.total(item.getTotal())
				.variantName(item.getVariantName())
				.weight(item.getVariant() != null ? item.getVariant().getWeight() : null)
				.unit(item.getVariant() != null ? item.getVariant().getUnit() : null)
				.imageUrl(item.getImageUrl())
				.purchaseType(item.getPurchaseType())
				.build())
			.collect(Collectors.toList());
	}

	@Override
	public OrderDetailResponse getOrderByOrderNumber(String orderNumber, Long userId) {
		Order order = orderRepository.findByOrderNumber(orderNumber)
			.orElseThrow(() -> new ApplicationException("Order not found: " + orderNumber, HttpStatus.NOT_FOUND));

		if (!order.getUser().getId().equals(userId)) {
			throw new ApplicationException("Unauthorized to view this order", HttpStatus.UNAUTHORIZED);
		}

		List<OrderItemDto> itemDtos = order.getItems().stream()
			.map(item -> {
				ProductVariant variant = item.getVariant();
				String imageUrl = item.getImageUrl();
				if (imageUrl == null && variant != null) {
					imageUrl = variant.getImageUrl();
				}

				return OrderItemDto.builder()
					.productId(variant != null && variant.getProduct() != null ? variant.getProduct().getId() : null)
					.variantId(variant != null ? variant.getId() : null)
					.productName(item.getProductName())
					.quantity(item.getQuantity())
					.price(item.getPrice())
					.total(item.getTotal())
					.variantName(item.getVariantName())
					.weight(variant != null ? variant.getWeight() : null)
					.unit(variant != null ? variant.getUnit() : null)
					.imageUrl(imageUrl)
					.purchaseType(item.getPurchaseType())
					.build();
			})
			.collect(Collectors.toList());

		return OrderDetailResponse.builder()
			.orderNumber(order.getOrderNumber())
			.orderDate(order.getOrderDate())
			.paymentMode(order.getPaymentMode())
			.shippingAddress(buildShippingAddressDto(order.getShippingAddress()))
			.totalAmount(order.getTotalAmount())
			.subtotal(order.getSubtotal())
			.shippingFee(order.getShippingFee())
			.cgst(order.getCgstAmount())
			.sgst(order.getSgstAmount())
			.status(order.getStatus() != null ? order.getStatus().toString() : "PLACED")
			.purchaseType(order.getPurchaseType())
			.items(itemDtos)
			.build();
	}

	@Override
	public Page<AdminOrderSummaryDto> getOrdersForAdminByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

		Page<Order> orderPage = orderRepository.findByOrderDateGreaterThanEqualAndOrderDateLessThan(startDateTime, endDateTime, pageable);

		return orderPage.map(order -> AdminOrderSummaryDto.builder()
			.id(order.getId())
			.orderNumber(order.getOrderNumber())
			.username(order.getUser().getUsername())
			.orderDate(order.getOrderDate())
			.paymentMode(order.getPaymentMode())
			.shippingAddressDto(toShippingAddressDto(order.getShippingAddress()))
			.totalAmount(order.getTotalAmount())
			.status(order.getStatus())
			.purchaseType(order.getPurchaseType())
			.build());
	}

	@Override
	public Page<AdminOrderSummaryDto> getOrdersForAdminByDateRangeAndPurchaseType(LocalDate startDate, LocalDate endDate, String purchaseType, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

		Page<Order> orderPage = orderRepository.findByOrderDateGreaterThanEqualAndOrderDateLessThan(startDateTime, endDateTime, pageable);

		List<AdminOrderSummaryDto> filtered;
		if (purchaseType == null || purchaseType.isBlank()) {
			filtered = orderPage.getContent().stream()
				.map(order -> toAdminOrderSummaryDto(order))
				.collect(Collectors.toList());
		} else {
			filtered = orderPage.getContent().stream()
				.filter(order -> purchaseType.equals(order.getPurchaseType().name()))
				.map(order -> toAdminOrderSummaryDto(order))
				.collect(Collectors.toList());
		}

		return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
	}

	private AdminOrderSummaryDto toAdminOrderSummaryDto(Order order) {
		return AdminOrderSummaryDto.builder()
			.id(order.getId())
			.orderNumber(order.getOrderNumber())
			.username(order.getUser().getUsername())
			.orderDate(order.getOrderDate())
			.paymentMode(order.getPaymentMode())
			.shippingAddressDto(toShippingAddressDto(order.getShippingAddress()))
			.totalAmount(order.getTotalAmount())
			.status(order.getStatus())
			.purchaseType(order.getPurchaseType())
			.build();
	}

	@Override
	public OrderStatus updateOrderStatus(Long id, OrderStatus status) {
		Order order = orderRepository.findById(id)
			.orElseThrow(() -> new ApplicationException("Order not found", HttpStatus.BAD_REQUEST));

		order.setStatus(status);
		order.setUpdatedAt(LocalDateTime.now());
		orderRepository.save(order);
		return status;
	}

	@Override
	@Transactional
	public void cancelOrder(CancelOrderRequest cancelOrderRequest, UserInfo userInfo) {
		Order order = orderRepository.findByOrderNumber(cancelOrderRequest.getOrderNumber())
			.orElseThrow(() -> new ApplicationException("Order not found", HttpStatus.NOT_FOUND));

		if (!order.getUser().getId().equals(userInfo.getId())) {
			throw new ApplicationException("Unauthorized to cancel this order", HttpStatus.FORBIDDEN);
		}

		if (order.getStatus() != OrderStatus.PLACED) {
			throw new ApplicationException(
				String.format("Order cannot be cancelled in current status: %s", order.getStatus()),
				HttpStatus.BAD_REQUEST);
		}

		order.setStatus(OrderStatus.CANCELLED);
		order.setCancellationReason(cancelOrderRequest.getCancellationReason());
		order.setCancelledAt(LocalDateTime.now());
		order.setCancelledByAdmin(userInfo.getRoles().contains("ROLE_ADMIN"));
		orderRepository.save(order);

		releaseOrderItemsInventory(order);
		sendCancellationNotifications(order, userInfo, cancelOrderRequest.getCancellationReason());
	}

	private void releaseOrderItemsInventory(Order order) {
		order.getItems().forEach(item -> {
			if (item.getVariant() != null) {
				inventoryService.releaseReservedStock(item.getVariant().getId(), item.getQuantity());
			}
		});
	}

	private void sendCancellationNotifications(Order order, UserInfo userInfo, String reason) {
		emailService.sendOrderCancellationNotificationToAdmin(order, userInfo, reason);
	}

	@Override
	public void rejectOrder(String orderNumber) {
		Order order = orderRepository.findByOrderNumber(orderNumber)
			.orElseThrow(() -> new ApplicationException("Order not found", HttpStatus.NOT_FOUND));

		if (order.getStatus() != OrderStatus.PLACED) {
			throw new ApplicationException("Only orders in PLACED state can be rejected", HttpStatus.BAD_REQUEST);
		}

		order.setStatus(OrderStatus.REJECTED);
		orderRepository.save(order);
	}

	@Override
	public AdminOrderSummaryDto getOrderDetails(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new ApplicationException("Order not found with ID: " + orderId, HttpStatus.BAD_REQUEST));

		List<OrderItemDto> orderItems = getOrderItems(order);

		double subtotal = orderItems.stream()
			.mapToDouble(item -> Math.ceil(item.getTotal()))
			.sum();

		double shippingFee = order.getShippingFee() != null ? order.getShippingFee() : 0.0;
		double finalTotal = Math.ceil(subtotal + shippingFee);

		return AdminOrderSummaryDto.builder()
			.id(order.getId())
			.orderNumber(order.getOrderNumber())
			.username(order.getUser().getUsername())
			.orderDate(order.getOrderDate())
			.paymentMode(order.getPaymentMode())
			.shippingAddressDto(toShippingAddressDto(order.getShippingAddress()))
			.totalAmount(Math.ceil(order.getTotalAmount()))
			.status(order.getStatus())
			.purchaseType(order.getPurchaseType())
			.items(orderItems)
			.shippingFee(order.getShippingFee())
			.subTotal(subtotal)
			.finalTotal(finalTotal)
			.build();
	}

	private List<OrderItemDto> getOrderItems(Order order) {
		return order.getItems().stream()
			.map(item -> {
				ProductVariant variant = item.getVariant();
				String imageUrl = item.getImageUrl();
				if (imageUrl == null && variant != null) {
					imageUrl = variant.getImageUrl();
				}

				return OrderItemDto.builder()
					.productId(variant != null && variant.getProduct() != null ? variant.getProduct().getId() : null)
					.variantId(variant != null ? variant.getId() : null)
					.productName(item.getProductName())
					.quantity(item.getQuantity())
					.price(Math.ceil(item.getPrice()))
					.total(Math.ceil(item.getTotal()))
					.variantName(item.getVariantName())
					.weight(variant != null ? variant.getWeight() : null)
					.unit(variant != null ? variant.getUnit() : null)
					.imageUrl(imageUrl)
					.discountPercent(item.getDiscount())
					.purchaseType(item.getPurchaseType())
					.build();
			})
			.collect(Collectors.toList());
	}

	private ShippingAddressDTO buildShippingAddressDto(ShippingAddress shippingAddress) {
		if (shippingAddress == null) return null;
		return ShippingAddressDTO.builder()
			.id(shippingAddress.getId())
			.name(shippingAddress.getName())
			.country(shippingAddress.getCountry())
			.state(shippingAddress.getState())
			.pinCode(shippingAddress.getPinCode())
			.address(shippingAddress.getAddress())
			.city(shippingAddress.getCity())
			.phoneNumber(shippingAddress.getPhoneNumber())
			.isDefault(shippingAddress.isDefault())
			.build();
	}

	private ShippingAddressDTO toShippingAddressDto(ShippingAddress shippingAddress) {
		return buildShippingAddressDto(shippingAddress);
	}

	@Override
	@Transactional
	public void handlePaymentSuccess(String razorpayOrderId, String razorpayPaymentId, String internalOrderId) {
		Order order = orderRepository.findById(Long.parseLong(internalOrderId))
			.orElseThrow(() -> new ApplicationException("Order not found for internal ID", HttpStatus.NOT_FOUND));

		order.setStatus(OrderStatus.PAID);
		order.setPaymentId(razorpayPaymentId);
		order.setPaymentDate(LocalDateTime.now());

		// Clear the cart
		Cart cart = cartRepository.findByUserId(order.getUser().getId())
			.orElseThrow(() -> new ApplicationException("Cart not found", HttpStatus.NOT_FOUND));
		cart.getItems().clear();
		cartRepository.save(cart);

		this.sendOrderNotifications(order);
		orderRepository.save(order);
	}

	private void sendOrderNotifications(Order order) {
		emailService.sendOrderPlacedNotificationToUser(order);
		emailService.sendOrderPlacedNotificationToAdmin(order);
	}

	@Override
	public long getTotalOrdersCount() {
		return orderRepository.countAllActiveOrders();
	}

	@Override
	public long getRetailOrdersCount() {
		return orderRepository.countRetailOrders();
	}

	@Override
	public long getWholesaleOrdersCount() {
		return orderRepository.countWholesaleOrders();
	}

	@Override
	public Double getTotalRevenue() {
		Double revenue = orderRepository.sumAllActiveSales();
		return revenue != null ? revenue : 0.0;
	}

	@Override
	public Double getRetailRevenue() {
		Double revenue = orderRepository.sumRetailSales();
		return revenue != null ? revenue : 0.0;
	}

	@Override
	public Double getWholesaleRevenue() {
		Double revenue = orderRepository.sumWholesaleSales();
		return revenue != null ? revenue : 0.0;
	}

	@Override
	public Order findByOrderNumberAndUserId(String orderNumber, Long userId) {
		return orderRepository.findByOrderNumberAndUserId(orderNumber, userId)
			.orElseThrow(() -> new ApplicationException("Order not found", HttpStatus.NOT_FOUND));
	}

	private OrderResponse buildOrderResponse(Order order, String razorpayOrderId) {
		List<OrderItemDto> itemDtos = order.getItems() != null ?
			order.getItems().stream()
				.map(item -> {
					ProductVariant variant = item.getVariant();
					return OrderItemDto.builder()
						.variantId(variant != null ? variant.getId() : null)
						.productName(item.getProductName())
						.quantity(item.getQuantity())
						.price(item.getPrice())
						.total(item.getTotal())
						.variantName(item.getVariantName())
						.weight(variant != null ? variant.getWeight() : null)
						.unit(variant != null ? variant.getUnit() : null)
						.imageUrl(item.getImageUrl())
						.purchaseType(item.getPurchaseType())
						.build();
				})
				.collect(Collectors.toList()) : new ArrayList<>();

		return OrderResponse.builder()
			.orderId(order.getId())
			.orderNumber(order.getOrderNumber())
			.orderDate(order.getOrderDate())
			.totalAmount(order.getTotalAmount())
			.paymentMode(order.getPaymentMode())
			.subtotal(order.getSubtotal())
			.shippingFee(order.getShippingFee())
			.cgst(order.getCgstAmount())
			.sgst(order.getSgstAmount())
			.status(order.getStatus())
			.purchaseType(order.getPurchaseType())
			.razorPayOrderId(razorpayOrderId)
			.currency("INR")
			.name(order.getUser() != null ? order.getUser().getUsername() : null)
			.email(order.getUser() != null ? order.getUser().getEmail() : null)
			.phoneNumber(order.getUser() != null ? order.getUser().getPhoneNumber() : null)
			.apikey(razorpayKey)
			.amount(order.getTotalAmount())
			.items(itemDtos)
			.build();
	}
}
